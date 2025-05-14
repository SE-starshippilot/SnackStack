package com.snackstack.server.service.llm;

import static com.snackstack.server.service.RecipeService.logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.snackstack.server.config.OpenAIConfig;
import com.snackstack.server.dto.RecipeGenerationDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.LLMServiceException;
import com.snackstack.server.service.RecipeGenerator;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.EasyInputMessage;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseOutputMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAIRecipeGenerator implements RecipeGenerator {

  private final OpenAIClient client;
  private final String systemPrompt;
  private final Gson gson;
  private final String model;
  private static final Logger logger = LoggerFactory.getLogger(OpenAIRecipeGenerator.class);

  public OpenAIRecipeGenerator(OpenAIConfig config, Gson gson) {
    this.gson = gson;
    this.systemPrompt = config.getSystemPrompt();
    this.model = config.getModel();
    this.client = OpenAIOkHttpClient.builder()
        .apiKey(config.getApiKey())
        .baseUrl(config.getBaseUrl()).build();
    logger.info("Initializing connection to OpenAI with model: {}", this.model);
  }

  @Override
  public String getProviderName() {
    return "OpenAI";
  }

  @Override
  public List<RecipeResponseDTO> generateRecipe(RecipeGenerationDTO recipeRequest)
      throws LLMServiceException {
    try {
      String userPrompt = gson.toJson(recipeRequest);
      logger.info("Generated user prompt: {}", userPrompt);

      // Create conversation messages
      List<ResponseInputItem> inputItems = new ArrayList<>();

      // Add system message
      inputItems.add(ResponseInputItem.ofEasyInputMessage(EasyInputMessage.builder()
          .role(EasyInputMessage.Role.SYSTEM)
          .content(systemPrompt)
          .build()));

      // Add user message with recipe request
      inputItems.add(ResponseInputItem.ofEasyInputMessage(EasyInputMessage.builder()
          .role(EasyInputMessage.Role.USER)
          .content(userPrompt)
          .build()));

      // Create the parameters for the API call
      ResponseCreateParams createParams = ResponseCreateParams.builder()
          .inputOfResponse(inputItems)
          .model(ChatModel.GPT_4_1)
          .build();

      // Make the API call and process the response
      List<ResponseOutputMessage> messages = client.responses().create(createParams).output()
          .stream()
          .flatMap(item -> item.message().stream())
          .toList();

      // 1. Collect the raw assistant text
      String rawResponse = messages.stream()
          .flatMap(m -> m.content().stream())
          .flatMap(c -> c.outputText().stream())
          .map(t -> t.text())
          .collect(Collectors.joining());

      // 2. Strip ```json … ``` if present
      String responseContent = unwrapMarkdownCodeBlock(rawResponse);

      logger.debug("Cleaned LLM JSON payload: {}", responseContent);

      // Parse the JSON response
      JsonElement jsonElement;
      try {
        jsonElement = gson.fromJson(responseContent, JsonElement.class);
      } catch (JsonSyntaxException e) {
        // The response isn't valid JSON at all
        throw new LLMServiceException("LLM response is not valid JSON: " + responseContent);
      }

      // Handle different response types
      if (jsonElement.isJsonPrimitive()) {
        // Try to parse the string content as JSON (LLM might have returned JSON as a string)
        try {
          String jsonString = jsonElement.getAsString();
          jsonElement = gson.fromJson(jsonString, JsonElement.class);
        } catch (Exception e) {
          // If still not valid JSON, we need to fix the system prompt
          throw new LLMServiceException(
              "LLM returned non-JSON format. Update the system prompt to require JSON output.");
        }
      }

      if (!jsonElement.isJsonObject()) {
        throw new LLMServiceException(
            "LLM did not return a JSON object as required: " + responseContent);
      }

      JsonObject responseJson = jsonElement.getAsJsonObject();

      boolean success = responseJson.has("success") && responseJson.get("success").getAsBoolean();
      if (!success) {
        String errorMessage =
            responseJson.has("message") ? responseJson.get("message").getAsString()
                : "Unknown error occurred.";
        throw new LLMServiceException("Recipe generation failed: " + errorMessage);
      }

      // Deserialize the "recipes" array into a list of RecipeResponseDTO
      List<RecipeResponseDTO> recipes = gson.fromJson(
          responseJson.getAsJsonArray("recipes"),  // JSON array of recipes
          new com.google.gson.reflect.TypeToken<List<RecipeResponseDTO>>() {
          }.getType()
      );
      return recipes.stream()
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

    } catch (JsonSyntaxException e) {
      logger.error("Failed to parse response from LLM.", e);
      throw new LLMServiceException("Failed to parse LLM response: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Unexpected error during recipe generation.", e);
      throw new LLMServiceException("Unexpected error: " + e.getMessage());
    }
  }

  private static String unwrapMarkdownCodeBlock(String raw) {
    // trim avoids stray whitespace before the opening ``` that would break startsWith()
    String cleaned = raw.trim();
    if (!cleaned.startsWith("```")) {          // fast path: not fenced
      return cleaned;
    }

    // (?s) → DOTALL, so '.' matches new‑lines
    // group(1) will hold the inside of the fence
    Pattern fence = Pattern.compile("(?s)```(?:json|\\w+)?\\s*(.*?)\\s*```");
    Matcher m = fence.matcher(cleaned);
    return m.find() ? m.group(1) : cleaned;    // fall back if somehow unmatched
  }
}
