package com.snackstack.server.dao;

import com.snackstack.server.model.User;
import java.time.Instant;
import java.util.Optional;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;

@RegisterBeanMapper(User.class)
public interface UserDAO {
  /* CREATE: Create a new user */
  @SqlQuery("""
      INSERT INTO users(userName, email, createdAt, lastLoginAt)
      VALUES (:userName, :email, :createdAt, :lastLoginAt)
      RETURNING userID
      """)
  Integer insert(
      @Bind("userName") String userName,
      @Bind("email") String email,
      @Bind("createdAt") Instant createdAt,
      @Bind("lastLoginAt") Instant lastLoginAt
  );

  /* READ: fetch a user */
  @SqlQuery("SELECT * FROM users WHERE userName = :userName")
  Optional<User> findByUsername(@Bind("userName") String username);

  /* DELETE: remove a user */
  @SqlUpdate("DELETE FROM users WHERE userName = :userName")
  int deleteByName(@Bind("userName") String userName);
}
