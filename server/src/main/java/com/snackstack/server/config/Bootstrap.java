package com.snackstack.server.config;

import com.google.gson.Gson;
import com.snackstack.server.controller.UserController;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.service.UserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import io.github.cdimascio.dotenv.Dotenv;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public final class Bootstrap {
  public static void init(){
    Dotenv dotenv = Dotenv.configure().directory(".").filename(".env").load();
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl(dotenv.get("JDBC_URL"));
    cfg.setUsername(dotenv.get("JDBC_USERNAME"));
    cfg.setPassword(dotenv.get("JDBC_PASSWORD"));
    HikariDataSource ds = new HikariDataSource(cfg);

    Gson gson = new Gson();
    Jdbi jdbi = Jdbi.create(ds).installPlugin(new SqlObjectPlugin());

    UserDAO userDAO = jdbi.onDemand(UserDAO.class);
    UserService userService = new UserService(userDAO);
    UserController.registerRoutes(userDAO, userService, gson);
  }

}
