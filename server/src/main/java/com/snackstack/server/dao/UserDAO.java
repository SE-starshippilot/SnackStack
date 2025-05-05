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
      INSERT INTO users(user_name, email, created_at, last_login_at)
      VALUES (:userName, :email, :createdAt, :lastLoginAt)
      RETURNING user_id
      """)
  Integer insert(
      @Bind("userName") String userName,
      @Bind("email") String email,
      @Bind("createdAt") Instant createdAt,
      @Bind("lastLoginAt") Instant lastLoginAt
  );

  /* READ: fetch a user */
  @SqlQuery("""
      SELECT user_id
      FROM users
      WHERE user_name = :userName
      """)
  Optional<Integer> getUserIdByName(@Bind("userName") String username);

  /* DELETE: remove a user */
  @SqlUpdate("""
      DELETE FROM users
      WHERE user_name = :userName
      """)
  int deleteByName(@Bind("userName") String userName);
}