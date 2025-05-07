package com.snackstack.server.config;
import org.flywaydb.core.Flyway;

public class RunMigration {
    public static void main(String[] args) {
        AppConfig config = AppConfig.getInstance();
        DBConfig dbConfig = config.configDB();

        Flyway flyway = Flyway.configure()
            .dataSource(dbConfig.getDataSource()) 
            .locations("classpath:db/migration")
            .baselineOnMigrate(true) 
            .cleanDisabled(false)
            .load();

        flyway.migrate();
        System.out.println("Database migration completed.");
    }
}
