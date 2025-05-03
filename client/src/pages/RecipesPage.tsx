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

    const navigate = useNavigate();

    useEffect(() => {
        // use fetch() to get real data later
        setRecipes(recipesData.recipes);
    }, []);

    const backToCookPage = () => {
        navigate("/cook");
    }

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
