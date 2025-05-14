package com.snackstack.server.config;

import com.snackstack.server.exceptions.LLMServiceException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenAIConfig {

  private final String apiKey;
  private final String baseUrl;
  private final String model;
  private final String systemPrompt;

  public OpenAIConfig(String apiKey, String baseUrl,
      String model, String systemPromptPath) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
    this.model = model;
    // Load and validate system prompt
    try {
      Path path = Paths.get(systemPromptPath);
      this.systemPrompt = Files.readString(path);
    } catch (IOException e) {
      throw new LLMServiceException("Failed to load system prompt: " + e.getMessage());
    }
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getModel() {
    return model;
  }

  public String getSystemPrompt() {
    return systemPrompt;
  }
}