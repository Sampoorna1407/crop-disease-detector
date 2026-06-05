-- src/main/resources/data.sql

-- Tomato Diseases
-- Diseases
INSERT INTO diseases (id, name, crop_type, description, severity, avg_red_value, avg_green_value, avg_brown_value)
VALUES (1, 'Early Blight', 'tomato', 'Fungal disease causing dark spots with concentric rings on lower leaves.', 'Moderate', 0.45, 0.35, 0.15);

INSERT INTO diseases (id, name, crop_type, description, severity, avg_red_value, avg_green_value, avg_brown_value)
VALUES (2, 'Late Blight', 'tomato', 'Devastating disease causing rapid plant death in wet conditions.', 'Severe', 0.35, 0.25, 0.25);

INSERT INTO diseases (id, name, crop_type, description, severity, avg_red_value, avg_green_value, avg_brown_value)
VALUES (3, 'Potato Scab', 'potato', 'Bacterial disease causing rough, corky patches on tubers.', 'Low', 0.5, 0.4, 0.2);


-- Symptoms (ElementCollection table)
INSERT INTO symptoms (disease_id, symptoms) VALUES (1, 'Dark brown spots with concentric rings');
INSERT INTO symptoms (disease_id, symptoms) VALUES (1, 'Yellowing around spots');
INSERT INTO symptoms (disease_id, symptoms) VALUES (1, 'Lower leaves affected first');

INSERT INTO symptoms (disease_id, symptoms) VALUES (2, 'Water-soaked lesions on leaves');
INSERT INTO symptoms (disease_id, symptoms) VALUES (2, 'White fuzzy growth on leaf undersides');
INSERT INTO symptoms (disease_id, symptoms) VALUES (2, 'Rapid browning and plant collapse');

INSERT INTO symptoms (disease_id, symptoms) VALUES (3, 'Raised, rough patches on skin');
INSERT INTO symptoms (disease_id, symptoms) VALUES (3, 'Brown corky lesions');


-- Treatments
INSERT INTO treatments (id, name, type, description, application_method, frequency, disease_id)
VALUES (1, 'Copper Fungicide', 'CHEMICAL', 'Apply copper-based fungicide to affected areas', 'Spray on leaves', 'Every 7-10 days', 1);

INSERT INTO treatments (id, name, type, description, application_method, frequency, disease_id)
VALUES (2, 'Neem Oil Solution', 'ORGANIC', 'Natural fungicide that prevents spread', 'Spray diluted solution', 'Every 5-7 days', 1);

INSERT INTO treatments (id, name, type, description, application_method, frequency, disease_id)
VALUES (3, 'Chlorothalonil', 'CHEMICAL', 'Preventive fungicide for late blight control', 'Foliar spray', 'Every 5-7 days during wet weather', 2);

INSERT INTO treatments (id, name, type, description, application_method, frequency, disease_id)
VALUES (4, 'Soil pH Adjustment', 'PREVENTIVE', 'Lower soil pH to below 5.5', 'Apply sulfur to soil', 'Before planting', 3);

