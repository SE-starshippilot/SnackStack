import FavoriteIcon from "@mui/icons-material/Favorite";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import SearchIcon from "@mui/icons-material/Search";
import {
  Box,
  Button,
  CircularProgress,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import ToggleButton from "@mui/material/ToggleButton";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
import React, { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { RecipeTable } from "../components/RecipeTable";
import "../styles/HistoryRecipes.css";
import { Ingredient, Recipe } from "../types/recipe";
import { useUserContext } from "../contexts/UserContext";

import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

// small generic hook
function useDebounce<T>(value: T, delay = 400) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const id = window.setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);
  return debounced;
}



const buildQueryString = (params: Record<string, unknown>) =>
  Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null)
    .map(
      ([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`
    )
    .join("&");

const HistoryRecipes: React.FC = () => {
  const navigate = useNavigate();
  const { dbUserId } = useUserContext();
  const rowsPerPage = 10;

  /* UI state ------------------------------------------------------- */
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [page, setPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [showFavoritesOnly, setShowFavoritesOnly] = useState(false);
  const [sortOrder, setSortOrder] = useState<"newest" | "oldest">("newest");

  const debouncedSearch = useDebounce(searchTerm, 400);

  /* request state -------------------------------------------------- */
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /* ----------------------------------------------------------------
   * Fetch helper
   * ---------------------------------------------------------------- */

  const fetchHistory = useCallback(async () => {
    if (!dbUserId) return; // no DB user yet
    setIsLoading(true);
    setError(null);

    try {
      const qs = buildQueryString({
        offset: (page - 1) * rowsPerPage,
        limit: rowsPerPage,
        sortAsc: sortOrder === "oldest",
        favoriteOnly: showFavoritesOnly,
        keyword: debouncedSearch,
      });

      const { data } = await axios.get<
        {
          id: number;
          userId: number;
          recipeId: number;
          recipeUuid: string;
          recipeName: string;
          recipeDescription: string;
          createdAt: string;
          isFavorite: boolean;
          recipeSteps: string[] | null;
          recipeIngredients: Ingredient[] | null;
        }[]
      >(`${API_BASE_URL}/history/${dbUserId}?${qs}`);

      console.log("Fetched history:", data);

      const mapped: Recipe[] = data.map((d) => ({
        id: d.id,
        uuid: String(d.recipeUuid),
        recipeName: d.recipeName,
        servings: 0, // backend doesn’t send this yet
        recipeDescription: d.recipeDescription ?? "",
        originName: "",
        recipeIngredients: d.recipeIngredients ?? [],
        recipeSteps: d.recipeSteps ?? [],
        historyId: d.id,
        createdAt: d.createdAt,
        isFavorite: d.isFavorite,
      }));

      setRecipes(mapped);
    } catch (err: any) {
      console.error(err);
      setError(
        err?.response?.data?.message ??
          "Could not fetch recipe history. Please try again."
      );
    } finally {
      setIsLoading(false);
    }
  }, [dbUserId, page, rowsPerPage, sortOrder, showFavoritesOnly, debouncedSearch]);

  useEffect(() => {
    fetchHistory();
  }, [fetchHistory]);
  

  /* optimistic favourite toggle ------------------------------------ */
  const handleToggleFavorite = async (row: Recipe) => {
    if (!dbUserId) return;
    setRecipes((prev) =>
      prev.map((r) =>
        r.id === row.id ? { ...r, isFavorite: !r.isFavorite } : r
      )
    );

    try {
      /* 2. call the new PUT  /api/history/:recipeUuid/favorite  endpoint */
      await axios.put(`${API_BASE_URL}/history/${row.uuid}/favorite`, {
        userId: dbUserId,
        favorite: !row.isFavorite, // <‑‑ the desired new state
      });
      await fetchHistory();
    } catch (err) {
      /* 3. roll back if request fails */
      console.error(err);
      if (axios.isAxiosError(err)) {
        // err is now typed as AxiosError<unknown>
        setError(
          err.response?.data?.message ??
            "Could not update favourite status. Please try again."
        );
      } else {
        setError("Could not update favourite status. Please try again.");
      }
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) =>
    e.key === "Enter" && setPage(1) && fetchHistory();

  /* loading / error / empty states --------------------------------- */
  if (!dbUserId)
    return (
      <Box
        className="history-container"
        display="flex"
        justifyContent="center"
        mt={8}
      >
        <Typography>Please sign in to view your cooking history.</Typography>
      </Box>
    );

  if (isLoading)
    return (
      <Box
        className="history-container"
        display="flex"
        justifyContent="center"
        mt={8}
      >
        <CircularProgress color="success" />
      </Box>
    );

  if (error)
    return (
      <Box
        className="history-container"
        display="flex"
        flexDirection="column"
        alignItems="center"
        mt={8}
      >
        <Typography color="error" gutterBottom>
          {error}
        </Typography>
        <Button variant="outlined" color="success" onClick={fetchHistory}>
          Retry
        </Button>
      </Box>
    );

  if (recipes.length === 0)
    return (
      <Box className="history-container empty">
        <Box display="flex" flexDirection="column" alignItems="center" gap={2}>
          <Typography variant="h3" fontWeight="bold">
            <MenuBookIcon sx={{ fontSize: 45, mr: 1 }} />
            Cooking History
          </Typography>
          <Typography variant="subtitle1" color="text.secondary">
            Your cooking journey starts here!
          </Typography>
          <Button
            variant="contained"
            color="success"
            onClick={() => navigate("/cook")}
          >
            Generate Your First Recipe
          </Button>
        </Box>
      </Box>
    );
  /* ----------------------------------------------------------------
   * Main render
   * ---------------------------------------------------------------- */
  return (
    <Box className="history-container has-content">
      <div className="history-header">
        <Typography variant="h3" fontWeight="bold" gutterBottom>
          <MenuBookIcon sx={{ fontSize: 45, mr: 1 }} />
          Cooking History
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
          View your past recipes and track what you've cooked!
        </Typography>
      </div>

      {/* Controls */}
      <Box sx={{ mb: 3, display: "flex", gap: 2, alignItems: "center" }}>
        <TextField
          placeholder="Search recipes..."
          size="small"
          color="success"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyDown={handleKeyPress}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <SearchIcon sx={{ color: "action.active" }} />
              </InputAdornment>
            ),
            style: { height: 36 },
          }}
          sx={{ flexGrow: 1 }}
        />

        <Button
          variant={showFavoritesOnly ? "contained" : "outlined"}
          color="success"
          startIcon={<FavoriteIcon />}
          onClick={() => {
            setShowFavoritesOnly((prev) => !prev);
            setPage(1);
          }}
          sx={{ height: 36, textTransform: "none", px: 2 }}
        >
          Favorites
        </Button>

        <ToggleButtonGroup
          color="success"
          value={sortOrder}
          exclusive
          onChange={(_, val) => val && setSortOrder(val)}
          aria-label="recipe sort order"
          sx={{
            height: 36,
            "& .MuiToggleButton-root": {
              textTransform: "none",
              px: 2,
              "&.Mui-selected": {
                color: "white",
                backgroundColor: "success.main",
                "&:hover": { backgroundColor: "success.dark" },
              },
            },
          }}
        >
          <ToggleButton value="newest">Newest</ToggleButton>
          <ToggleButton value="oldest">Oldest</ToggleButton>
        </ToggleButtonGroup>
      </Box>

      {/* Table */}
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