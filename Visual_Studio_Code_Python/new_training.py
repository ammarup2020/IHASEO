import os
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from pytorch_tabnet.tab_model import TabNetRegressor
import joblib

# Function to load and preprocess data
def load_data(file_path):
    df = pd.read_csv(file_path)
    # Preprocess data as needed (e.g., handle missing values, encode categorical variables)
    # For simplicity, let's assume preprocessing involves selecting relevant columns and dropping missing values
    df = df[['Date_Time', 'Usage_kW']]  # Assuming only interested in 'Date_Time' and 'Usage_kW' columns
    
    # Convert 'Date_Time' column to datetime
    df['Date_Time'] = pd.to_datetime(df['Date_Time'])
    
    # Extract numerical features from 'Date_Time'
    df['year'] = df['Date_Time'].dt.year
    df['month'] = df['Date_Time'].dt.month
    df['day'] = df['Date_Time'].dt.day
    df['hour'] = df['Date_Time'].dt.hour
    df['minute'] = df['Date_Time'].dt.minute
    
    # Drop 'Date_Time' column as it's no longer needed for modeling
    df.drop(columns=['Date_Time'], inplace=True)
    
    # Drop rows with missing values
    df.dropna(inplace=True)
    
    return df


# Function to train model
def train_model(data, pre_trained_model=None):
    X = data.drop(columns=['Usage_kW'])  # Features
    y = data['Usage_kW'].values.reshape(-1, 1)  # Target reshaped to 2D array

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Feature scaling
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    # If a pre-trained model is provided, load it
    if pre_trained_model:
        model = pre_trained_model
    else:
        model = TabNetRegressor()

    # Continue training the model on the new dataset for 2 epochs
    model.fit(
        X_train_scaled, 
        y_train, 
        eval_set=[(X_test_scaled, y_test)], 
        patience=10
    )

    return model



from sklearn.metrics import mean_absolute_error, mean_squared_error


# Function to evaluate model
def evaluate_model(model, X_test, y_test):
    # Predict on test data
    y_pred = model.predict(X_test)
   
    # Evaluate model on test data
    score = model.score(X_test, y_test)
    return score

# Function to save model
def save_model(model, file_path):
    joblib.dump(model, file_path)

# Main function
def main():
    # Directory containing CSV files
    folder_path = "PRECON"

    # Load and preprocess data from all CSV files
    all_data = pd.DataFrame()
    for file_name in os.listdir(folder_path):
        if file_name.endswith(".csv"):
            file_path = os.path.join(folder_path, file_name)
            data = load_data(file_path)
            all_data = pd.concat([all_data, data])

    # Load pre-trained model
    pre_trained_model = joblib.load("energy_model_tabnet_cost.pkl")

    # Train model
    model = train_model(all_data, pre_trained_model)


    # Save model
    save_model(model, "energy_model_tabnet_new.pkl")

    # Split data into features (X) and target (y)
    X_test = all_data.drop(columns=['Usage_kW'])
    y_test = all_data['Usage_kW'].values.reshape(-1, 1)  # Reshape to 2D array

    # Evaluate model
    accuracy = evaluate_model(model, X_test, y_test)
    print("Model accuracy:", accuracy)
    

    # Save model
    #save_model(model, "energy_model_tabnet_new.pkl")

if __name__ == "__main__":
    main()


