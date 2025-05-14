package com.snackstack.server.servicetest;

import com.google.gson.Gson;
import com.snackstack.server.dao.*;
import com.snackstack.server.dto.IngredientDTO;
import com.snackstack.server.dto.RecipeGenerationDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.dto.UserDTO;
import com.snackstack.server.exceptions.RecordNotFound;
import com.snackstack.server.model.RecipeType;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.RecipeHistoryService;
import com.snackstack.server.service.RecipeService;
import com.snackstack.server.service.UserService;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.snackstack.server.model.RecipeType.MAIN;
import static org.junit.Assert.*;

public class InventoryServiceTest {
  @Rule
  public JdbiRule jdbiRule = JdbiRule.h2().withPlugin(new SqlObjectPlugin());

  private final Gson gson = new Gson();
  private IngredientDAO ingredientDAO;
  private InventoryDAO inventoryDAO;
  private InventoryService inventoryService;
  private UserDAO userDAO;
  private UserService userService;
  private final String sampleUserName = "Nim Telson";
  private final String sampleUserEmail = "nim@snackstack.com";
  private final UserDTO sampleUser = new UserDTO(sampleUserName, sampleUserEmail);
  private int userId;

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
          );
                CREATE TABLE ingredients (
                    ingredient_id SERIAL PRIMARY KEY,
                    ingredient_name varchar(255) NOT NULL UNIQUE
                );
                CREATE TABLE inventory_items
                (
                    inventory_item_id SERIAL PRIMARY KEY,
                    user_id           INT         NOT NULL
                        REFERENCES users (user_id)
                            ON DELETE CASCADE,
                    ingredient_id     INT         NOT NULL
                        REFERENCES ingredients (ingredient_id),
                    purchase_date     TIMESTAMP NOT NULL DEFAULT now()
                );
            """);
    });

    userDAO = jdbi.onDemand(UserDAO.class);
    ingredientDAO = jdbi.onDemand(IngredientDAO.class);
    userService = new UserService(userDAO);
    inventoryDAO = jdbi.onDemand(InventoryDAO.class);
    inventoryService = new InventoryService(userDAO, inventoryDAO, ingredientDAO);

    // Prepare sample user & recipe
    userId = userService.createUser(sampleUser);
    inventoryService.addInventoryRecord(sampleUserName, "ingredient1");
  }

  // add new inventory
  @Test
  public void testAddInventoryRecord_ShouldAddIngredientAndAppearInUserInventory() {
    inventoryService.addInventoryRecord(sampleUserName, "ingredient2");

    List<String> ingredients = inventoryService.getIngredients(sampleUserName);
    assertTrue(ingredients.contains("ingredient1"));
    assertTrue(ingredients.contains("ingredient2"));
    assertEquals(2, ingredients.size());
  }

  // get inventory
  @Test
  public void testGetIngredients_ShouldReturnCorrectList() {
    List<String> ingredients = inventoryService.getIngredients(sampleUserName);
    assertEquals(1, ingredients.size());
    assertEquals("ingredient1", ingredients.get(0));
  }

  // delete inventory
  @Test
  public void testDeleteIngredient_ShouldRemoveIngredientFromInventory() {
    inventoryService.deleteIngredient(sampleUserName, "ingredient1");
    List<String> ingredientsAfterDelete = inventoryService.getIngredients(sampleUserName);
    assertEquals(0, ingredientsAfterDelete.size());
  }

  // delete non existent ingredient
  @Test
  public void testDeleteNonExistentIngredient_ShouldReturnMinusOne() {
    int deleted = inventoryService.deleteIngredient(sampleUserName, "nonexistent_ingredient");
    assertEquals(-1, deleted);
  }

  // get inventory from the illegal user
  @Test
  public void testGetIngredients_WithNonExistentUser_ShouldThrowRecordNotFound() {
    assertThrows(RecordNotFound.class, () -> {
      inventoryService.getIngredients("not_a_real_user");
    });
  }
}
