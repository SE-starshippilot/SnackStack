package com.snackstack.server;

import com.snackstack.server.config.Bootstrap;
import spark.Spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class Server {

  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  public static void main(String[] args) {
    logger.info("Starting Snackstack server application");

    try {
      logger.debug("Setting server port to 8080");
      port(8080);

      logger.info("Initializing application components");
      Bootstrap.init();

      logger.debug("Waiting for server initialization");
      Spark.awaitInitialization();

      logger.info("Server started successfully on port {}", Spark.port());

      // Register shutdown hook
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        logger.info("Shutdown signal received, stopping server...");
        Spark.stop();
        logger.info("Server stopped");
      }));

    } catch (Exception e) {
      logger.error("Failed to start server", e);
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
      // add authentication
      // response.header("Access-Control-Allow-Credentials", "true");
    });
  }
}
