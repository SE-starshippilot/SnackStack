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
import { JSXElementConstructor, Key, ReactElement, ReactNode, ReactPortal, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

// mocked fetch data
import recipesData from "../../data/recipes.json";

function RecipesPage() {
    const [recipes, setRecipes] = useState<any[]>([]);
    const [selectedRecipeId, setSelectedRecipeId] = useState<number | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        // use fetch() to get real data later
        setRecipes(recipesData.recipes);
    }, []);

    const backToCookPage = () => {
        navigate("/cook");
    }

    const handleSelectRecipe = (recipeId: number) => {
        setSelectedRecipeId(recipeId);
    };

    const handleConfirmSelection = async () => {
        try {
            console.log(selectedRecipeId);
            // find all of the recipe_id that were not chosen by user
            const unselectedRecipeIds = recipes
                .filter((r) => r.recipe_id !== selectedRecipeId)
                .map((r) => r.recipe_id);

            const res = await fetch("http://localhost:8080/api/recipes", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(unselectedRecipeIds)
            });

            const data = await res.json();
            if (data.message) {
                const selected = recipes.find((r) => r.recipe_id === selectedRecipeId);
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
                                        {recipe.recipe_ingredients.map((ingredient: { ingredient_name: any; quantity: any; unit: any; note: string | number | boolean | ReactElement<any, string | JSXElementConstructor<any>> | Iterable<ReactNode> | ReactPortal | null | undefined; }, i: Key | null | undefined) => (
                                            <ListItem key={i}>
                                                <ListItemText
                                                    primary={`${ingredient.ingredient_name} — ${ingredient.quantity} ${ingredient.unit || ""}`}
                                                    secondary={ingredient.note}
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
                    <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
                        <Button
                            variant={selectedRecipeId === recipe.recipe_id ? "contained" : "outlined"}
                            color="primary"
                            onClick={() => handleSelectRecipe(recipe.recipe_id)}
                            sx={{ mt: 2 }}
                        >
                            {selectedRecipeId === recipe.recipe_id ? "Selected" : "Select this recipe"}
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
