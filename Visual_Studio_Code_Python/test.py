import pandas as pd
import numpy as np
from geneticalgorithm import geneticalgorithm as ga
from datetime import datetime

def calculate_distance(timestamp1, timestamp2):
    # Convert timestamps to datetime objects
    time_format = "%Y-%m-%d %H:%M:%S"
    datetime1 = datetime.strptime(timestamp1, time_format)
    datetime2 = datetime.strptime(timestamp2, time_format)
    
    # Calculate the absolute difference in seconds
    return abs((datetime1 - datetime2).total_seconds())

def calculate_energy_consumption(device, timestamp):
    # Function to calculate energy consumption based on the device and timestamp
    # You need to implement this based on your specific requirements
    # For demonstration, let's assume energy consumption is constant for each device type
    if device == 'light':
        return 50  # Assuming 50 units of energy consumption for each light
    elif device == 'fan':
        return 100  # Assuming 100 units of energy consumption for each fan
    elif device == 'ac':
        return 2000  # Assuming 2000 units of energy consumption for each AC
    elif device == 'fridge':
        return 500  # Assuming 500 units of energy consumption for each fridge
    elif device == 'washing_machine':
        return 1000  # Assuming 1000 units of energy consumption for each washing machine
    else:
        return 0  # Assuming 0 units of energy consumption for unknown devices

def generate_energy_matrices(data, num_devices):
    num_locations = len(data)
    distance_matrix = np.zeros((num_locations, num_locations))
    energy_matrix = np.zeros((num_locations, num_locations))

    for i in range(num_locations):
        for j in range(num_locations):
            distance_matrix[i, j] = calculate_distance(data['Time'][i], data['Time'][j])
            # Calculate energy consumption between locations i and j
            energy_matrix[i, j] = sum([calculate_energy_consumption(device, data['Time'][i]) for device in range(num_devices)])

    return distance_matrix, energy_matrix

def fitness_function(chromosome, distance_matrix, energy_matrix):
    total_distance = 0
    total_energy = 0
    for i in range(len(chromosome) - 1):
        total_distance += distance_matrix[chromosome[i], chromosome[i+1]]
        total_energy += energy_matrix[chromosome[i], chromosome[i+1]]
    total_distance += distance_matrix[chromosome[-1], chromosome[0]]
    total_energy += energy_matrix[chromosome[-1], chromosome[0]]
    return -(total_distance + total_energy)

def generate_optimized_plans(data, num_devices, num_days=30):
    distance_matrix, energy_matrix = generate_energy_matrices(data, num_devices)
    num_locations = len(data)

    # Initialize genetic algorithm model
    model = ga(function=fitness_function, dimension=num_locations, variable_type='int', variable_boundaries=[[0, num_locations-1]]*num_locations)

    best_plans = []
    for _ in range(num_days):
        model.run()

        best_chromosome = model.output_dict['variable']
        total_distance = 0
        total_energy = 0
        for i in range(len(best_chromosome) - 1):
            total_distance += distance_matrix[best_chromosome[i], best_chromosome[i+1]]
            total_energy += energy_matrix[best_chromosome[i], best_chromosome[i+1]]
        total_distance += distance_matrix[best_chromosome[-1], best_chromosome[0]]
        total_energy += energy_matrix[best_chromosome[-1], best_chromosome[0]]

        best_plans.append((best_chromosome, total_distance, total_energy))

    return best_plans

def main():
    # Interface: Get user input about devices
    # For demonstration, let's assume the user provides the number of devices of each type
    num_lights = int(input("Enter number of lights: "))
    num_fans = int(input("Enter number of fans: "))
    num_acs = int(input("Enter number of ACs: "))
    num_fridges = int(input("Enter number of fridges: "))
    num_washing_machines = int(input("Enter number of washing machines: "))

    num_devices = num_lights + num_fans + num_acs + num_fridges + num_washing_machines

    # Load the dataset
    data = pd.read_csv("Dataset_Ampere.csv")

    # Generate optimized plans for 30 days
    optimized_plans = generate_optimized_plans(data, num_devices)

    # Output the optimized plans
    for i, (chromosome, total_distance, total_energy) in enumerate(optimized_plans):
        print(f"Plan {i+1}:")
        print("Best route:", chromosome)
        print("Total distance:", total_distance)
        print("Total energy consumption:", total_energy)
        print()

    # User selects a plan (for demonstration, let's assume the user selects the first plan)
    selected_plan_index = 0
    selected_plan = optimized_plans[selected_plan_index]
    print("Selected plan:")
    print("Best route:", selected_plan[0])
    print("Total distance:", selected_plan[1])
    print("Total energy consumption:", selected_plan[2])

if __name__ == "__main__":
    main()
