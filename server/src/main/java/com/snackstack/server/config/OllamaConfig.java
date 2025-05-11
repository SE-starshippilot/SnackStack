package com.snackstack.server.config;

import com.snackstack.server.exceptions.LLMServiceException;
import io.github.ollama4j.OllamaAPI;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OllamaConfig {

  private final String hostURL;
  private final String systemPrompt;
  private final String model;

  public OllamaConfig(String hostURL, String systemPromptPath, String model) {
    this.hostURL = hostURL;
    this.model = model;

    // Validate Ollama service
    OllamaAPI tempApi = new OllamaAPI(hostURL);
    tempApi.setVerbose(false);
    boolean isOllamaServiceReachable = tempApi.ping();
    if (!isOllamaServiceReachable) {
      throw new LLMServiceException("Ollama service is not reachable");
    }

    // Load and validate system prompt
    try {
      Path path = Paths.get(systemPromptPath);
      this.systemPrompt = Files.readString(path);
    } catch (IOException e) {
      throw new LLMServiceException("Failed to load system prompt: " + e.getMessage());
    }
  }

  public String getHostURL() {
    return hostURL;
  }

  public String getSystemPrompt() {
    return systemPrompt;
  }

  public String getModel() {
    return model;
  }

}