import FavoriteIcon from "@mui/icons-material/Favorite";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import SearchIcon from "@mui/icons-material/Search";
import {
  Box,
  Button,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import ToggleButton from "@mui/material/ToggleButton";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { RecipeTable } from "../components/RecipeTable";
import "../styles/HistoryRecipes.css";
import { Recipe } from "../types/recipe";

const historyList: Recipe[] = [
  {
    id: "recipe-1",
    recipe_name: "Grilled Chicken with Roasted Vegetables and Rice",
    servings: 2,
    description:
      "A simple and flavorful main dish featuring grilled chicken, roasted vegetables, and a side of fluffy rice.",
    origin_name: "",
    recipe_ingredients: [
      { ingredient_name: "Chicken breast", quantity: 300.0, unit: "grams" },
      { ingredient_name: "Rice", quantity: 250.0, unit: "grams" },
      { ingredient_name: "Carrots", quantity: 150.0, unit: "grams" },
      {
        ingredient_name: "Onions",
        quantity: 100.0,
        unit: "grams",
        note: "Chopped.",
      },
      { ingredient_name: "Olive oil", quantity: 20.0, unit: "ml" },
    ],
    recipe_steps: [
      "Preheat grill to medium-high heat.",
      "Season the chicken with salt and cook until browned on both sides, then set aside.",
      "In a separate pan, heat olive oil over medium heat. Add chopped onions and cook until softened.",
      "Add sliced carrots to the pan and cook for an additional 5 minutes.",
      "Serve the grilled chicken with roasted vegetables and a side of fluffy rice.",
    ],
    date: "2024-03-15",
  },
  {
    id: "recipe-2",
    recipe_name: "Chicken and Vegetable Stir-Fry",
    servings: 2,
    description:
      "A quick and easy main dish featuring sautéed chicken, mixed vegetables, and a hint of garlic powder.",
    origin_name: "",
    recipe_ingredients: [
      { ingredient_name: "Chicken breast", quantity: 300.0, unit: "grams" },
      { ingredient_name: "Carrots", quantity: 150.0, unit: "grams" },
      {
        ingredient_name: "Onions",
        quantity: 100.0,
        unit: "grams",
        note: "Chopped.",
      },
      { ingredient_name: "Spinach", quantity: 50.0, unit: "grams" },
      { ingredient_name: "Garlic powder", quantity: 0.5, unit: "teaspoon" },
    ],
    recipe_steps: [
      "Heat olive oil in a large skillet over medium-high heat.",
      "Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.",
      "Add the chicken to the pan and cook until browned on both sides and cooked through.",
      "Stir in garlic powder and wilted spinach. Serve hot.",
    ],
    date: "2024-03-14",
  },
  {
    id: "recipe-3",
    recipe_name: "Chicken and Vegetable Fried Rice",
    servings: 2,
    description:
      "A flavorful main dish featuring sautéed chicken, mixed vegetables, and a side of fried rice.",
    origin_name: "",
    recipe_ingredients: [
      { ingredient_name: "Chicken breast", quantity: 300.0, unit: "grams" },
      { ingredient_name: "Rice", quantity: 250.0, unit: "grams" },
      { ingredient_name: "Carrots", quantity: 150.0, unit: "grams" },
      {
        ingredient_name: "Onions",
        quantity: 100.0,
        unit: "grams",
        note: "Chopped.",
      },
      { ingredient_name: "Canned tomatoes", quantity: 150.0, unit: "grams" },
    ],
    recipe_steps: [
      "Heat olive oil in a large skillet over medium-high heat.",
      "Add chopped onions and cook until softened, then add sliced carrots and cook for an additional 3 minutes.",
      "Add the chicken to the pan and cook until browned on both sides and cooked through.",
      "Stir in canned tomatoes and cooked rice. Serve hot.",
    ],
    date: "2024-03-13",
  },
];

const HistoryRecipes: React.FC = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [recipes, setRecipes] = useState(historyList);
  const [showFavoritesOnly, setShowFavoritesOnly] = useState(false);
  const [sortOrder, setSortOrder] = useState<"newest" | "oldest">("newest");

  const handleToggleFavorite = (targetRecipe: Recipe) => {
    setRecipes(
      recipes.map((recipe) =>
        recipe === targetRecipe
          ? { ...recipe, isFavorite: !recipe.isFavorite }
          : recipe
      )
    );
  };

  const handleSearch = () => {
    const filtered = historyList.filter(
      (recipe) =>
        recipe.recipe_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        recipe.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
        recipe.recipe_ingredients.some((ing) =>
          ing.ingredient_name.toLowerCase().includes(searchTerm.toLowerCase())
        )
    );
    setRecipes(filtered);
    setPage(1);
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === "Enter") {
      handleSearch();
    }
  };

  const applyFilters = () => {
    let filtered = [...historyList];

    if (showFavoritesOnly) {
      filtered = filtered.filter((recipe) => recipe.isFavorite);
    }

    filtered.sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return sortOrder === "newest" ? dateB - dateA : dateA - dateB;
    });

    if (searchTerm) {
      filtered = filtered.filter(
        (recipe) =>
          recipe.recipe_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          recipe.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
          recipe.recipe_ingredients.some((ing) =>
            ing.ingredient_name.toLowerCase().includes(searchTerm.toLowerCase())
          )
      );
    }

    setRecipes(filtered);
    setPage(1);
  };

  useEffect(() => {
    applyFilters();
  }, [showFavoritesOnly, sortOrder]);

  const rowsPerPage = 10;
  const pageCount = Math.ceil(recipes.length / rowsPerPage);

  if (recipes.length === 0) {
    return (
      <Box className="history-container empty">
        <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
          gap={2}
        >
          <Typography
            variant="h3"
            fontWeight="bold"
            gutterBottom
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <MenuBookIcon sx={{ fontSize: 45, marginRight: 1 }} />
            Cooking History
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" gutterBottom>
            Your cooking journey starts here!
          </Typography>
          <Button
            variant="contained"
            color="success"
            sx={{ color: "white" }}
            onClick={() => navigate("/cook")}
          >
            Generate Your First Recipe
          </Button>
        </Box>
      </Box>
    );
  }

  return (
    <Box className="history-container has-content">
      <div className="history-header">
        <Typography
          variant="h3"
          fontWeight="bold"
          gutterBottom
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <MenuBookIcon sx={{ fontSize: 45, marginRight: 1 }} />
          Cooking History
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
          View your past recipes and track what you've cooked!
        </Typography>
      </div>

      <Box sx={{ mb: 3, display: "flex", gap: 2, alignItems: "center" }}>
        <TextField
          placeholder="Search recipes..."
          size="small"
          color="success"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyDown={handleKeyPress}
          inputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <SearchIcon sx={{ color: "action.active" }} />
              </InputAdornment>
            ),
          }}
          sx={{
            flexGrow: 1,
            "& .MuiInputBase-root": {
              height: "36px",
            },
          }}
        />

        <Button
          variant={showFavoritesOnly ? "contained" : "outlined"}
          color="success"
          // onClick={() => setShowFavoritesOnly(!showFavoritesOnly)}
          startIcon={<FavoriteIcon />}
          sx={{
            height: "36px",
            textTransform: "none",
            px: 2,
          }}
        >
          Favorites
        </Button>

        <ToggleButtonGroup
          color="success"
          value={sortOrder}
          exclusive
          onChange={(_, newValue) => newValue && setSortOrder(newValue)}
          aria-label="recipe sort order"
          sx={{
            height: "36px",
            "& .MuiToggleButton-root": {
              textTransform: "none",
              px: 2,
              "&.Mui-selected": {
                color: "white",
                backgroundColor: "success.main",
                "&:hover": {
                  backgroundColor: "success.dark",
                },
              },
            },
          }}
        >
          <ToggleButton value="newest">Newest</ToggleButton>
          <ToggleButton value="oldest">Oldest</ToggleButton>
        </ToggleButtonGroup>
      </Box>

      <RecipeTable
        recipes={recipes}
        page={page}
        rowsPerPage={rowsPerPage}
        onPageChange={(_, value) => setPage(value)}
        onToggleFavorite={handleToggleFavorite}
      />
    </Box>
  );
};

export default HistoryRecipes;