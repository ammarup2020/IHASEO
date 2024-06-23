from flask import Flask, request, jsonify
from datetime import datetime
import random

app = Flask(__name__)

# Device energy consumption data
device_energy = {
    "Refrigerator": 2,  # Energy consumption in kW
    "AC": 3.5,           # Energy consumption in kW
    "Fan": 0.0311,       # Energy consumption in kW
    # Add other devices with their energy consumption
}

# Function to calculate total cost for a day based on the device plan
def calculate_total_cost_for_day(day_plan):
    total_cost = 0
    for entry in day_plan:
        total_cost += entry['cost_per_day']
    return total_cost

# Randomized starting time and duration for a device
def randomize_device_plan(device):
    start_time = datetime.strptime('{}:{}'.format(random.randint(0, 23), random.randint(0, 59)), '%H:%M')
    duration = random.randint(1, 4)  # Random duration between 1 and 4 hours
    energy_consumption = device_energy.get(device, 0)  # Get the energy consumption per unit
    cost_per_day = 65 * energy_consumption * duration  # Calculate cost per day

    return {
        'device': device,
        'start_time': start_time.strftime('%H:%M'),
        'duration': duration,
        'cost_per_day': cost_per_day
    }

# Randomized plan for a day
def randomize_day_plan(device_data):
    day_plan = []
    for device, _ in device_data:
        device_plan = randomize_device_plan(device)
        day_plan.append(device_plan)
    return day_plan

# Function to evaluate fitness of a schedule
def evaluate_fitness(schedule):
    total_cost = 0
    for day_plan in schedule:
        total_cost += calculate_total_cost_for_day(day_plan)
    return total_cost

# Genetic Algorithm
def genetic_algorithm(device_data, total_cost_constraint, population_size=100, generations=100):
    population = [randomize_day_plan(device_data) for _ in range(population_size)]

    for _ in range(generations):
        population = sorted(population, key=lambda x: evaluate_fitness(x))
        if evaluate_fitness(population[0]) <= total_cost_constraint:
            return population[0], evaluate_fitness(population[0])
        # Implement genetic operators: selection, crossover, mutation, updating population

    return population[0], evaluate_fitness(population[0])  # Return the best schedule found

# API endpoint for receiving device data and total cost constraint
@app.route('/generate_schedule', methods=['POST'])
def generate_schedule():
    data = request.json
    device_data = data.get('device_data')
    total_cost_constraint = data.get('total_cost_constraint')

    # Check if device data and total cost constraint are provided
    if not device_data or not total_cost_constraint:
        return jsonify({"error": "Please provide device data and total cost constraint."}), 400

    # Check if device data is in the correct format
    if not isinstance(device_data, list) or not all(isinstance(device, list) and len(device) == 2 for device in device_data):
        return jsonify({"error": "Device data should be a list of lists with device name and quantity."}), 400

    # Check if total cost constraint is a positive number
    if not isinstance(total_cost_constraint, (int, float)) or total_cost_constraint <= 0:
        return jsonify({"error": "Total cost constraint should be a positive number."}), 400

    best_schedule, total_cost = genetic_algorithm(device_data, total_cost_constraint)
    
    return jsonify({"best_schedule": best_schedule, "total_cost": total_cost})

if __name__ == '__main__':
    app.run(debug=True)
