package com.snackstack.server.llmtest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snackstack.server.config.AppConfig;
import com.snackstack.server.config.OpenAIConfig;
import com.snackstack.server.dto.RecipeGenerationDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.model.RecipeType;
import com.snackstack.server.service.llm.LLMProvider;
import com.snackstack.server.service.llm.OpenAIRecipeGenerator;
import com.snackstack.server.utils.InstantTypeAdapter;
import java.time.Instant;
import java.util.List;
import org.junit.Test;

public class OpenAiTest {

  private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
      .create();
  private final LLMProvider provider = LLMProvider.OPENAI;
  private final AppConfig appConfig = AppConfig.getInstance();
  private final OpenAIConfig openAIConfig = appConfig.configOpenAi();
  private final OpenAIRecipeGenerator generator = new OpenAIRecipeGenerator(openAIConfig, gson);


  @Test
  public void testGeneration() {
    List<String> ingredients = List.of(
        "spaghetti",
        "minced pork",
        "onion",
        "lettuce",
        "egg",
        "milk",
        "clam",
        "carrot",
        "celery",
        "bacon",
        "mushroom"
    );
    int servings = 2;
    RecipeType type = RecipeType.MAIN;
    List<String> mealOrigin = List.of(
        "Italia"
    );
    List<String> allergies = List.of();
    RecipeGenerationDTO genDTO = new RecipeGenerationDTO(
        ingredients,
        servings,
        type,
        mealOrigin,
        allergies
    );
    List<RecipeResponseDTO> recipes = generator.generateRecipe(genDTO);
    System.out.println(gson.toJson(recipes));
  }
}
