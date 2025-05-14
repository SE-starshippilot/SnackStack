package com.snackstack.server.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  private final Dotenv dotenv;
  private static AppConfig instance;

  private AppConfig() {
    this.dotenv = Dotenv.configure().directory(".").filename(".env").load();
  }

  public static synchronized AppConfig getInstance() {
    if (instance == null) {
      instance = new AppConfig();
    }
    return instance;
  }

  public DBConfig configDB() throws RuntimeException {
    try {
      String dbUrl = getField("JDBC_URL");
      String dbUser = getField("JDBC_USERNAME");
      String dbPassword = getField("JDBC_PASSWORD");
      return new DBConfig(dbUrl, dbUser, dbPassword);
    } catch (RuntimeException e) {
      logger.error("Failed to initialize DBConfig", e);
      throw new RuntimeException("Failed to initialize DBConfig: " + e);
    }
  }

  public OllamaConfig configOllama() throws RuntimeException {
    try {
      String ollamaHostURL = getField("OLLAMA_URL");
      String systemPromptPath = getField("LLM_SYSTEM_PROMPT_FILE");
      String ollamaModel = getField("OLLAMA_MODEL");
      return new OllamaConfig(ollamaHostURL, systemPromptPath, ollamaModel);
    } catch (Exception e) {
      logger.error("Failed to config OllamaRecipeGenerator", e);
      throw new RuntimeException("Failed to initialize OllamaRecipeGenerator: " + e);
    }
  }

  public OpenAIConfig configOpenAi() throws RuntimeException {
    try {
      // Required parameters
      String openaiApiKey = getField("OPENAI_API_KEY");
      String systemPromptPath = getField("LLM_SYSTEM_PROMPT_FILE");
      String openaiModel = getField("OPENAI_MODEL");

      // Optional parameters with defaults
      String openaiBaseUrl = getOptionalField("OPENAI_BASE_URL", "https://api.openai.com/v1");

      return new OpenAIConfig(
          openaiApiKey,
          openaiBaseUrl,
          openaiModel,
          systemPromptPath
      );
    } catch (Exception e) {
      logger.error("Failed to config OpenAIRecipeGenerator", e);
      throw new RuntimeException("Failed to initialize OpenAIRecipeGenerator: " + e);
    }
  }


  private String getField(String fieldName) throws RuntimeException {
    String fieldVal = this.dotenv.get(fieldName);
    if (fieldVal == null) {
      throw new RuntimeException("Field " + fieldName + " not found");
    }
    return fieldVal;
  }

  private String getOptionalField(String fieldName, String defaultValue) {
    String fieldVal = this.dotenv.get(fieldName);
    if (fieldVal == null) {
      logger.info("{} not provided, using default value", fieldName);
      return defaultValue;
    }
    return fieldVal;
  }
}
