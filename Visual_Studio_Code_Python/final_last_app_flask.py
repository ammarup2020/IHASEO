from flask import Flask, json, request, jsonify
from datetime import datetime, timedelta
from flask_cors import CORS
import random

app = Flask(__name__)
CORS(app)

# Device energy consumption data
device_energy = {
    "Refrigerator": 2,
    "AC": 3.5,
    "Fan": 0.0311,
    "Light": 0.6,
    "Washing Machine": 1.4,
    "Microwave": 1,
    "Tv": 0.2,
    "Iron": 1.1
}

# Function to calculate cost for a day
def calculate_cost_for_day(plan_for_day):
    total_cost = 0
    for entry in plan_for_day:
        total_cost += entry['cost_per_day']
    return total_cost

# Randomized starting time and duration for a device
def randomize_device_plan(device, location):
    location = location
    start_time = datetime.strptime('{}:{}'.format(random.randint(0, 23), random.randint(0, 59)), '%H:%M')
    duration = random.randint(1, 4)  # Random duration between 1 and 6 hours
    cost_per_unit = device_energy.get(device, 0)  # Get the energy consumption per unit
    cost_per_day = 65 * cost_per_unit * duration  # Calculate cost per day

    return {
        'device': device+" "+location,
        'start_time': start_time.strftime('%H:%M'),
        'duration': duration,
        'cost_per_day': cost_per_day
    }

# Randomized plan for a day
def randomize_day_plan(device_data):
    day_plan = []
    for device_info in device_data:
        device_name = device_info[0]  # Extract device name from device_info
        device_location = device_info[1] #Extract device Location from device_info
        day_plan.append(randomize_device_plan(device_name, device_location))
    return day_plan

# API endpoint for receiving device data and total cost constraint
@app.route('/plan', methods=['POST'])
def generate_plan_for_30_days():
    input_data = request.json
    device_data = input_data.get('device_data')
    total_cost_constraint = input_data.get('total_cost_constraint')

    if not device_data or not total_cost_constraint:
        return "Please provide device data and total cost constraint."

    total_cost = 0
    plan = {}
    day_number = 1
    while total_cost < total_cost_constraint and day_number <= 30:
        day_plan = randomize_day_plan(device_data)  # Pass device_data here
        day_cost = calculate_cost_for_day(day_plan)
        if total_cost + day_cost <= total_cost_constraint:
            plan[f"Day{day_number}"] = day_plan
            total_cost += day_cost
            day_number += 1
        else:
            break

    # Save plan to JSON file
    result = {"plan": plan, "total_cost_30_days": total_cost}
    with open('result.json', 'w') as outfile:
        json.dump(result, outfile)

    return jsonify(result)

if __name__ == '__main__':
    app.run(debug=True)
