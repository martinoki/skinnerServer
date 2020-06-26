Comandos para utilizar:

Reentrenar la red:
python retrain.py --bottleneck_dir=./bottlenecks --model_dir=./inception --output_labels=./retrained_labels.txt --output_graph=./retrained_graph.pb --image_dir=./training_images/

Identificar imagen:

python label_image.py --graph=./retrained_graph.pb --labels=./retrained_labels.txt --image=./ISIC_0000000.jpg