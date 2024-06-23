import numpy as np
from flask import Flask, request, jsonify
import joblib
import pandas as pd
from sklearn.preprocessing import StandardScaler
from pytorch_tabnet.tab_model import TabNetRegressor

app = Flask(__name__)

# Function to load the pre-trained TabNet model
def load_model(model_path):
    return joblib.load(model_path)

# Function to preprocess input data
def prepare_input_data(device_data):
    df = pd.DataFrame(device_data, columns=['Device_Name', 'Device_Priority'])
    df_encoded = pd.get_dummies(df, columns=['Device_Name'])
    scaler = StandardScaler()
    df_scaled = scaler.fit_transform(df_encoded)
    return df_scaled

# Function to generate initial population
def generate_population(population_size, n_devices):
    return np.random.randint(24, size=(population_size, n_devices, 2))  # Each device has (start_time, duration)

# Function to evaluate fitness of an individual
def evaluate_fitness(individual, model, input_data):
    total_energy_consumption = 0
    total_cost = 0
    
    for device_info in individual:
        start_time, duration = device_info
        
        activation_times = np.array([start_time] * duration)
        activation_times = activation_times.reshape(1, -1)  # Reshape to 2D
        
        energy_consumption = model.predict(input_data, activation_times)
        total_energy_consumption += energy_consumption
        
        device_index = np.where(individual == device_info)[0][0]
        device_priority = input_data[device_index][-1]
        device_cost = duration * device_priority
        total_cost += device_cost
    
    return -total_energy_consumption, -total_cost

# Function to select parents based on fitness
def select_parents(population, fitness_scores):
    # Roulette wheel selection
    fitness_probs = fitness_scores / np.sum(fitness_scores)
    selected_indices = np.random.choice(len(population), size=len(population), p=fitness_probs)
    return population[selected_indices]

# Function to perform crossover between parents
def crossover(parent1, parent2):
    crossover_point = np.random.randint(1, len(parent1))
    child1 = np.concatenate((parent1[:crossover_point], parent2[crossover_point:]))
    child2 = np.concatenate((parent2[:crossover_point], parent1[crossover_point:]))
    return child1, child2

# Function to perform mutation on individuals
def mutate(individual, mutation_rate):
    mutated_individual = individual.copy()
    for device_info in mutated_individual:
        if np.random.rand() < mutation_rate:
            device_info[0] = np.random.randint(24)  # Mutate start time
            device_info[1] = np.random.randint(1, 24)  # Mutate duration
    return mutated_individual

# Main function to run the genetic algorithm
def run_genetic_algorithm(population_size, num_generations, input_data, model):
    n_devices = input_data.shape[0]
    population = generate_population(population_size, n_devices)
    
    for generation in range(num_generations):
        fitness_scores = np.array([evaluate_fitness(individual, model, input_data) for individual in population])
        parents = select_parents(population, fitness_scores)
        offspring = []
        
        for i in range(0, len(parents), 2):
            child1, child2 = crossover(parents[i], parents[i+1])
            offspring.extend([mutate(child1, mutation_rate=0.1), mutate(child2, mutation_rate=0.1)])
        
        population = np.array(offspring)
    
    # Select the best individuals from the final generation
    fitness_scores = np.array([evaluate_fitness(individual, model, input_data) for individual in population])
    best_individual_indices = np.argsort(fitness_scores[:, 0])[:10]  # Top 10 individuals based on energy consumption
    best_individuals = population[best_individual_indices]
    return best_individuals

# Main function to optimize device activation times
def optimize_device_activation_times(device_data):
    model = load_model('energy_model_tabnet_new.pkl')  # Update with your model file path
    input_data = prepare_input_data(device_data)
    population_size = 500
    num_generations = 105
    best_individuals = run_genetic_algorithm(population_size, num_generations, input_data, model)
    return best_individuals

# Endpoint to receive device data and return optimized device activation times
@app.route('/optimize', methods=['POST'])
def optimize():
    device_data = request.json['device_data']
    best_individuals = optimize_device_activation_times(device_data)
    optimized_results = []
    for i, individual in enumerate(best_individuals):
        device_results = []
        for device_index, (start_time, duration) in enumerate(individual):
            device_results.append({
                'device_index': device_index,
                'activation_time': start_time,
                'duration': duration,
            })
        optimized_results.append({
            'solution': i+1,
            'devices': device_results,
        })
    return jsonify(optimized_results)

if __name__ == '__main__':
    app.run(debug=True)
