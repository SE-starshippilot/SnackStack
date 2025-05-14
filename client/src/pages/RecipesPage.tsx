import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Typography,
  List,
  ListItem,
  ListItemText,
  Divider,
  Snackbar,
  Alert,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { useUserContext } from "../contexts/UserContext"; // Import the UserContext hook
import type { Ingredient, Recipe, LocationState } from "../types/recipe";

const API_URL = "http://localhost:8080/api/history";

function RecipesPage() {
  const { state } = useLocation() as { state?: LocationState };
  const navigate = useNavigate();
  const { dbUserId } = useUserContext(); // Get user ID from context
  const [recipes, setRecipes] = useState<Recipe[]>(state?.recipes ?? []);
  const [selectedRecipeId, setSelectedRecipeId] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showSuccess, setShowSuccess] = useState(false);

  useEffect(() => {
    if (state?.recipes?.length) {
      setRecipes(state.recipes);
    } else {
      navigate("/cook", { replace: true });
    }
  }, [state, navigate]);

  const backToCookPage = () => {
    navigate("/cook");
  };

  const handleSelectRecipe = (uuid: string) => {
    setSelectedRecipeId(uuid);
  };

  const handleConfirmSelection = async () => {
    if (!selectedRecipeId) return;

    // Check if user is logged in
    if (!dbUserId) {
      setError("Please log in to save recipes to your history");
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      // Send request to add recipe to history using the user ID from context
      await axios.post(API_URL, {
        userId: dbUserId,
        recipeUuid: selectedRecipeId,
      });

      // Filter to keep only the selected recipe
      const selectedRecipe = recipes.find((r) => r.uuid === selectedRecipeId);
      if (selectedRecipe) {
        setRecipes([selectedRecipe]);
        setShowSuccess(true);

        // Navigate to details page after a short delay
        setTimeout(() => {
          navigate("/recipe-details", {
            state: { recipe: selectedRecipe },
          });
        }, 1500);
      }
    } catch (err) {
      console.error("Error confirming recipe:", err);
      setError("Failed to save recipe to history. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={600} textAlign="center">
        Your Generated Recipes
      </Typography>

      {recipes.map((recipe) => (
        <Card key={recipe.uuid} sx={{ mb: 4, boxShadow: 3 }}>
          <CardContent>
            <Typography variant="h5" fontWeight={600}>
              {recipe.recipeName}
            </Typography>
            <Typography color="text.secondary" gutterBottom>
              {recipe.originName} · {recipe.servings} servings
            </Typography>
            <Typography variant="body1" sx={{ mt: 1, mb: 2 }}>
              {recipe.description}
            </Typography>

            <Accordion>
              <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                <Typography fontWeight={500}>View Details</Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Box mb={2}>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Ingredients
                  </Typography>
                  <List dense>
                    {recipe.recipeIngredients.map(
                      (ing: Ingredient, i: number) => (
                        <ListItem key={i}>
                          <ListItemText
                            primary={`${ing.ingredientName} — ${ing.quantity} ${ing.unit ?? ""
                              }`}
                            secondary={ing.note}
                          />
                        </ListItem>
                      )
                    )}
                  </List>
                </Box>

                <Divider sx={{ my: 2 }} />

                <Box>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Steps
                  </Typography>
                  <List dense>
                    {recipe.recipeSteps.map((step: any, j: number) => (
                      <ListItem key={j}>
                        <ListItemText
                          primaryTypographyProps={{ style: { whiteSpace: "pre-line" } }}
                          primary={`${j + 1}. ${step}`}
                        />
                      </ListItem>
                    ))}
                  </List>
                </Box>
              </AccordionDetails>
            </Accordion>
          </CardContent>
          <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
            <Button
              variant={
                selectedRecipeId === recipe.uuid ? "contained" : "outlined"
              }
              color="primary"
              onClick={() => handleSelectRecipe(recipe.uuid)}
              sx={{ mt: 2 }}
            >
              {selectedRecipeId === recipe.uuid
                ? "Selected"
                : "Select this recipe"}
            </Button>
          </Box>
        </Card>
      ))}

      <Box sx={{ display: "flex", justifyContent: "space-between" }}>
        <Button
          aria-label="recipes-page-back-btn"
          variant="contained"
          color="success"
          sx={{ px: 4, py: 1.5, fontWeight: 500 }}
          onClick={backToCookPage}
        >
          Back
        </Button>

        {selectedRecipeId && (
          <Button
            aria-label="confirm-selection-btn"
            variant="contained"
            color="secondary"
            sx={{ px: 4, py: 1.5, fontWeight: 500 }}
            onClick={handleConfirmSelection}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Saving..." : "Confirm"}
          </Button>
        )}
      </Box>

      {/* Error and success notifications */}
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
      >
        <Alert severity="error" onClose={() => setError(null)}>
          {error}
        </Alert>
      </Snackbar>

      <Snackbar
        open={showSuccess}
        autoHideDuration={3000}
        onClose={() => setShowSuccess(false)}
      >
        <Alert severity="success" onClose={() => setShowSuccess(false)}>
          Recipe saved to your history!
        </Alert>
      </Snackbar>
    </Container>
  );
}

export default RecipesPage;
