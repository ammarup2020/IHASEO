import pandas as pd
from sklearn.model_selection import train_test_split
import pickle

# Define function to load and preprocess data for a single house
def load_house_data(filepath):
  data = pd.read_csv(filepath)
  # Extract features (assuming "Usage_kW" is the target)
  features = list(data.columns)
  features.remove("Date_Time")
  features.remove("Usage_kW")  # Assuming "Usage_kW" is the target
  X = data[features]
  y = data["Usage_kW"]
  return X, y

# Load the pre-trained TabNet model
with open("energy_model_tabnet_cost.pkl", "rb") as f:
  model = pickle.load(f)

# Load data from all houses
all_X = []
all_y = []
for i in range(1, 43):  # Assuming house files are House1.csv to House42.csv
  house_path = f"PRECON/House{i}.csv"
  X, y = load_house_data(house_path)
  all_X.append(X)
  all_y.append(y)

# Combine data from all houses
X = pd.concat(all_X, ignore_index=True)
y = pd.concat(all_y, ignore_index=True)

# Split data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

# Train the model (assuming TabNet takes features and target as separate arguments)
model.fit(
    X_train.values,  # Convert pandas dataframe to numpy array for TabNet
    y_train.values
)

# Evaluate model accuracy (replace with your desired metric)
y_pred = model.predict(X_test.values)
from sklearn.metrics import mean_squared_error
accuracy = mean_squared_error(y_test, y_pred)
print(f"Model accuracy (MSE): {accuracy}")

# Save the updated model
with open("updated_energy_model.pkl", "wb") as f:
  pickle.dump(model, f)

print("Model retraining and evaluation complete!")
