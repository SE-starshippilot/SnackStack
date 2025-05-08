import React from "react";
import { IconButton } from "@mui/material";
import FavoriteIcon from "@mui/icons-material/Favorite";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import { Recipe } from "../../types/recipe";

interface FavoriteButtonProps {
  recipe: Recipe;
  onToggleFavorite: (recipe: Recipe) => void;
}

const FavoriteButtonComponent: React.FC<FavoriteButtonProps> = ({
  recipe,
  onToggleFavorite,
}) => {
  const handleClick = (event: React.MouseEvent) => {
    event.stopPropagation();
    onToggleFavorite(recipe);
  };

  return (
    <IconButton
      onClick={handleClick}
      size="small"
      sx={{ color: recipe.isFavorite ? "#e57373" : "rgba(0, 0, 0, 0.54)" }}
    >
      {recipe.isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
    </IconButton>
  );
};

export const FavoriteButton = React.memo(
  FavoriteButtonComponent,
  (prevProps, nextProps) => {
    return (
      prevProps.recipe.id === nextProps.recipe.id &&
      prevProps.recipe.isFavorite === nextProps.recipe.isFavorite
    );
  }
);
