package com.snackstack.server.config;

import com.google.gson.Gson;
import com.snackstack.server.controller.InventoryController;
import com.snackstack.server.controller.UserController;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.UserService;
import com.snackstack.server.util.DBUtil;
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
      Gson gson = new Gson();

      Jdbi jdbi = DBUtil.getJdbi();

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
