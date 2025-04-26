import { expect, test } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:5173/");
});

test("navigate from Home to Inventory", async ({ page }) => {
  const inventoryLink = page.getByRole("link", { name: /update inventory/i });
  await expect(inventoryLink).toBeVisible();

  await inventoryLink.click();
  await expect(page).toHaveURL(/.*\/inventory/);
});

test("navigate from Home to RecipeGeneration", async ({ page }) => {
  const cookLink = page.getByRole("link", { name: /find recipes/i });
  await expect(cookLink).toBeVisible();

  await cookLink.click();
  await expect(page).toHaveURL(/.*\/cook/);
});
