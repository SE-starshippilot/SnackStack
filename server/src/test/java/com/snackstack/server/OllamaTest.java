package com.snackstack.server;

import com.google.gson.Gson;
import com.snackstack.server.config.OllamaConfig;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.service.llm.OllamaRecipeGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class OllamaTest {

  private final Gson gson = new Gson();
  private final OllamaConfig config = new OllamaConfig("http://10.147.20.184:11434/",
      "src/main/resources/system_prompt.md", "gemma2:2b");

  @Test
  public void testOllama() {
    OllamaRecipeGenerator ollamaRecipeGenerator = new OllamaRecipeGenerator(config, gson);
    List<String> ingredients = new ArrayList<>();
    List<String> meal_origin  = new ArrayList<>();
    List<String> allergies = new ArrayList<>();
    List<RecipeResponseDTO> recipes = new ArrayList<>();
    ingredients.add("Carrots");
    ingredients.add("Spinach");
    ingredients.add("Onions");
    ingredients.add("Apples");
    ingredients.add("Bananas");
    ingredients.add("Chicken breast");
    ingredients.add("Rice");
    ingredients.add("Milk");
    ingredients.add("Salt");
    ingredients.add("Olive oil");
    ingredients.add("Sugar");
    ingredients.add("Almonds");
    ingredients.add("Canned tomatoes");
    ingredients.add("Garlic powder");
    ingredients.add("Eggs");

    RecipeRequestDTO sampleRequest = new RecipeRequestDTO(
      ingredients,
        2,
        "Main",
        meal_origin,
        allergies
    );
    recipes = ollamaRecipeGenerator.generateRecipe(sampleRequest);
    System.out.println(gson.toJson(recipes));
  }
}
