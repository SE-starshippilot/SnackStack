package com.snackstack.server.servicetest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.dto.UserDTO;
import com.snackstack.server.exceptions.RecordNotFound;
import com.snackstack.server.service.UserService;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UserServiceTest {

  @Rule
  public JdbiRule jdbiRule = JdbiRule.h2().withPlugin(new SqlObjectPlugin());;

  private UserDAO userDAO;
  private UserService userService;
  private final String sampleUserName = "Nim Telson";
  private final String sampleUserEmail = "nim@snackstack.com";
  private final UserDTO sampleUser = new UserDTO(sampleUserName, sampleUserEmail);

  @Before
  public void setUp() {
    Jdbi jdbi = jdbiRule.getJdbi();
    // Create your tables
    jdbi.useHandle(handle -> {
      handle.execute("""
          CREATE TABLE users
          (
              user_id       SERIAL PRIMARY KEY,
              user_name     VARCHAR(16) NOT NULL,
              email         TEXT UNIQUE NOT NULL,
              created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
              last_login_at TIMESTAMP
          );""");
    });

    // Initialize your DAO
    userDAO = jdbi.onDemand(UserDAO.class);
    userService = new UserService(userDAO);
  }

  @Test
  public void createUser_ShouldAddUserToDatabase() {
    int id = userService.createUser(sampleUser);
    assertTrue("User should exist after creation", userDAO.userExistsById(id));
    assertTrue("User should be found by username", userDAO.userExistsByName(sampleUserName));
    assertTrue("User should be found by email", userDAO.userExistsByEmail(sampleUserEmail));
  }

  @Test(expected = Exception.class)
  public void createUser_WithDuplicateName_ShouldThrowException() {
    userService.createUser(sampleUser);
    // Try to create duplicate user
    userService.createUser(sampleUser);
    // Should throw exception due to unique constraint
  }

  @Test
  public void getUserIdByName_ShouldReturnCorrectId() {
    int expectedId = userService.createUser(sampleUser);
    int actualId = (int)userService.getUserIdByName(sampleUserName);
    assertEquals("User ID should match the one returned during creation", expectedId, actualId);
  }

  @Test(expected = RecordNotFound.class)
  public void getUserIdByName_WithNonExistentUser_ShouldThrowException() {
    userService.getUserIdByName("NonExistentUser");
    // Should throw RecordNotFound
  }

  @Test
  public void deleteUserByName_ShouldRemoveUserFromDatabase() {
    int id = userService.createUser(sampleUser);
    int rowsAffected = userService.deleteUserByName(sampleUser);

    assertEquals("Should affect exactly 1 row", 1, rowsAffected);
    assertFalse("User should not exist after deletion", userDAO.userExistsById(id));
    assertFalse("User should not be found by username after deletion",
        userDAO.userExistsByName(sampleUserName));
  }

  @Test
  public void deleteUserByName_WithNonExistentUser_ShouldReturnZeroRowsAffected() {
    int rowsAffected = userService.deleteUserByName(sampleUser);
    assertEquals("Should affect 0 rows when deleting non-existent user", 0, rowsAffected);
  }

}
