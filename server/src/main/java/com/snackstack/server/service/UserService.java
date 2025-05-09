// service/UserService.java
package com.snackstack.server.service;

import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.dto.UserDTO;
import com.snackstack.server.exceptions.RecordNotFound;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

  private final UserDAO dao;
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public UserService(UserDAO dao) {
    this.dao = dao;
    logger.debug("UserService initialized");
  }

  public int createUser(UserDTO req) {
    try {
      logger.info("Creating new user with username: {}", req.userName());
      // 1. uniqueness format checks (omitted, maybe will implement later idk)
      // 2. get current time instant
      Instant now = Instant.now();
      // 3. delegate to DAO
      int userId = dao.insert(req.userName(), req.email(), now, now);
      logger.info("Successfully created user with ID: {}", userId);
      return userId;
    } catch (Exception e) {
      logger.error("Error creating user: {}", req.userName(), e);
      throw e;
    }
  }

  public int deleteUserByName(UserDTO req) {
    try {
      logger.info("Deleting user with username: {}", req.userName());
      int rowsAffected = dao.deleteByName(req.userName());
      logger.info("Delete user operation affected {} rows", rowsAffected);
      return rowsAffected;
    } catch (Exception e) {
      logger.error("Error deleting user: {}", req.userName(), e);
      throw e;
    }
  }

  public int getUserIdByName(String userName) {
    try {
      logger.info("Searching user with username: {}", userName);
      Optional<Integer> uid = dao.getUserIdByName(userName);
      if (uid.isPresent()) {
        return uid.get();
      } else {
        throw new RecordNotFound("User not found");
      }
    } catch (Exception e) {
      logger.error("Error searching user: {}", userName, e);
      throw e;
    }
  }
}
