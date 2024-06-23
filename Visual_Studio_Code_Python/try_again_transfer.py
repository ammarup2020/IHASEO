import pandas as pd
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from pytorch_tabnet.tab_model import TabNetRegressor
import joblib

# Load the dataset
df = pd.read_csv('Dataset_Ampere.csv')

# Convert non-numeric columns to numeric or drop them
for column in df.columns:
    if df[column].dtype == 'object':
        try:
            df[column] = pd.to_numeric(df[column])
        except ValueError:
            df = df.drop(column, axis=1)

# Handle missing values
df.fillna(df.mean(), inplace=True)

# Assuming 'Cost' is the new target variable
X = df.drop(['Cost', 'use'], axis=1)
y = df['Cost']  # New target variable "Cost"

# Feature Scaling
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Split the dataset
X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.3, random_state=42)

# Reshape y_train to make it 2D
y_train = y_train.values.reshape(-1, 1)

# Load pre-trained TabNet model
base_model = joblib.load('energy_model_tabnet.pkl')

# Reshape y_test to match the shape of y_train
y_test = y_test.values.reshape(-1, 1)

# Train the TabNet model with the new target variable "Cost"
base_model.fit(X_train, y_train, eval_set=[(X_test, y_test)], patience=10)  # Add patience parameter for early stopping

# Save the model with the new target variable
joblib.dump(base_model, 'energy_model_tabnet_cost.pkl')

# Evaluate the model
y_pred = base_model.predict(X_test)
mse = mean_squared_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)
print(f"Mean Squared Error: {mse}")
print(f"R^2 Score: {r2}")
