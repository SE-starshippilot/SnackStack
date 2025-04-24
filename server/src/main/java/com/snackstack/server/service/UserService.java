// service/UserService.java
package com.snackstack.server.service;

import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.dto.UserDTO;
import java.time.Instant;

public class UserService {

  private final UserDAO dao;

  public UserService(UserDAO dao) { this.dao = dao; }

  public long createUser(UserDTO req) {
    try{
      // 1. uniqueness format checks (omitted, maybe will implement later idk)
      // 2. get current time instant
      Instant now = Instant.now();
      // 3. delegate to DAO
      return dao.insert(req.userName(), req.email(), now, now);
    } catch(Exception e){
      System.out.println(e);
      throw e;
    }

  }
}
