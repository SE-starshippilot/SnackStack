import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/recipes");
});

test("recipes box and back button will show on the screen", async ({ page }) => {
    await expect(page.getByText('Your Generated Recipes')).toBeVisible();
    await expect(page.getByLabel("recipes-page-back-btn")).toBeVisible();
    await expect(page.getByTestId('recipe-card-0')).toBeVisible();
    await expect(page.getByTestId('recipe-card-0')).toBeVisible();
    await expect(page.getByTestId('recipe-card-0')).toBeVisible();
});

test('should navigate back to the cook page when clicking back', async ({ page }) => {
    await page.getByLabel("recipes-page-back-btn").click();
    await expect(page).toHaveURL('http://localhost:5173/cook');
  });
