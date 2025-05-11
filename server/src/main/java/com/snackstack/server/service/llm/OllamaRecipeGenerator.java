package com.snackstack.server.service.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.snackstack.server.config.OllamaConfig;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.LLMServiceException;
import com.snackstack.server.service.RecipeGenerator;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.types.OllamaModelType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OllamaRecipeGenerator implements RecipeGenerator {

  private final OllamaAPI api;
  private final String systemPrompt;
  private final Gson gson;
  private final String model;
  private static final Logger logger = LoggerFactory.getLogger(OllamaRecipeGenerator.class);

  public OllamaRecipeGenerator(OllamaConfig config, Gson gson) {
    this.gson = gson;
    this.systemPrompt = config.getSystemPrompt();
    this.model = config.getModel();
    this.api = new OllamaAPI(config.getHostURL());
    this.api.setVerbose(false);
  }


  @Override
  public List<RecipeResponseDTO> generateRecipe(RecipeRequestDTO recipeRequest)
      throws LLMServiceException {
    try {
      String userPrompt = gson.toJson(recipeRequest);
      logger.info("Generated user prompt: {}", userPrompt);
      OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(
          OllamaModelType.LLAMA3_1);
      OllamaChatRequest request = builder
          .withMessage(OllamaChatMessageRole.SYSTEM, systemPrompt)
          .withMessage(OllamaChatMessageRole.USER, userPrompt)
          .build();

      OllamaChatResult result = api.chat(request);

      if (result == null || result.getResponseModel() == null) {
        throw new LLMServiceException("Failed to get a response from the LLM.");
      }

      // Parse the response content
      String responseContent = result.getResponseModel().getMessage().getContent();
      JsonObject responseJson = gson.fromJson(responseContent, JsonObject.class);

      boolean success = responseJson.has("success") && responseJson.get("success").getAsBoolean();
      if (!success) {
        String errorMessage =
            responseJson.has("message") ? responseJson.get("message").getAsString()
                : "Unknown error occurred.";
        throw new LLMServiceException("Recipe generation failed: " + errorMessage);
      }

      // Deserialize the "recipes" array into a list of RecipeResponseDTO
      return gson.fromJson(
          responseJson.getAsJsonArray("recipes"),  // JSON array of recipes
          new com.google.gson.reflect.TypeToken<List<RecipeResponseDTO>>() {
          }.getType()
      );

    } catch (JsonSyntaxException e) {
      logger.error("Failed to parse response from LLM.", e);
      throw new LLMServiceException("Failed to parse LLM response: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Unexpected error during recipe generation.", e);
      throw new LLMServiceException("Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public String getProviderName() {
    return "Ollama";
  }
}
