import React from "react";
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableHead,
  TableRow,
  TableCell,
} from "@mui/material";
import { Recipe } from "../../types/recipe";
import { COLORS, TYPOGRAPHY } from "../../styles/constants";

const TABLE_COLUMN_WIDTHS = {
  ingredient: "55%",
  quantity: "25%",
  unit: "20%",
} as const;

const tableStyles = {
  maxWidth: "600px",
  borderCollapse: "collapse",
  "& th, & td": {
    fontSize: "0.875rem",
    fontFamily: "inherit",
    borderBottom: "none",
  },
  "& thead th": {
    borderBottom: `1px solid ${COLORS.border}`,
    fontWeight: TYPOGRAPHY.fontWeights.semiBold,
  },
  border: `1px solid ${COLORS.border}`,
};

interface ExpandedRecipeDetailsProps {
  recipe: Recipe;
}

const ExpandedRecipeDetailsComponent: React.FC<ExpandedRecipeDetailsProps> = ({
  recipe,
}) => {
  return (
    <Box sx={{ margin: 2 }}>
      <Typography
        variant="h6"
        gutterBottom
        component="div"
        sx={{
          color: COLORS.text.primary,
          fontWeight: TYPOGRAPHY.fontWeights.semiBold,
        }}
      >
        Recipe Details
      </Typography>
      <Box sx={{ mb: 2 }}>
        <Table size="small" sx={tableStyles}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ width: TABLE_COLUMN_WIDTHS.ingredient }}>
                Ingredient
              </TableCell>
              <TableCell sx={{ width: TABLE_COLUMN_WIDTHS.quantity }}>
                Quantity
              </TableCell>
              <TableCell sx={{ width: TABLE_COLUMN_WIDTHS.unit }}>
                Unit
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {recipe.recipeIngredients.map((ingredient, index) => (
              <TableRow key={index}>
                <TableCell sx={{ width: TABLE_COLUMN_WIDTHS.ingredient }}>
                  {ingredient.ingredientName}
                  {ingredient.note && (
                    <span
                      style={{
                        marginLeft: "8px",
                        color: COLORS.text.secondary,
                        fontStyle: "italic",
                      }}
                    >
                      (Note: {ingredient.note})
                    </span>
                  )}
                </TableCell>
                <TableCell sx={{ width: TABLE_COLUMN_WIDTHS.quantity }}>
                  {ingredient.quantity}
                </TableCell>
                <TableCell sx={{ width: TABLE_COLUMN_WIDTHS.unit }}>
                  {ingredient.unit}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Box>
      <Box>
        <Typography
          variant="subtitle1"
          gutterBottom
          component="div"
          sx={{
            color: COLORS.text.primary,
            fontWeight: TYPOGRAPHY.fontWeights.semiBold,
          }}
        >
          Steps
        </Typography>
        <Box component="ol" sx={{ mt: 1, pl: 2, color: COLORS.text.primary }}>
          {recipe.recipeSteps.map((step, index) => (
            <li key={index} style={{ marginBottom: "8px" }}>
              {step}
            </li>
          ))}
        </Box>
      </Box>
    </Box>
  );
};

export const ExpandedRecipeDetails = React.memo(
  ExpandedRecipeDetailsComponent,
  (prevProps, nextProps) => {
    return prevProps.recipe.id === nextProps.recipe.id;
  }
);
