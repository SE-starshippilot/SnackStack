import React, { useEffect, useState } from "react";
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
import { useNavigate } from "react-router-dom";

// mocked fetch data
import recipesData from "../../data/recipes.json";

export default function FavoritesPage() {
  const [favorites, setFavorites] = useState<any[]>([]);
  const navigate = useNavigate();


// uncomment once recipe_history is implemented
// and use the current user
//   useEffect(() => {
//     async function loadFavorites() {
//       const res = await fetch(
//         "http://localhost:8080/api/users/john_doe/recipe_history"
//       );
//       if (!res.ok) throw new Error(`HTTP ${res.status}`);
//       const history: { recipe_id: number }[] = await res.json();

//       const counts: Record<number, number> = {};
//       history.forEach(({ recipe_id }) => {
//         counts[recipe_id] = (counts[recipe_id] || 0) + 1;
//       });

//       const favIds = Object.entries(counts)
//         .filter(([_, count]) => count > 1)
//         .map(([id]) => Number(id));

//       const favs = recipesData.recipes.filter((r: any) =>
//         favIds.includes(r.recipe_id)
//       );
//       setFavorites(favs);
//     }

//     loadFavorites().catch((err) => console.error("Failed to load favorites:", err));
//   }, []);

useEffect(() => {
    const mockHistory = [
      "Spaghetti Carbonara",
      "Spaghetti Carbonara",
      "Tacos"
    ];
  
    const counts: Record<string, number> = {};
    mockHistory.forEach((name) => {
      counts[name] = (counts[name] || 0) + 1;
    });
  
    const favNames = Object.entries(counts)
      .filter(([, c]) => c > 1)
      .map(([name]) => name);
  
    const favs = recipesData.recipes.filter((r: any) =>
      favNames.includes(r.recipe_name)
    );
    setFavorites(favs);
  }, []);

  const backToCookPage = () => {
    navigate("/cook");
}

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={600} textAlign="center">
        Your Favorite Recipes
      </Typography>

      {favorites.length === 0 ? (
        <Typography textAlign="center">No favorites yet!</Typography>
      ) : (
        favorites.map((recipe: any, index: number) => (
          <Card
            key={index}
            sx={{ mb: 4, boxShadow: 3 }}
            data-testid={`recipe-card-${index}`}>
            <CardContent>
              <Typography variant="h5" fontWeight={600}>
                {recipe.recipe_name}
              </Typography>
              <Typography color="text.secondary" gutterBottom>
                {recipe.origin_name} · {recipe.servings} servings
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
                      {recipe.recipe_ingredients.map((ing: any, i: number) => (
                        <ListItem key={i}>
                          <ListItemText
                            primary={`${ing.ingredient_name} — ${ing.quantity} ${ing.unit || ""}`}
                            secondary={ing.note}
                          />
                        </ListItem>
                      ))}
                    </List>
                  </Box>

                  <Divider sx={{ my: 2 }} />

                  <Box>
                    <Typography variant="subtitle1" fontWeight={600}>
                      Steps
                    </Typography>
                    <List dense>
                      {recipe.recipe_steps.map((step: any, j: number) => (
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
          </Card>
        ))
      )}

            <Button
                aria-label="favorites-page-back-btn"
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
