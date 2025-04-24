// controller/UserController.java
package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.dto.UserDTO;
import com.snackstack.server.service.UserService;   // business rules

public final class UserController {

  public static void registerRoutes(UserDAO dao, UserService svc, Gson gson) {

    path("/api/users", () -> {

      /* ---- POST /api/users ----  (create) */
      post("", (req, res) -> {
        UserDTO body = gson.fromJson(req.body(), UserDTO.class);
        long id = svc.createUser(body);           // hashes pw, calls dao.insert()
        res.status(201);
        return gson.toJson(new IdResponse(id));
      });

      /* ---- DELETE /api/users/:id ----  (delete) */
      delete("/:name", (req, res) -> {
        boolean ok = dao.deleteByName(req.params(":name")) == 1;     // rows affected
        if (!ok) { halt(404, "User not found"); }
        res.status(204);
        return "";
      });
    });
  }

  /* small DTOs local to controller layer */
  private record UserCreateReq(String userName, String email) {}
  private record IdResponse(long id) {}
}
