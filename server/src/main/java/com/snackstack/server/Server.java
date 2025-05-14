package com.snackstack.server;

import com.snackstack.server.service.llm.LLMProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snackstack.server.config.ApplicationContext;

import spark.Spark;
import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;

public class Server {

  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private static ApplicationContext appContext;

  /**
   * Parse command line arguments to determine the LLM provider.
   * Format: --provider=OPENAI or -p=OPENAI
   * @param args Command line arguments
   * @return The selected LLM provider, defaults to MOCK if not specified or invalid
   */
  private static LLMProvider parseLLMProvider(String[] args) {
    if (args != null) {
      for (String arg : args) {
        if (arg.startsWith("--provider=") || arg.startsWith("-p=")) {
          String providerStr = arg.contains("--provider=") ?
              arg.substring("--provider=".length()) :
              arg.substring("-p=".length());

          try {
            return LLMProvider.valueOf(providerStr.toUpperCase());
          } catch (IllegalArgumentException e) {
            logger.warn("Invalid LLM provider: {}. Using default MOCK provider.", providerStr);
          }
        }
      }
    }

    // Default to MOCK if no valid provider is specified
    return LLMProvider.MOCK;
  }

  public static void main(String[] args) {
    logger.info("Starting Snackstack server application");

    LLMProvider provider = parseLLMProvider(args);
    logger.info("Using LLM provider: {}", provider);

    try {
      logger.debug("Setting server port to 8080");
      port(8080);

      logger.info("Initializing application context");
      appContext = new ApplicationContext(provider);

      logger.info("Registering Controller routes");
      appContext.registerAllRoutes();

      logger.debug("Waiting for server initialization");
      Spark.awaitInitialization();

      logger.info("Server started successfully on port {}", Spark.port());

      // Register shutdown hook
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        logger.info("Shutdown signal received, stopping server...");
        Spark.stop();
        logger.info("Server stopped");
        if (appContext != null) {
          try {
            appContext.close();
          } catch (Exception e) {
            logger.error("Error during application context shutdown", e);
          }
        }
      }));

    } catch (Exception e) {
      logger.error("Failed to start server", e);
      if (appContext != null) {
        try {
          appContext.close();
        } catch (Exception ex) {
          logger.error("Error closing application context after startup failure", ex);
        }
      }
      System.exit(1);
    }

    // enable CORS
    enableCORS("*", "*", "*");
  }

  private static void enableCORS(final String origin, final String methods, final String headers) {
    options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    before((request, response) -> {
      response.header("Access-Control-Allow-Origin", origin);
      response.header("Access-Control-Request-Method", methods);
      response.header("Access-Control-Allow-Headers", headers);
      // Optional for id verification
      // response.header("Access-Control-Allow-Credentials", "true");
    });
  }
}
