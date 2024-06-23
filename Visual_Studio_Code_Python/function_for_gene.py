import joblib
import numpy as np
from deap import base, creator, tools, algorithms
import pickle

import pandas as pd
from sklearn.discriminant_analysis import StandardScaler

# Function to load the pre-trained TabNet model
def load_model(model_path):
    return joblib.load(model_path)

# Function to preprocess input data
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

# Function to configure the genetic algorithm
def configure_genetic_algorithm():
    creator.create("FitnessMax", base.Fitness, weights=(-1.0,))
    creator.create("Individual", list, fitness=creator.FitnessMax)
    toolbox = base.Toolbox()

    toolbox.register("attr_bool", np.random.randint, 0, 24)  # 24 hours
    toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_bool, n=4)  # 4 devices

    def initPopulationWithDiversity(icls, params):
        population = []
        for _ in range(params.population_size):
            individual = icls()
            for _ in range(params.n_devices):
                # Represent activation time and duration for each device
                device_activation = (np.random.randint(0, 24), np.random.randint(1, 6))  # (start time, duration)
                individual.append(device_activation)
            population.append(individual)
        return population

    toolbox.register("population", initPopulationWithDiversity, creator.Individual, toolbox)
    toolbox.register("mate", tools.cxTwoPoint)
    toolbox.register("mutate", tools.mutUniformInt, low=0, up=23, indpb=0.9)
    toolbox.register("select", tools.selTournamentDCD, k=3, fitness_attr='fitness')  # Tournament selection with replacement (DEAP >= 1.3.0)
    toolbox.register("evaluate", fitness_function)

    return toolbox

# Function to run the genetic algorithm
def run_genetic_algorithm(toolbox, population_size=5000, num_generations=105):
    population = toolbox.population(n=population_size)
    algorithms.eaSimple(population, toolbox, cxpb=0.2, mutpb=0.9, ngen=num_generations, verbose=True)
    best_individuals = tools.selBest(population, k=10)
    return best_individuals


# Function to define the fitness function for the genetic algorithm
def fitness_function(individual, model, input_data):
    total_energy_consumption = 0
    total_cost = 0
    for device_index, (start_time, duration) in enumerate(individual):
        activation_times = np.array([start_time] * duration)  # Same start time for duration of activation
        energy_consumption = model.predict(input_data, activation_times)
        total_energy_consumption += energy_consumption
        # Assume cost is directly proportional to duration and priority
        device_priority = input_data[device_index][-1]  # Assuming priority is the last feature in input_data
        device_cost = duration * device_priority
        total_cost += device_cost
    # Return negative of total energy consumption and cost as we want to minimize them
    return -total_energy_consumption, -total_cost


# Main function to use the genetic algorithm as a model
def optimize_device_activation_times(device_data):
    # Load pre-trained model
    model = load_model('your_model.pkl')  # Update with your model file path

    # Preprocess input data
    input_data = prepare_input_data(device_data)

    # Configure genetic algorithm
    toolbox = configure_genetic_algorithm()

    # Run genetic algorithm
    best_individuals = run_genetic_algorithm(toolbox, population_size=5000, num_generations=105)

    return best_individuals


# Example Device Data
device_data = [
    ['Fridge', 1],
    ['AC', 2],
    ['Lights', 4],
    ['Fans', 4],
    ['Dishwasher', 1]
]

# Example usage
best_individuals = optimize_device_activation_times(device_data)
for i, ind in enumerate(best_individuals):
    print(f"Solution {i+1}: {ind}, Fitness: {ind.fitness.values[0]}")
