package com.snackstack.server.llmtest;

import com.google.gson.Gson;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.service.llm.MockRecipeGenerator;
import java.util.List;
import org.junit.Test;

public class MockLLMTest {

  private final Gson gson = new Gson();
  private final MockRecipeGenerator mockLLM = new MockRecipeGenerator(gson);

  @Test
  public void testGeneration() {
    List<RecipeResponseDTO> recipes = mockLLM.generateRecipe(null);
    System.out.println(gson.toJson(recipes));
  }
}
