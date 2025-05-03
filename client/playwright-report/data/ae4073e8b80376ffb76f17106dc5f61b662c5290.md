# Test info

- Name: navigate from Home to Inventory
- Location: C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\App.spec.ts:7:1

# Error details

```
Error: page.goto: Test timeout of 30000ms exceeded.
Call log:
  - navigating to "http://localhost:5173/", waiting until "load"

    at C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\App.spec.ts:4:14
```

# Test source

```ts
   1 | import { expect, test } from "@playwright/test";
   2 |
   3 | test.beforeEach(async ({ page }) => {
>  4 |   await page.goto("http://localhost:5173/");
     |              ^ Error: page.goto: Test timeout of 30000ms exceeded.
   5 | });
   6 |
   7 | test("navigate from Home to Inventory", async ({ page }) => {
   8 |   const inventoryLink = page.getByRole("link", { name: /update inventory/i });
   9 |   await expect(inventoryLink).toBeVisible();
  10 |
  11 |   await inventoryLink.click();
  12 |   await expect(page).toHaveURL(/.*\/inventory/);
  13 | });
  14 |
  15 | test("navigate from Home to RecipeGeneration", async ({ page }) => {
  16 |   const cookLink = page.getByRole("link", { name: /find recipes/i });
  17 |   await expect(cookLink).toBeVisible();
  18 |
  19 |   await cookLink.click();
  20 |   await expect(page).toHaveURL(/.*\/cook/);
  21 | });
  22 |
```