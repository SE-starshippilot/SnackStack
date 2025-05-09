package com.snackstack.server.service.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.LLMServiceException;
import com.snackstack.server.service.RecipeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MockRecipeGenerator implements RecipeGenerator {

  private final List<RecipeResponseDTO> hardcodedRecipes;
  private final ObjectMapper objectMapper;

  public MockRecipeGenerator() {
    this.objectMapper = new ObjectMapper();
    this.hardcodedRecipes = loadHardcodedRecipes();
  }

  @Override
  public List<RecipeResponseDTO> generateRecipe(RecipeRequestDTO recipeRequest)
      throws LLMServiceException {
    // For a mock implementation, we'll just return all recipes
    // In a real implementation, you might filter based on ingredients or other criteria
    return hardcodedRecipes.stream()
        .map(recipe -> new RecipeResponseDTO(
            UUID.randomUUID().toString(),
            recipe.recipe_name(),
            recipe.servings(),
            recipe.description(),
            recipe.origin_name(),
            recipe.recipe_ingredients(),
            recipe.recipe_steps()
        ))
        .collect(Collectors.toList());
  }

  @Override
  public String getProviderName() {
    return "MockRecipeProvider";
  }

  private List<RecipeResponseDTO> loadHardcodedRecipes() {
    String recipesJson = """
        [{
        "recipe_name":"Grilled Chicken with Roasted Vegetables and Rice",
        "servings":2,
        "description":"A simple and flavorful main dish featuring grilled chicken, roasted vegetables, and a side of fluffy rice.","origin_name":"","recipe_ingredients":[{"ingredient_name":"Chicken breast","quantity":300.0,"unit":"grams"},{"ingredient_name":"Rice","quantity":250.0,"unit":"grams"},{"ingredient_name":"Carrots","quantity":150.0,"unit":"grams"},{"ingredient_name":"Onions","quantity":100.0,"unit":"grams","note":"Chopped."},{"ingredient_name":"Olive oil","quantity":20.0,"unit":"ml"}],"recipe_steps":["Preheat grill to medium-high heat.","Season the chicken with salt and cook until browned on both sides, then set aside.","In a separate pan, heat olive oil over medium heat. Add chopped onions and cook until softened.","Add sliced carrots to the pan and cook for an additional 5 minutes.","Serve the grilled chicken with roasted vegetables and a side of fluffy rice."]},{"recipe_name":"Chicken and Vegetable Stir-Fry","servings":2,"description":"A quick and easy main dish featuring sautéed chicken, mixed vegetables, and a hint of garlic powder.","origin_name":"","recipe_ingredients":[{"ingredient_name":"Chicken breast","quantity":300.0,"unit":"grams"},{"ingredient_name":"Carrots","quantity":150.0,"unit":"grams"},{"ingredient_name":"Onions","quantity":100.0,"unit":"grams","note":"Chopped."},{"ingredient_name":"Spinach","quantity":50.0,"unit":"grams"},{"ingredient_name":"Garlic powder","quantity":0.5,"unit":"teaspoon"}],"recipe_steps":["Heat olive oil in a large skillet over medium-high heat.","Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.","Add the chicken to the pan and cook until browned on both sides and cooked through.","Stir in garlic powder and wilted spinach. Serve hot."]},{"recipe_name":"Chicken and Vegetable Fried Rice","servings":2,"description":"A flavorful main dish featuring sautéed chicken, mixed vegetables, and a side of fried rice.","origin_name":"","recipe_ingredients":[{"ingredient_name":"Chicken breast","quantity":300.0,"unit":"grams"},{"ingredient_name":"Rice","quantity":250.0,"unit":"grams"},{"ingredient_name":"Carrots","quantity":150.0,"unit":"grams"},{"ingredient_name":"Onions","quantity":100.0,"unit":"grams","note":"Chopped."},{"ingredient_name":"Canned tomatoes","quantity":150.0,"unit":"grains"}],"recipe_steps":["Heat olive oil in a large skillet over medium-high heat.","Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.","Add the chicken to the pan and cook until browned on both sides and cooked through.","Stir in canned tomatoes and cooked rice. Serve hot."]}]
        """;

    try {
      // Using a TypeReference to handle the List<RecipeResponseDTO> generic type
      return objectMapper.readValue(recipesJson, new TypeReference<List<RecipeResponseDTO>>() {
      });
    } catch (JsonProcessingException e) {
      // In a real application, you might want to log this error
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}