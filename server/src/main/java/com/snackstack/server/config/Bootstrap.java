package com.snackstack.server.config;

import com.google.gson.Gson;
import com.snackstack.server.controller.InventoryController;
import com.snackstack.server.controller.UserController;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.UserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import io.github.cdimascio.dotenv.Dotenv;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bootstrap {

  private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

  public static void init() {
    logger.info("Initializing application...");
    try {
      logger.debug("Loading environment variables");
      Dotenv dotenv = Dotenv.configure().directory(".").filename(".env").load();

      logger.debug("Configuring database connection pool");
      HikariConfig cfg = new HikariConfig();
      cfg.setJdbcUrl(dotenv.get("JDBC_URL"));
      cfg.setUsername(dotenv.get("JDBC_USERNAME"));
      // Password intentionally not logged
      cfg.setPassword(dotenv.get("JDBC_PASSWORD"));
      HikariDataSource ds = new HikariDataSource(cfg);
      logger.info("Database connection pool initialized");

      logger.debug("Creating JSON serializer");
      Gson gson = new Gson();

      logger.debug("Setting up JDBI with SQL Object plugin");
      Jdbi jdbi = Jdbi.create(ds).installPlugin(new SqlObjectPlugin());

      logger.info("Initializing DAO and service layers");
      UserDAO userDAO = jdbi.onDemand(UserDAO.class);
      InventoryDAO inventoryDAO = jdbi.onDemand(InventoryDAO.class);
      UserService userService = new UserService(userDAO);
      InventoryService inventoryService = new InventoryService(userDAO, inventoryDAO);

      logger.info("Registering controller routes");
      UserController.registerRoutes(userDAO, userService, gson);
      InventoryController.registerRoutes(inventoryService, gson);

      logger.info("Application initialization completed successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize application", e);
      throw e;
    }
  }

}
