from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
from PIL import Image
import os

app = Flask(__name__)

# Load model
MODEL_PATH = "crop_model.keras"

if not os.path.exists(MODEL_PATH):
    raise Exception("Model file not found! Place crop_model.keras in ml-api folder.")

model = tf.keras.models.load_model(MODEL_PATH)

# Class labels (UPDATE according to your dataset)
classes = [
    "Tomato_healthy",
    "Tomato_Late_blight",
    "Potato_healthy",
    "Potato_Early_blight",
    "Rice_Bacterial_leaf_blight",
    "Corn_Common_rust",
    "Wheat_healthy",
    "Jute_disease",
    "Barley_disease",
    "Millet_disease",
    "Brinjal_leaf_spot",
    "Cauliflower_black_rot",
    "Tomato_mosaic_virus",
    "Potato_late_blight",
    "Rice_brown_spot"
]

# Preprocess image
def preprocess(image):
    image = image.resize((224, 224))
    image = np.array(image) / 255.0

    if image.shape[-1] == 4:  # RGBA → RGB
        image = image[:, :, :3]

    image = np.expand_dims(image, axis=0)
    return image

@app.route("/", methods=["GET"])
def home():
    return "🌱 Crop Disease Detection API Running!"

# Prediction API
@app.route("/predict", methods=["POST"])
def predict():
    try:
        if "image" not in request.files:
            return jsonify({"error": "No image uploaded"}), 400

        file = request.files["image"]
        img = Image.open(file).convert("RGB")

        processed = preprocess(img)
        prediction = model.predict(processed)

        index = int(np.argmax(prediction))
        confidence = float(np.max(prediction))

        disease_name = classes[index]

        return jsonify({
            "diseaseName": disease_name,
            "confidence": confidence,
            "description": f"Detected {disease_name} using AI model",
            "symptoms": [
                "Leaf discoloration",
                "Spots or lesions",
                "Wilting"
            ],
            "treatments": [
                {
                    "name": "Organic Spray",
                    "description": "Use neem oil or organic pesticide"
                },
                {
                    "name": "Chemical Treatment",
                    "description": "Apply recommended fungicide"
                }
            ]
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(debug=True, port=5000)