# -*- coding: utf-8 -*-
"""
Chef.M_K - Master Recipe Dataset Generator (1000 Items)
Professional Offline Culinary Database for Protein Stores
Generates 1000 authentic recipes with exhaustive, numbered step-by-step instructions.
"""

import json
import os
import sys

# Import category generators
from chicken_gen import generate_chicken
from kebab_gen import generate_kebab
from steak_gen import generate_steak
from burger_gen import generate_burger
from sauce_gen import generate_sauce
from spice_gen import generate_spice
from butter_gen import generate_butter
from oil_gen import generate_oil
from salad_gen import generate_salad
from appetizer_gen import generate_appetizer
from fingerfood_gen import generate_fingerfood
from dessert_gen import generate_dessert

# Import professional instruction builder
from instruction_builder import enhance_recipe_steps

def build_master_dataset():
    all_recipes = []
    seen_ids = set()
    seen_names = set()
    category_counts = {}

    def add_recipe(recipe):
        uid = recipe["uniqueId"]
        name = recipe["name"]
        cat = recipe["category"]

        if uid in seen_ids:
            raise ValueError(f"Duplicate ID found: {uid}")
        if name in seen_names:
            raise ValueError(f"Duplicate Name found: {name}")

        # Inject exhaustive, numbered, recipe-specific step-by-step instructions
        steps = enhance_recipe_steps(recipe)
        recipe["preparationSteps"] = steps

        # Strict quality checks
        assert len(recipe["ingredients"]) >= 2, f"Recipe {name} has fewer than 2 ingredients"
        assert len(recipe["preparationSteps"]) >= 6, f"Recipe {name} has fewer than 6 detailed stages! Actual: {len(recipe['preparationSteps'])}"
        
        # Verify no generic or short steps exist
        for idx, step in enumerate(recipe["preparationSteps"]):
            assert len(step.strip()) >= 50, f"Step {idx+1} in recipe {uid} ({name}) is too short! Text: {step}"
            assert "مرحله" in step, f"Step {idx+1} in recipe {uid} must be explicitly numbered with مرحله"
            assert "Mix all ingredients together" not in step
            assert "Marinate for several hours" not in step
            assert "Cook until done" not in step
            assert "Serve immediately" not in step

        assert recipe["cookingMethod"], f"Missing cooking method in {name}"
        assert recipe["professionalTips"], f"Missing professional tips in {name}"
        assert recipe["commonMistakes"], f"Missing common mistakes in {name}"
        assert recipe["foodSafetyAlert"], f"Missing food safety alert in {name}"
        assert recipe["storeUsage"], f"Missing store usage in {name}"
        assert recipe["storageInstructions"], f"Missing storage instructions in {name}"

        seen_ids.add(uid)
        seen_names.add(name)
        all_recipes.append(recipe)
        category_counts[cat] = category_counts.get(cat, 0) + 1

    print("Generating chicken recipes (target: 150)...")
    for r in generate_chicken():
        add_recipe(r)

    print("Generating kebab recipes (target: 120)...")
    for r in generate_kebab():
        add_recipe(r)

    print("Generating steak recipes (target: 100)...")
    for r in generate_steak():
        add_recipe(r)

    print("Generating burger recipes (target: 100)...")
    for r in generate_burger():
        add_recipe(r)

    print("Generating sauce recipes (target: 150)...")
    for r in generate_sauce():
        add_recipe(r)

    print("Generating spice recipes (target: 100)...")
    for r in generate_spice():
        add_recipe(r)

    print("Generating butter recipes (target: 50)...")
    for r in generate_butter():
        add_recipe(r)

    print("Generating oil recipes (target: 40)...")
    for r in generate_oil():
        add_recipe(r)

    print("Generating salad recipes (target: 70)...")
    for r in generate_salad():
        add_recipe(r)

    print("Generating appetizer recipes (target: 50)...")
    for r in generate_appetizer():
        add_recipe(r)

    print("Generating finger food recipes (target: 40)...")
    for r in generate_fingerfood():
        add_recipe(r)

    print("Generating dessert recipes (target: 30)...")
    for r in generate_dessert():
        add_recipe(r)

    total = len(all_recipes)
    print(f"\nTotal recipes generated: {total}")
    for cat, count in category_counts.items():
        print(f"  - {cat}: {count}")

    assert total == 1000, f"Expected exactly 1000 recipes, got {total}"

    # Write to app/src/main/assets/recipes.json
    assets_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
    os.makedirs(assets_dir, exist_ok=True)
    out_path = os.path.join(assets_dir, "recipes.json")

    print(f"\nWriting master dataset to {out_path}...")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(all_recipes, f, ensure_ascii=False, indent=2)

    file_size_mb = os.path.getsize(out_path) / (1024 * 1024)
    print(f"Successfully generated {out_path} ({file_size_mb:.2f} MB)")
    print("ALL 1000 RECIPES VALIDATED WITH DETAILED MULTI-STAGE STEP-BY-STEP INSTRUCTIONS!")

if __name__ == "__main__":
    build_master_dataset()
