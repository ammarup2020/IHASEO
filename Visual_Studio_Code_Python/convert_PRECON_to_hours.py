import pandas as pd
import os

# Function to process each CSV file
def process_csv(file_path):
    # Read CSV into DataFrame
    df = pd.read_csv(file_path)

    # Rename column 'Usage_kW' to 'use'
    df.rename(columns={'Usage_kW': 'use'}, inplace=True)

    # Add 'Cost' column
    df['Cost'] = 55 * df['use']

    # Convert 'Date_Time' column to datetime type
    #df['Date_Time'] = pd.to_datetime(df['Date_Time'])

    # Round 'Date_Time' to the nearest hour
    #df['Date_Time'] = df['Date_Time'].dt.round('H')

    # Aggregate data per hour
    #df = df.groupby('Date_Time').sum()

    # Write back to CSV
    df.to_csv(file_path, index=True)

# Directory containing CSV files
folder_path = 'PRECON'

# Iterate over each CSV file in the directory
for file_name in os.listdir(folder_path):
    if file_name.endswith('.csv'):
        file_path = os.path.join(folder_path, file_name)
        process_csv(file_path)
