package com.snackstack.server;

import com.snackstack.server.config.Bootstrap;
import spark.Spark;

import static spark.Spark.port;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  }
}
