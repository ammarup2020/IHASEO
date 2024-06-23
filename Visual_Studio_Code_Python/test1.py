import random
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score
from deap import base, creator, tools, algorithms
#from sklearn.externals import joblib
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

# Assuming 'Cost' and 'Energy Consumption' are the targets, and they need to be minimized.
X = df.drop(['Cost', 'use'], axis=1)
y = df[['Cost', 'use']]

# Define the columns based on the provided image
#features_columns = ['use','gen','Dishwasher','Furnace 1','Furnace 2','Home office','Fridge','Wine cellar','Garage door','Kitchen 12','Kitchen 14','Kitchen 38','Barn','Well','Microwave','Living room','Solar','temperature','humidity','visibility','apparentTemperature','pressure','windSpeed','cloudCover','windBearing','precipIntensity','dewPoint','precipProbability']
#target_columns = ['Cost','Energy Consumption']

# Use the columns to define X and y
#X = df[features_columns]
#y = df[target_columns]

# Feature Scaling
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# Split the dataset
X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.3, random_state=42)

# Train the model
model = RandomForestRegressor(n_estimators=100)
model.fit(X_train, y_train)

# Save the model
joblib.dump(model, 'energy_model.pkl')

# Evaluate the model
y_pred = model.predict(X_test)
mse = mean_squared_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)
print(f"Mean Squared Error: {mse}")
print(f"R^2 Score: {r2}")

# Define Genetic Algorithm components
creator.create("FitnessMin", base.Fitness, weights=(-1.0, -1.0)) # Minimize both cost and energy
creator.create("Individual", list, fitness=creator.FitnessMin)

toolbox = base.Toolbox()
toolbox.register("attr_float", random.uniform, 0, 1)
toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_float, n=len(X.columns))
toolbox.register("population", tools.initRepeat, list, toolbox.individual)

def evalEnergy(individual):
    # Convert individual to a DataFrame row
    individual_df = pd.DataFrame([individual], columns=X.columns)
    individual_scaled = scaler.transform(individual_df)
    
    # Make prediction using the trained model
    prediction = model.predict(individual_scaled)[0]
    
    # The fitness function returns the predicted cost and energy consumption
    return prediction

toolbox.register("evaluate", evalEnergy)
toolbox.register("mate", tools.cxTwoPoint)
toolbox.register("mutate", tools.mutGaussian, mu=0, sigma=1, indpb=0.1)
toolbox.register("select", tools.selTournament, tournsize=3)

# Genetic Algorithm parameters
population = toolbox.population(n=300)
NGEN=50
CXPB=0.7
MUTPB=0.2

# Run the genetic algorithm
for gen in range(NGEN):
    offspring = algorithms.varAnd(population, toolbox, cxpb=CXPB, mutpb=MUTPB)
    fits = toolbox.map(toolbox.evaluate, offspring)
    for fit, ind in zip(fits, offspring):
        ind.fitness.values = fit
    population = toolbox.select(offspring, k=len(population))

top10 = tools.selBest(population, k=10)
for i, ind in enumerate(top10):
    print(f"Solution {i+1}: {ind}, Fitness: {ind.fitness.values}")

# Convert each Individual object to a list of values
top10_lists = [list(ind) for ind in top10]

# Create a DataFrame from the list of lists
solutions_df = pd.DataFrame(top10_lists, columns=X.columns)
solutions_df['Cost'], solutions_df['Energy Consumption'] = zip(*[ind.fitness.values for ind in top10])
solutions_df.to_csv('optimal_solutions.csv', index=False)

print("Top 10 optimized solutions saved to 'optimal_solutions.csv'.")
# Print the top ten optimal solutions
#for i, ind in enumerate(top10):
#    print(f"Solution {i+1}: {ind}, Fitness: {fitness_values[ind]}")