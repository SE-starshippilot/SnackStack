import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/history");
});

test("basic elements show on the recipe generation page", async ({ page }) => {
    
    await expect(page.getByRole("heading", { name: /Cooking History/i })).toBeVisible();
  
    await expect(page.getByText(/View your past recipes and track what you've cooked!/i)).toBeVisible();
  
    await expect(page.getByPlaceholder("Search recipes...")).toBeVisible();
  
    // Favorites toggle button
    await expect(page.getByRole("button", { name: "Favorites" })).toBeVisible();

    await expect(page.getByRole("button", { name: "Newest" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Oldest" })).toBeVisible();
  });