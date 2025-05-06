package com.snackstack.server.config;

import com.snackstack.server.util.DBUtil;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Migration {
    private static final Logger logger = LoggerFactory.getLogger(Migration.class);

    public static void run(Jdbi jdbi) {
        logger.info("Running database migrations (drop + recreate)");

        jdbi.useHandle(handle -> {
            // Development only: force clean
            handle.execute("DROP TABLE IF EXISTS recipe_ingredients CASCADE;");
            handle.execute("DROP TABLE IF EXISTS recipe_steps CASCADE;");
            handle.execute("DROP TABLE IF EXISTS recipe_history CASCADE;");
            handle.execute("DROP TABLE IF EXISTS recipes CASCADE;");
            handle.execute("DROP TABLE IF EXISTS inventory_items CASCADE;");
            handle.execute("DROP TABLE IF EXISTS ingredients CASCADE;");
            handle.execute("DROP TABLE IF EXISTS users CASCADE;");
            handle.execute("DROP TYPE IF EXISTS recipe_type;");

            // Create enum
            createEnumIfNotExists(handle, "recipe_type", """
                CREATE TYPE recipe_type AS ENUM ('Main', 'Appetizer', 'Dessert', 'Breakfast', 'Snack');
            """);

            // Create tables
            createTableIfNotExists(handle, "users", """
                CREATE TABLE users (
                    user_id SERIAL PRIMARY KEY,
                    user_name VARCHAR(16) NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                    last_login_at TIMESTAMPTZ
                );
            """);

            handle.execute("CREATE INDEX IF NOT EXISTS user_name_index ON users (user_name);");

            createTableIfNotExists(handle, "ingredients", """
                CREATE TABLE ingredients (
                    ingredient_id SERIAL PRIMARY KEY,
                    ingredient_name VARCHAR(255) NOT NULL UNIQUE
                );
            """);

            createTableIfNotExists(handle, "inventory_items", """
                CREATE TABLE inventory_items (
                    inventory_item_id SERIAL PRIMARY KEY,
                    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                    ingredient_name TEXT NOT NULL,
                    purchase_date TIMESTAMPTZ NOT NULL DEFAULT now()
                );
            """);

            createTableIfNotExists(handle, "recipes", """
                CREATE TABLE recipes (
                    recipe_id SERIAL PRIMARY KEY,
                    recipe_name TEXT NOT NULL,
                    description TEXT,
                    servings INT,
                    recipe_origin_id VARCHAR(16),
                    recipe_type TEXT
                );
            """);

            createTableIfNotExists(handle, "recipe_history", """
                CREATE TABLE recipe_history (
                    history_id SERIAL PRIMARY KEY,
                    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                    recipe_id INT NOT NULL REFERENCES recipes(recipe_id),
                    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
                );
            """);

            createTableIfNotExists(handle, "recipe_steps", """
                CREATE TABLE recipe_steps (
                    step_id SERIAL PRIMARY KEY,
                    recipe_id INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
                    step_number INT NOT NULL,
                    step_description TEXT NOT NULL,
                    UNIQUE (recipe_id, step_number)
                );
            """);

            createTableIfNotExists(handle, "recipe_ingredients", """
                CREATE TABLE recipe_ingredients (
                    recipe_id INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
                    ingredient_id INT NOT NULL REFERENCES ingredients(ingredient_id) ON DELETE RESTRICT,
                    quantity DECIMAL(10, 2) NOT NULL,
                    unit VARCHAR(50),
                    note TEXT,
                    PRIMARY KEY (recipe_id, ingredient_id)
                );
            """);

            logger.info("✅ Migration completed successfully.");
        });
    }

    private static void createTableIfNotExists(Handle handle, String tableName, String ddl) {
        boolean exists = handle.createQuery("""
            SELECT EXISTS (
                SELECT 1 FROM information_schema.tables 
                WHERE table_schema = 'public' AND table_name = :table
            )
        """).bind("table", tableName).mapTo(Boolean.class).one();

        if (!exists) {
            logger.info("Creating table '{}'", tableName);
            handle.execute(ddl);
        } else {
            logger.info("Skipping table '{}' — already exists.", tableName);
        }
    }

    private static void createEnumIfNotExists(Handle handle, String typeName, String ddl) {
        boolean exists = handle.createQuery("""
            SELECT EXISTS (
                SELECT 1 FROM pg_type WHERE typname = :type
            )
        """).bind("type", typeName).mapTo(Boolean.class).one();

        if (!exists) {
            logger.info("Creating enum type '{}'", typeName);
            handle.execute(ddl);
        } else {
            logger.info("Skipping enum type '{}' — already exists.", typeName);
        }
    }

    public static void main(String[] args) {
        Jdbi jdbi = DBUtil.getJdbi();
        run(jdbi);
    }
}
