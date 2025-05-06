package com.snackstack.server.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class DBUtil {
    private static Jdbi jdbi;
    private static Dotenv dotenv;

    static {
        dotenv = Dotenv.configure().filename(".env").load();

        if (dotenv.get("JDBC_URL") == null) {
            throw new RuntimeException("Missing JDBC_URL in .env");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dotenv.get("JDBC_URL"));
        config.setUsername(dotenv.get("JDBC_USERNAME"));
        config.setPassword(dotenv.get("JDBC_PASSWORD"));

        HikariDataSource dataSource = new HikariDataSource(config);
        jdbi = Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin());
    }

    public static Jdbi getJdbi() {
        return jdbi;
    }

    public static Dotenv getDotenv() {
        return dotenv;
    }
}
