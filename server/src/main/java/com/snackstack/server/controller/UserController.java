package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.dto.UserDTO;
import com.snackstack.server.model.User;
import com.snackstack.server.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController implements Controller {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private final UserDAO dao;
  private final UserService service;
  private final Gson gson;

  public UserController(UserDAO dao, UserService service, Gson gson) {
    this.dao = dao;
    this.service = service;
    this.gson = gson;
  }

  @Override
  public String getBasePath() {
    return "/api/users";
  }

  @Override
  public void registerRoutes() {
    logger.info("Registering User API routes");

    path(getBasePath(), () -> {
      /* ---- POST /api/users ----  (create) */
      post("", (req, res) -> {
        logger.info("Received request to create user");
        UserDTO body = gson.fromJson(req.body(), UserDTO.class);
        logger.debug("Request body parsed: username={} email={}", body.userName(), body.email());

        long id = service.createUser(body);
        res.status(201);
        logger.info("User created successfully with ID: {}", id);
        return gson.toJson(new IdResponse(id));
      });

      /* ---- GET /api/users/email/:email ---- (get user by email) */
      get("/email/:email", (req, res) -> {
        String email = req.params(":email");
        logger.info("Received request to get user with email: {}", email);

        Optional<User> user = service.findUserByEmail(email);
        if (user.isPresent()) {
          logger.debug("User found with email: {}", email);
          res.status(200);
          return gson.toJson(user.get());
        } else {
          logger.debug("No user found with email: {}", email);
          res.status(404);
          return gson.toJson(new ErrorResponse("User not found"));
        }
      });

      /* ---- GET /api/users/exists/email/:email ---- (check if exists) */
      get("/exists/email/:email", (req, res) -> {
        String email = req.params(":email");
        logger.info("Received request to check if user exists with email: {}", email);

        long userId = service.getUserIdByEmail(email);
        logger.debug("User lookup by email result: {}", userId);

        return gson.toJson(new UserExistsResponse(userId));
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
  private record IdResponse(long id) {}
  private record UserExistsResponse(long id) {}
  private record ErrorResponse(String message) {}
}