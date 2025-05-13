package com.snackstack.server.service.llm;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snackstack.server.dto.RecipeGenerationDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.LLMServiceException;
import com.snackstack.server.service.RecipeGenerator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MockRecipeGenerator implements RecipeGenerator {

  private final List<RecipeResponseDTO> hardcodedRecipes;
  private final Gson gson;

  public MockRecipeGenerator(Gson gson) {
    this.gson = gson;
    this.hardcodedRecipes = loadHardcodedRecipes();
  }

  @Override
  public List<RecipeResponseDTO> generateRecipe(RecipeGenerationDTO recipeRequest)
      throws LLMServiceException {
    // Simply return copies of all recipes with new UUIDs
    return hardcodedRecipes.stream()
        .map(recipe -> new RecipeResponseDTO(
            UUID.randomUUID().toString(),
            recipe.recipeName(),
            recipe.servings(),
            recipe.description(),
            recipe.originName(),
            recipe.recipeIngredients(),
            recipe.recipeSteps()
        ))
        .collect(Collectors.toList());
  }

  @Override
  public String getProviderName() {
    return "MockRecipeProvider";
  }

  private List<RecipeResponseDTO> loadHardcodedRecipes() {
    String recipesJson = """
                [
        {
            "recipeName": "Grilled Chicken with Roasted Vegetables and Rice",
            "servings": 2,
            "description": "A simple and flavorful main dish featuring grilled chicken, roasted vegetables, and a side of fluffy rice.",
            "originName": "",
            "recipeIngredients": [
                {
                    "ingredientName": "Chicken breast",
                    "quantity": 300.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Rice",
                    "quantity": 250.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Carrots",
                    "quantity": 150.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Onions",
                    "quantity": 100.0,
                    "unit": "grams",
                    "note": "Chopped."
                },
                {
                    "ingredientName": "Olive oil",
                    "quantity": 20.0,
                    "unit": "ml"
                }
            ],
            "recipeSteps": [
                "Preheat grill to medium-high heat.",
                "Season the chicken with salt and cook until browned on both sides, then set aside.",
                "In a separate pan, heat olive oil over medium heat. Add chopped onions and cook until softened.",
                "Add sliced carrots to the pan and cook for an additional 5 minutes.",
                "Serve the grilled chicken with roasted vegetables and a side of fluffy rice."
            ]
        },
        {
            "recipeName": "Chicken and Vegetable Stir-Fry",
            "servings": 2,
            "description": "A quick and easy main dish featuring sautéed chicken, mixed vegetables, and a hint of garlic powder.",
            "originName": "",
            "recipeIngredients": [
                {
                    "ingredientName": "Chicken breast",
                    "quantity": 300.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Carrots",
                    "quantity": 150.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Onions",
                    "quantity": 100.0,
                    "unit": "grams",
                    "note": "Chopped."
                },
                {
                    "ingredientName": "Spinach",
                    "quantity": 50.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Garlic powder",
                    "quantity": 0.5,
                    "unit": "teaspoon"
                }
            ],
            "recipeSteps": [
                "Heat olive oil in a large skillet over medium-high heat.",
                "Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.",
                "Add the chicken to the pan and cook until browned on both sides and cooked through.",
                "Stir in garlic powder and wilted spinach. Serve hot."
            ]
        },
        {
            "recipeName": "Chicken and Vegetable Fried Rice",
            "servings": 2,
            "description": "A flavorful main dish featuring sautéed chicken, mixed vegetables, and a side of fried rice.",
            "originName": "",
            "recipeIngredients": [
                {
                    "ingredientName": "Chicken breast",
                    "quantity": 300.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Rice",
                    "quantity": 250.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Carrots",
                    "quantity": 150.0,
                    "unit": "grams"
                },
                {
                    "ingredientName": "Onions",
                    "quantity": 100.0,
                    "unit": "grams",
                    "note": "Chopped."
                },
                {
                    "ingredientName": "Canned tomatoes",
                    "quantity": 150.0,
                    "unit": "grains"
                }
            ],
            "recipeSteps": [
                "Heat olive oil in a large skillet over medium-high heat.",
                "Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.",
                "Add the chicken to the pan and cook until browned on both sides and cooked through.",
                "Stir in canned tomatoes and cooked rice. Serve hot."
            ]
        }
        ]
        """;

    try {
      Type recipeListType = new TypeToken<ArrayList<RecipeResponseDTO>>() {
      }.getType();
      return gson.fromJson(recipesJson, recipeListType);
    } catch (Exception e) {
      // In a real application, you might want to log this error
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}