export interface Ingredient {
  ingredientName: string;
  quantity: number;
  unit?: string;
  note?: string;
}

export interface Recipe {
  id: number;
  uuid: string;
  recipeName: string;
  servings: number;
  recipeDescription: string;
  originName: string;
  recipeIngredients: Ingredient[];
  recipeSteps: string[];
  isFavorite: boolean;
  createdAt: string;
}

export interface LocationState {
  recipes: Recipe[];
}