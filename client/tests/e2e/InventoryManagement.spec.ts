import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/inventory");
});

test("input box shows on the inventory management page", async ({ page }) => {
    await expect(page.getByLabel("add-ingredient")).toBeVisible();
});