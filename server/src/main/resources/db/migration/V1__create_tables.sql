DROP TABLE IF EXISTS recipe_ingredients CASCADE;
DROP TABLE IF EXISTS recipe_steps CASCADE;
DROP TABLE IF EXISTS recipe_history CASCADE;
DROP TABLE IF EXISTS recipes CASCADE;
DROP TABLE IF EXISTS inventory_items CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS recipe_generation_requests CASCADE;
DROP TABLE IF EXISTS request_preferences CASCADE;
DROP TABLE IF EXISTS request_allergies CASCADE;
DROP TYPE IF EXISTS recipe_type;

CREATE EXTENSION pg_trgm;

CREATE TABLE users
(
    user_id       SERIAL PRIMARY KEY,
    user_name     VARCHAR(16) NOT NULL,
    email         TEXT UNIQUE NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at TIMESTAMPTZ
);

CREATE TABLE ingredients
(
    ingredient_id   SERIAL PRIMARY KEY,
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
    purchase_date     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TYPE recipe_type AS ENUM ('MAIN', 'APPETIZER', 'DESSERT', 'BREAKFAST', 'SNACK');

CREATE TABLE recipes
(
    recipe_id        SERIAL PRIMARY KEY,
    recipe_name      TEXT NOT NULL,
    description      TEXT,
    servings         INT,
    recipe_origin_id VARCHAR(16),
    recipe_type      recipe_type,
    uuid             VARCHAR(36) NOT NULL UNIQUE
);

CREATE TABLE recipe_history
(
    history_id  SERIAL PRIMARY KEY,
    user_id     INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    recipe_id   INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    is_favorite      BOOLEAN DEFAULT false
);

CREATE TABLE recipe_steps
(
    step_id          SERIAL PRIMARY KEY,
    recipe_id        INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    step_number      INT NOT NULL,
    step_description TEXT NOT NULL,
    UNIQUE (recipe_id, step_number)
);

CREATE TABLE recipe_ingredients
(
    recipe_id     INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    ingredient_id INT NOT NULL REFERENCES ingredients(ingredient_id) ON DELETE RESTRICT,
    quantity      DECIMAL(10, 2) NOT NULL,
    unit          VARCHAR(50),
    note          TEXT,
    PRIMARY KEY (recipe_id, ingredient_id)
);

CREATE TABLE recipe_generation_requests (
    request_id SERIAL PRIMARY KEY,
    history_id INTEGER NOT NULL REFERENCES recipe_history(history_id) ON DELETE CASCADE,
    servings INTEGER NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT valid_servings CHECK (servings BETWEEN 1 AND 10)
);

CREATE TABLE request_preferences (
    request_id INTEGER REFERENCES recipe_generation_requests(request_id) ON DELETE CASCADE,
    preference VARCHAR(100) NOT NULL,
    PRIMARY KEY (request_id, preference)
);

CREATE TABLE request_allergies (
    request_id INTEGER REFERENCES recipe_generation_requests(request_id) ON DELETE CASCADE,
    allergy VARCHAR(100) NOT NULL,
    PRIMARY KEY (request_id, allergy)
);

CREATE INDEX idx_recipe_history_user ON recipe_history(user_id);
CREATE INDEX idx_recipe_history_recipe ON recipe_history(recipe_id);
CREATE INDEX idx_recipe_requests_history ON recipe_generation_requests(history_id);
CREATE INDEX idx_request_preferences ON request_preferences(request_id);
CREATE INDEX idx_request_allergies ON request_allergies(request_id);


