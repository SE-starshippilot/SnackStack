import React, { useState, useCallback } from "react";
import { TableRow, TableCell, IconButton, Collapse } from "@mui/material";
import IcecreamOutlinedIcon from "@mui/icons-material/IcecreamOutlined";
import { Recipe } from "../../types/recipe";
import {
  StyledTableRow,
  StyledTableCell,
} from "../../styles/RecipeTableStyles";
import { ExpandedRecipeDetails } from "./ExpandedRecipeDetails";
import { FavoriteButton } from "./FavoriteButton";

interface RecipeTableRowProps {
  row: Recipe;
  onToggleFavorite: (recipe: Recipe) => void;
}

const RecipeTableRowComponent: React.FC<RecipeTableRowProps> = ({
  row,
  onToggleFavorite,
}) => {
  const [open, setOpen] = useState(false);

  const handleClick = useCallback(() => {
    setOpen((prev) => !prev);
  }, []);

  return (
    <React.Fragment>
      <StyledTableRow
        onClick={handleClick}
        sx={{ "& > *": { borderBottom: "unset" } }}
      >
        <StyledTableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            sx={{
              transition: "transform 0.3s",
              transform: open ? "rotate(90deg)" : "rotate(0deg)",
              pointerEvents: "none",
            }}
          >
            <IcecreamOutlinedIcon />
          </IconButton>
        </StyledTableCell>
        <StyledTableCell>{row.recipe_name}</StyledTableCell>
        <StyledTableCell>{row.date}</StyledTableCell>
        <StyledTableCell>{row.description}</StyledTableCell>
        <StyledTableCell>
          {row.recipe_ingredients.map((i) => i.ingredient_name).join(", ")}
        </StyledTableCell>
        <StyledTableCell>
          <FavoriteButton recipe={row} onToggleFavorite={onToggleFavorite} />
        </StyledTableCell>
      </StyledTableRow>
      <TableRow>
        <StyledTableCell
          style={{ paddingBottom: 0, paddingTop: 0 }}
          colSpan={6}
        >
          <Collapse in={open} timeout="auto" unmountOnExit>
            <ExpandedRecipeDetails recipe={row} />
          </Collapse>
        </StyledTableCell>
      </TableRow>
    </React.Fragment>
  );
};

// Only re-render when row data or favorite status changes
export const RecipeTableRow = React.memo(
  RecipeTableRowComponent,
  (prevProps, nextProps) => {
    return (
      prevProps.row.id === nextProps.row.id &&
      prevProps.row.isFavorite === nextProps.row.isFavorite
    );
  }
);
