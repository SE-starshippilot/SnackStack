export interface RecipeIngredient {
  ingredient_name: string;
  quantity: number;
  unit: string;
  note?: string;
}

export interface Recipe {
  id: string;
  recipe_name: string;
  servings: number;
  description: string;
  origin_name: string;
  recipe_ingredients: RecipeIngredient[];
  recipe_steps: string[];
  date: string;
  isFavorite?: boolean;
}
