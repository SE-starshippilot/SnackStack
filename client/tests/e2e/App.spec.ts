import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
});

test("Inventory Management button shows on the home page", async ({ page }) => {
    await expect(page.getByLabel("inventory-management-btn")).toBeVisible();
});

test('should navigate to the inventory page when clicking Inventory Management btn', async ({ page }) => {
    await page.getByLabel("inventory-management-btn").click();
    await expect(page).toHaveURL('http://localhost:5173/inventory');
  });