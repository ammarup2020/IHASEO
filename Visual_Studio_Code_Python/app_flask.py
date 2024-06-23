from flask import Flask, request, jsonify
import pickle
import numpy as np

model = pickle.load(open('energy_model_tabnet_cost.pkl', 'rb'))

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    
    #Device Name
    Dev_name = request.form.get('name')
    #Device Location
    Dev_loc = request.form.get('location')
    #Device Priority
    Dev_per = request.form.get('priority')

    #Step 1 name = Device name + in + Device Location e.g name = Fridge in Kitchen
    #Step 2


    result = 5

    return jsonify(result)
    
