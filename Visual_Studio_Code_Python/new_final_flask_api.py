from flask import Flask, request, jsonify
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler
from pytorch_tabnet.tab_model import TabNetRegressor
import joblib

app = Flask(__name__)

# Load your trained model
model = joblib.load("energy_model_tabnet_new.pkl")

# Function to preprocess data
def preprocess_data(device_data):
    df = pd.DataFrame(device_data, columns=['Device', 'Duration'])
    # Drop 'Device' column and return duration values
    return df['Duration'].values.reshape(-1, 1)

# Function to optimize turn-on times and return best solutions
def optimize_predictions(X):
    # Scale features
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    
    # Predict usage for each device
    predicted_usage = model.predict(X_scaled)

    # Sort devices based on predicted usage
    sorted_indices = np.argsort(predicted_usage)[::-1]  # Descending order

    # Get the top 10 devices with highest predicted usage
    top_10_indices = sorted_indices[:10]

    # Initialize a dictionary to store turn-on times for each device
    turn_on_times = {}
    for idx in top_10_indices:
        device_name = device_data[idx][0]
        turn_on_times[device_name] = None

    # Simulated optimization: assign turn-on times based on dynamic programming
    # Here, we use a simple greedy approach as an example
    # Replace this with your more sophisticated optimization algorithm

    # Suppose we have 24 hours in a day
    total_hours = 24
    # Initialize a matrix to store cost values
    dp = np.zeros((len(top_10_indices), total_hours + 1))
    # Iterate over each device
    for i, idx in enumerate(top_10_indices):
        duration = device_data[idx][1]
        # Calculate energy cost for each possible turn-on time
        for j in range(1, total_hours + 1):
            # Assume energy cost is proportional to usage and duration
            cost = predicted_usage[idx] * duration * j
            # Update cost matrix
            dp[i][j] = cost
    # Find the optimal turn-on time for each device
    for i, idx in enumerate(top_10_indices):
        device_name = device_data[idx][0]
        # Find the turn-on time with minimum cost
        optimal_turn_on_time = np.argmin(dp[i][1:]) + 1  # Add 1 to account for 0-indexing
        # Store optimal turn-on time
        turn_on_times[device_name] = optimal_turn_on_time

    # Prepare response data
    response_data = []
    for device_name, turn_on_time in turn_on_times.items():
        duration = device_data[top_10_indices[i]][1]  # Get duration for the device
        response_data.append({
            'device_name': device_name,
            'turn_on_time': turn_on_time,
            'duration': duration
        })

    return response_data

@app.route('/predict', methods=['POST'])
def predict():
    # Parse the JSON data
    device_data = request.json.get('device_data')

    # Preprocess data
    X = preprocess_data(device_data)

    # Optimize turn-on times
    best_solutions = optimize_predictions(X)

    return jsonify(best_solutions)

if __name__ == '__main__':
    # Start Flask application
    app.run(debug=True)
