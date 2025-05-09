package com.snackstack.server;

import com.google.gson.Gson;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.service.llm.MockRecipeGenerator;
import java.util.List;
import org.junit.Test;

public class MockLLMTest {

  private final MockRecipeGenerator mockLLM = new MockRecipeGenerator();
  private final Gson gson = new Gson();

  @Test
  public void testGeneration() {
    List<RecipeResponseDTO> recipes = mockLLM.generateRecipe(null);
    System.out.println(gson.toJson(recipes));
  }
}
