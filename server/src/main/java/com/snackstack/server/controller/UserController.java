// controller/UserController.java
package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.dto.UserDTO;
import com.snackstack.server.service.UserService;   // business rules
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  public static void registerRoutes(UserDAO dao, UserService svc, Gson gson) {
    logger.info("Registering User API routes");
    path("/api/users", () -> {

      /* ---- POST /api/users ----  (create) */
      post("", (req, res) -> {
        logger.info("Received request to create user");
        UserDTO body = gson.fromJson(req.body(), UserDTO.class);
        logger.debug("Request body parsed: username={} email={}", body.userName(), body.email());
        long id = svc.createUser(body);           // hashes pw, calls dao.insert()
        res.status(201);
        logger.info("User created successfully with ID: {}", id);
        return gson.toJson(new IdResponse(id));
      });

      /* ---- DELETE /api/users/:name ----  (delete) */
      delete("/:name", (req, res) -> {
        String username = req.params(":name");
        logger.info("Received request to delete user: {}", username);

        boolean ok = dao.deleteByName(username) == 1;
        if (!ok) {
          logger.warn("User not found for deletion: {}", username);
          halt(404, "User not found");
        }
        res.status(204);
        logger.info("User deleted successfully: {}", username);
        return "";
      });
    });
    logger.info("User API routes registered successfully");
  }

  /* small DTOs local to controller layer */
  private record IdResponse(long id) {

  }
}
