package com.snackstack.server;

import com.snackstack.server.config.ApplicationContext;
import spark.Spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Server {

  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private static ApplicationContext appContext;

  public static void main(String[] args) {
    logger.info("Starting Snackstack server application");

    try {
      logger.debug("Setting server port to 8080");
      port(8080);

      logger.info("Initializing application context");
      appContext = new ApplicationContext(true);

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
