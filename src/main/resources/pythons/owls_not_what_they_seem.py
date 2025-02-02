from transformers import pipeline
from PIL import Image

def beach_check(search_terms, image_path):
    detector = pipeline("zero-shot-object-detection", model="google/owlv2-base-patch16-ensemble", device="mps")

    image = Image.open(image_path)

    predictions = detector(image=image, candidate_labels=search_terms)

    return predictions
