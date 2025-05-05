import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/favorites");
});

test("favorites page and back button will show on the screen", async ({ page }) => {
  await expect(page.getByText('Your Favorite Recipes')).toBeVisible();
  await expect(page.getByLabel("favorites-page-back-btn")).toBeVisible();
  await expect(page.getByTestId('recipe-card-0')).toBeVisible();
});

test('should navigate back to the cook page when clicking back', async ({ page }) => {
  page.getByLabel("favorites-page-back-btn").click();
  await expect(page).toHaveURL('http://localhost:5173/cook');
});
