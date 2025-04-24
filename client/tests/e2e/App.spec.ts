import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
});

test("Inventory Management button shows on the home page", async ({ page }) => {
    await expect(page.getByLabel("inventory-management-btn")).toBeVisible();
});