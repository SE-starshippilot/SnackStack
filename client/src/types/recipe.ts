export interface Ingredient {
  ingredientName: string;
  quantity: number;
  unit?: string;
  note?: string;
}

export interface Recipe {
  uuid: string;
  recipeName: string;
  servings: number;
  description: string;
  originName: string;
  recipeIngredients: Ingredient[];
  recipeSteps: string[];
}

export interface LocationState {
  recipes: Recipe[];
}