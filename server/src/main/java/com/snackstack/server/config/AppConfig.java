package com.snackstack.server.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  private final Dotenv dotenv;
  private static AppConfig instance;

  private AppConfig() {
    this.dotenv = Dotenv.configure().directory("server").filename(".env").load();
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

  private String getField(String fieldName) throws RuntimeException {
    String fieldVal = this.dotenv.get(fieldName);
    if (fieldVal == null) {
      throw new RuntimeException("Field " + fieldName + " not found");
    }
    return fieldVal;
  }
}
