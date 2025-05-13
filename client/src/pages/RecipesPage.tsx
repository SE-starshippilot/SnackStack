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

function RecipesPage() {
  const { state } = useLocation() as { state?: LocationState };
  const navigate = useNavigate();
  const [recipes, setRecipes] = useState<Recipe[]>(state?.recipes ?? []);

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

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={600} textAlign="center">
        Your Generated Recipes
      </Typography>

      {recipes.map((recipe, index) => (
        <Card key={recipe.uuid} sx={{ mb: 4, boxShadow: 3 }}>
          <CardContent>
            <Typography variant="h5" fontWeight={600}>
              {recipe.recipeName}
            </Typography>
            <Typography color="text.secondary" gutterBottom>
              {recipe.originName || "Unknown origin"} · {recipe.servings}{" "}
              servings
            </Typography>
            <Typography variant="body1" sx={{ mt: 1, mb: 2 }}>
              {recipe.description}
            </Typography>

            <Accordion>
              <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                <Typography fontWeight={500}>View Details</Typography>
              </AccordionSummary>
              <AccordionDetails>
                {/* Ingredients */}
                <Box mb={2}>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Ingredients
                  </Typography>
                  <List dense>
                    {recipe.recipeIngredients.map(
                      (ing: Ingredient, i: number) => (
                        <ListItem key={i}>
                          <ListItemText
                            primary={`${ing.ingredientName} — ${ing.quantity} ${
                              ing.unit ?? ""
                            }`}
                            secondary={ing.note}
                          />
                        </ListItem>
                      )
                    )}
                  </List>
                </Box>

                <Divider sx={{ my: 2 }} />

                {/* Steps */}
                <Box>
                  <Typography variant="subtitle1" fontWeight={600}>
                    Steps
                  </Typography>
                  <List dense>
                    {recipe.recipeSteps.map((step: string, j: number) => (
                      <ListItem key={j}>
                        <ListItemText
                          primaryTypographyProps={{
                            style: { whiteSpace: "pre-line" },
                          }}
                          primary={`${j + 1}. ${step}`}
                        />
                      </ListItem>
                    ))}
                  </List>
                </Box>
              </AccordionDetails>
            </Accordion>
          </CardContent>
        </Card>
      ))}

      <Button
        aria-label="recipes-page-back-btn"
        variant="contained"
        color="success"
        sx={{ px: 4, py: 1.5, fontWeight: 500 }}
        onClick={backToCookPage}
      >
        Back
      </Button>
    </Container>
  );
}

export default RecipesPage;
