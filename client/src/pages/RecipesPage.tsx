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
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  JSXElementConstructor,
  Key,
  ReactElement,
  ReactNode,
  ReactPortal,
  useEffect,
  useState,
} from "react";
import { useLocation, useNavigate } from "react-router-dom";
import type { Ingredient, Recipe, LocationState } from "../types/recipe";
import { useUserContext } from "../contexts/UserContext";

function RecipesPage() {
  const { state } = useLocation() as { state?: LocationState };
  const navigate = useNavigate();
  const [recipes, setRecipes] = useState<Recipe[]>(state?.recipes ?? []);
  const [selectedRecipeId, setSelectedRecipeId] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { username, isProfileComplete } = useUserContext();
  const [userId, setUserId] = useState<string>('');

  useEffect(() => {
    if (username && isProfileComplete) {
      fetchUserId();
    }
  }, [username, isProfileComplete]);

  const fetchUserId = async () => {

    try {
      const res = await fetch(`http://localhost:8080/api/users/${encodeURIComponent(username)}/id`);

      if (!res.ok) {
        console.warn(`User not found: ${username}`);
        return null;
      }

      const data = await res.json();
      setUserId(data.id)
    } catch (error) {
      console.error("Error fetching user ID:", error);
      return null;
    }
  };

  /* If the page is refreshed there is no state – redirect user back */
  useEffect(() => {
    if (state?.recipes?.length) {
      setRecipes(state.recipes);
    } else {
      // No recipes (e.g. page refresh or bad state) → send user back
      navigate("/cook", { replace: true });
    }
  }, [state, navigate]);

  const backToCookPage = () => {
    navigate("/cook");
  };

  const handleSelectRecipe = (recipeId: string) => {
    setSelectedRecipeId(recipeId);
  };

  const handleConfirmSelection = async () => {
    try {
      console.log(selectedRecipeId);
      console.log(userId);

      const payload = {
        userId: parseInt(userId, 10),
        recipeId: parseInt(selectedRecipeId, 10)
      };

      const res = await fetch("http://localhost:8080/api/history", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
      });

      const data = await res.json();
      if (data.id) {
        const selected = recipes.find((r) => r.uuid === selectedRecipeId);
        setRecipes(selected ? [selected] : []);
        setIsSubmitting(true);
      } else {
        alert("Fail to confirm chosen recipe.");
      }
    } catch (err) {
      console.error("Error confirming recipe:", err);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={600} textAlign="center">
        Your Generated Recipes
      </Typography>

      {recipes.map((recipe, index) => (
        <Card
          key={index}
          sx={{ mb: 4, boxShadow: 3 }}
          data-testid={`recipe-card-${index}`}>
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
              variant={selectedRecipeId === recipe.uuid ? "contained" : "outlined"}
              color="primary"
              onClick={() => handleSelectRecipe(recipe.uuid)}
              sx={{ mt: 2 }}
            >
              {selectedRecipeId === recipe.uuid ? "Selected" : "Select this recipe"}
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
            Confirm
          </Button>
        )}
      </Box>

    </Container>
  );
}

export default RecipesPage;
