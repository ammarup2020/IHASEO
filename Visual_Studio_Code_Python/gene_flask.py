from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
import joblib
from pytorch_tabnet.tab_model import TabNetRegressor
from deap import base, creator, tools, algorithms
from sklearn.discriminant_analysis import StandardScaler

# Load Pre-trained TabNet Model
def load_model(model_path):
  return joblib.load(model_path)

# Preprocess Input Data
def prepare_input_data(device_data):
  # Convert device data to DataFrame
  df = pd.DataFrame(device_data, columns=['Device_Name', 'Energy_Consumption', 'Total_Same_Type_Devices'])

  # Perform any necessary preprocessing steps
  # For example, you can encode categorical variables
  # Here, we'll one-hot encode the 'Device_Name' column
  df_encoded = pd.get_dummies(df, columns=['Device_Name'])

  # Scale numerical features
  scaler = StandardScaler()
  df_scaled = scaler.fit_transform(df_encoded)

  return df_scaled


# Define the fitness function
def fitness_function(individual, model):
    activation_times = np.array(individual)
    total_energy_consumption = np.dot(activation_times)
    
    # Predict additional energy consumption using the pre-trained model
    additional_energy = model.predict(np.expand_dims(activation_times, axis=0))

    # Return negative of total energy consumption plus additional predicted energy
    return -(total_energy_consumption + additional_energy[0]),





app = Flask(__name__)

model = load_model('energy_model_tabnet_cost.pkl')


@app.route('/predict', methods=['POST'])
def predict():
    # Parse input data from the request
    device_data = request.json  # Assuming JSON data with an array of devices

    # Prepare input data for the genetic algorithm
    input_data = prepare_input_data(device_data)


    # Genetic Algorithm Configuration (using selTournamentDCD for newer DEAP)
    creator.create("FitnessMax", base.Fitness, weights=(-1.0,))
    creator.create("Individual", list, fitness=creator.FitnessMax)
    toolbox = base.Toolbox()

    # Increased mutation rate for higher diversity
    toolbox.register("attr_bool", np.random.randint, 0, 24)  # 24 hours
    toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_bool, n=4)  # 4 devices

    # Seeding population with some randomness for diversity
    def initPopulationWithDiversity(icls, params):
        population = []
        for _ in range(params.population_size):
            individual = icls(toolbox.attr_bool() for _ in range(params.n))
            # Introduce some random values (0 or 23)
            for i in range(len(individual)):
                if np.random.rand() < 0.2:
                    individual[i] = np.random.randint(0, 24)
            # Add values from normal distribution around mean (e.g., mean=12, std=3)
            individual += np.random.normal(loc=12, scale=3, size=params.n).astype(int).clip(min=0, max=23)
            population.append(individual)
        return population


    toolbox.register("population", initPopulationWithDiversity, creator.Individual, toolbox)
    toolbox.register("mate", tools.cxTwoPoint)
    # Adjusted mutation rate
    toolbox.register("mutate", tools.mutUniformInt, low=0, up=23, indpb=0.9)
    toolbox.register("select", tools.selTournamentDCD, k=3, fitness_attr='fitness')  # Tournament selection with replacement (DEAP >= 1.3.0)
    #toolbox.register("evaluate", fitness_function)

    # Genetic Algorithm Parameters
    population_size = 5000
    num_generations = 105

    # Register the fitness function with the DEAP toolbox
    toolbox.register("evaluate", fitness_function, model=model)

    # Execute the genetic algorithm
    population = toolbox.population(n=population_size)
    algorithms.eaSimple(population, toolbox, cxpb=0.2, mutpb=0.9, ngen=num_generations, verbose=True)

    # Select the best 10 individuals
    best_individuals = tools.selBest(population, k=10)
    solutions = []
    for ind in best_individuals:
        solutions.append({
            "activation_times": ind,
            "fitness": ind.fitness.values[0]
        })

    return jsonify(solutions)

if __name__ == '__main__':
    app.run(debug=True)
