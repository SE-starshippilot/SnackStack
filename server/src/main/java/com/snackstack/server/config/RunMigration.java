package com.snackstack.server.config;
import org.flywaydb.core.Flyway;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

public class RunMigration {
    public static void main(String[] args) {
        AppConfig config = AppConfig.getInstance();
        DBConfig dbConfig = config.configDB();
        DataSource dataSource = dbConfig.getDataSource();

        try {
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .cleanDisabled(false)
                .load();

            flyway.clean();
            flyway.migrate();
            System.out.println("Database migration completed.");
        } finally {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        }
    }
}
