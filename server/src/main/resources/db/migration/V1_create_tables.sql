DROP TABLE IF EXISTS recipe_ingredients CASCADE;
DROP TABLE IF EXISTS recipe_steps CASCADE;
DROP TABLE IF EXISTS recipe_history CASCADE;
DROP TABLE IF EXISTS recipes CASCADE;
DROP TABLE IF EXISTS inventory_items CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS recipe_type;

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

CREATE TYPE recipe_type AS ENUM ('Main', 'Appetizer', 'Dessert', 'Breakfast', 'Snack');

CREATE TABLE recipes
(
    recipe_id        SERIAL PRIMARY KEY,
    recipe_name      TEXT NOT NULL,
    description      TEXT,
    servings         INT,
    recipe_origin_id VARCHAR(16),
    recipe_type TEXT
);

CREATE TABLE recipe_history
(
    history_id SERIAL PRIMARY KEY,
    user_id    INT         NOT NULL
        REFERENCES users (user_id)
            ON DELETE CASCADE,
    recipe_id  INT         NOT NULL
        REFERENCES recipes (recipe_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE recipe_steps
(
    step_id          SERIAL PRIMARY KEY,
    recipe_id        INT  NOT NULL
        REFERENCES recipes (recipe_id)
            ON DELETE CASCADE,
    step_number      INT  NOT NULL,
    step_description TEXT NOT NULL,
    UNIQUE (recipe_id, step_number)
);

CREATE TABLE recipe_ingredients
(
    recipe_id     INT NOT NULL
        REFERENCES recipes (recipe_id)
            ON DELETE CASCADE,
    ingredient_id INT NOT NULL
        REFERENCES ingredients (ingredient_id)
            ON DELETE RESTRICT,
    quantity      DECIMAL(10, 2) NOT NULL ,
    unit          VARCHAR(50),
    note          TEXT,
    PRIMARY KEY (recipe_id, ingredient_id)
);


