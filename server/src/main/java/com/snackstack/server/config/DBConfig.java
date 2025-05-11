package com.snackstack.server.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class DBConfig {

  private final Jdbi jdbi;
  private final HikariDataSource dataSource;

  public DBConfig(String jdbcUrl, String username, String password) {
    if (jdbcUrl == null) {
      throw new RuntimeException("Missing JDBC_URL in .env");
    }

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);

    this.dataSource = new HikariDataSource(config);
    this.jdbi = Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin());
  }

  public Jdbi getJdbi() {
    return jdbi;
  }

  public HikariDataSource getDataSource() {
    return dataSource;
  }

  public void close() {
    if (this.dataSource != null) {
      this.dataSource.close();
    }
  }
}
