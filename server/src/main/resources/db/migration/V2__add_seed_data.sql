INSERT INTO users (user_id, user_name, email) VALUES
    (1, 'testuser1', 'test1@example.com'),
    (2, 'testuser2', 'test2@example.com'),
    (3, 'foodlover', 'food@example.com'),
    (4, 'chef_pro', 'chef@example.com');

INSERT INTO ingredients (ingredient_id, ingredient_name) VALUES
    (1, 'Spaghetti'),
    (2, 'Eggs'),
    (3, 'Pancetta'),
    (4, 'Parmesan'),
    (5, 'Chicken breast'),
    (6, 'Rice'),
    (7, 'Carrots'),
    (8, 'Onions'),
    (9, 'Olive oil'),
    (10, 'Spinach'),
    (11, 'Garlic powder'),
    (12, 'Canned tomatoes'),
    (13, 'Ground Black Pepper'),
    (14, 'Yogurt'),
    (15, 'Garam Masala'),
    (16, 'Heavy Cream'),
    (17, 'Tomato Puree'),
    (18, 'Salt'),
    (19, 'Butter'),
    (20, 'Flour');

INSERT INTO recipes (recipe_id, recipe_name, description, servings, recipe_origin_id, recipe_type, is_favorite, uuid) VALUES
    (1, 'Spaghetti Carbonara', 'Classic Italian pasta dish with eggs and pancetta', 2, 'Italian', 'main', true, '11111111-1111-1111-1111-111111111111'),
    (2, 'Chicken Tikka Masala', 'Creamy and spicy Indian curry', 4, 'Indian', 'main', false, '22222222-2222-2222-2222-222222222222'),
    (3, 'Classic Pancakes', 'Fluffy breakfast pancakes', 2, 'American', 'breakfast', true, '33333333-3333-3333-3333-333333333333'),
    (4, 'Caesar Salad', 'Fresh and crispy classic salad', 2, 'American', 'appetizer', false, '44444444-4444-4444-4444-444444444444'),
    (5, 'Beef Burger', 'Juicy homemade burger with all the fixings', 1, 'American', 'main', true, '55555555-5555-5555-5555-555555555555'),
    (6, 'Chocolate Brownies', 'Rich and fudgy dessert squares', 8, 'American', 'dessert', true, '66666666-6666-6666-6666-666666666666'),
    (7, 'Vegetable Stir-Fry', 'Quick and healthy Asian-style vegetables', 2, 'Chinese', 'main', false, '77777777-7777-7777-7777-777777777777'),
    (8, 'Fish Tacos', 'Fresh and zesty Mexican-style tacos', 3, 'Mexican', 'main', true, '88888888-8888-8888-8888-888888888888'),
    (9, 'Mushroom Risotto', 'Creamy Italian rice dish', 4, 'Italian', 'main', false, '99999999-9999-9999-9999-999999999999'),
    (10, 'Apple Pie', 'Classic American dessert', 8, 'American', 'dessert', true, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO recipe_steps (recipe_id, step_number, step_description) VALUES
    (1, 1, 'Cook spaghetti in salted water'),
    (1, 2, 'Fry pancetta until crispy'),
    (1, 3, 'Mix eggs with cheese'),
    (1, 4, 'Combine all ingredients'),
    (2, 1, 'Marinate chicken in yogurt and spices'),
    (2, 2, 'Cook chicken until done'),
    (2, 3, 'Prepare curry sauce'),
    (2, 4, 'Combine chicken and sauce'),
    (3, 1, 'Mix dry ingredients'),
    (3, 2, 'Add wet ingredients'),
    (3, 3, 'Cook on griddle'),
    (4, 1, 'Wash and chop lettuce'),
    (4, 2, 'Make dressing'),
    (4, 3, 'Add croutons and parmesan'),
    (5, 1, 'Form burger patties'),
    (5, 2, 'Season with salt and pepper'),
    (5, 3, 'Grill to desired doneness'),
    (6, 1, 'Mix wet ingredients'),
    (6, 2, 'Add dry ingredients'),
    (6, 3, 'Bake until done');

INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
    (1, 1, 200, 'g'),
    (1, 2, 2, 'pieces'),
    (1, 3, 100, 'g'),
    (1, 4, 50, 'g'),
    (2, 5, 500, 'g'),
    (2, 14, 200, 'g'),
    (2, 15, 1.5, 'tablespoons'),
    (2, 17, 400, 'g'),
    (2, 16, 150, 'ml'),
    (3, 20, 200, 'g'),
    (3, 19, 50, 'g'),
    (3, 2, 2, 'pieces'),
    (4, 7, 100, 'g'),      
    (5, 8, 150, 'g'),      
    (6, 9, 200, 'g'),     
    (7, 10, 100, 'g'),     
    (8, 11, 2, 'tsp'),     
    (9, 12, 300, 'g'),     
    (10, 13, 1, 'tsp');    

INSERT INTO inventory_items (user_id, ingredient_id, purchase_date) VALUES
    (1, 1, NOW() - INTERVAL '3 days'),
    (1, 2, NOW() - INTERVAL '2 days'),
    (1, 4, NOW() - INTERVAL '1 day'),
    (2, 3, NOW()),
    (2, 5, NOW() - INTERVAL '1 day'),
    (3, 8, NOW() - INTERVAL '2 days'),
    (3, 9, NOW() - INTERVAL '1 day'),
    (4, 13, NOW()),
    (4, 15, NOW() - INTERVAL '3 days');

INSERT INTO recipe_history (history_id, user_id, recipe_id, created_at) VALUES
    (1, 1, 1, NOW() - INTERVAL '1 day'),
    (2, 1, 2, NOW() - INTERVAL '2 days'),
    (3, 1, 3, NOW() - INTERVAL '3 days'),
    (4, 1, 4, NOW() - INTERVAL '4 days'),
    (5, 1, 5, NOW() - INTERVAL '5 days'),
    (6, 1, 6, NOW() - INTERVAL '6 days'),
    (7, 2, 7, NOW() - INTERVAL '1 day'),
    (8, 2, 8, NOW() - INTERVAL '2 days'),
    (9, 3, 9, NOW() - INTERVAL '1 day'),
    (10, 3, 10, NOW() - INTERVAL '2 days'),
    (11, 4, 1, NOW() - INTERVAL '1 day'),
    (12, 4, 2, NOW() - INTERVAL '2 days');

SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));
SELECT setval('recipes_recipe_id_seq', (SELECT MAX(recipe_id) FROM recipes));
SELECT setval('ingredients_ingredient_id_seq', (SELECT MAX(ingredient_id) FROM ingredients));
SELECT setval('recipe_history_history_id_seq', (SELECT MAX(history_id) FROM recipe_history)); 