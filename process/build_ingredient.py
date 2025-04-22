import os
import pandas as pd
import spacy
import ast
import argparse
import csv
from tqdm import tqdm
from concurrent.futures import ThreadPoolExecutor, as_completed

def simplify_noun_phrase(line: pd.Series, nlp):
    # Process the text
    raw_ingredients_list = ast.literal_eval(line['ingredients'])
    processed_ingredients = set()
    for ingredient in raw_ingredients_list:
        doc = nlp(ingredient)
    
        # Find noun chunks (noun phrases)
        for chunk in doc.noun_chunks:
            # Get the root/head of the noun phrase
            head = chunk.root
            
            # Find essential modifiers
            essential_modifiers = []
            for token in chunk:
                if token.dep_ in ["compound", "amod"] and token.i < head.i:
                    if token.text.lower() in ["olive", "kitchen", "leather", "steel"]:
                        essential_modifiers.append(token)
            
            # Build simplified phrase
            simplified = " ".join([token.text for token in essential_modifiers]) + " " + head.text
            simplified = simplified.strip()
            processed_ingredients.add(simplified)
    return processed_ingredients
    

def parse_args():
    parser = argparse.ArgumentParser(description="Process product names.")
    parser.add_argument("-i", "--input", type=str, required=True, help="Input CSV file")
    parser.add_argument("-o", "--output", type=str, required=True, help="Output CSV file")
    return parser.parse_args()

def main():
    nlp = spacy.load("en_core_web_trf")
    args = parse_args()
    # Load the CSV file
    if not (os.path.exists(args.input) and os.path.isfile(args.input) and args.input.endswith('.csv')):
        print(f"File {args.input} does not exist.")
        return
    df = pd.read_csv(args.input, usecols=["ingredients"])
    
    ingredients = set()
    output = ["ingredients"]
    
    with ThreadPoolExecutor(max_workers=4) as executor:
        # Submit each row for processing.
        futures = [executor.submit(simplify_noun_phrase, row, nlp) for _, row in df.iterrows()]
        for future in tqdm(as_completed(futures), total=len(futures), desc="Processing rows"):
            ingredients.update(future.result())
            
    ingredients = sorted(list(ingredients))
    # Write the output to a CSV file
    with open(args.output, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(output)
        for ingredient in ingredients:
            writer.writerow([ingredient])
            
    print(f"Processed {len(ingredients)} unique ingredients.")
    print(f"Output written to {args.output}")
    
if __name__ == "__main__":
    main()
