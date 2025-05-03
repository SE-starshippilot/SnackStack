# Test info

- Name: delete btn is disabled before item was selected
- Location: C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\InventoryManagement.spec.ts:15:1

# Error details

```
Error: page.goto: Test timeout of 30000ms exceeded.
Call log:
  - navigating to "http://localhost:5173/inventory", waiting until "load"

    at C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\InventoryManagement.spec.ts:5:16
```

# Test source

```ts
   1 | import { expect, test } from "@playwright/test";
   2 |
   3 |
   4 | test.beforeEach(async ({ page }) => {
>  5 |     await page.goto("http://localhost:5173/inventory");
     |                ^ Error: page.goto: Test timeout of 30000ms exceeded.
   6 | });
   7 |
   8 | test("input box and buttons show on the inventory management page", async ({ page }) => {
   9 |     await expect(page.getByLabel("add-ingredient")).toBeVisible();
  10 |     await expect(page.getByLabel("add-btn")).toBeVisible();
  11 |     await expect(page.getByLabel("delete-btn")).toBeVisible();
  12 |     await expect(page.getByLabel("done-btn")).toBeVisible();
  13 | });
  14 |
  15 | test("delete btn is disabled before item was selected", async ({ page }) => {
  16 |     await expect(page.getByLabel("delete-btn")).toBeDisabled();
  17 | });
  18 |
  19 | test("add items", async ({ page }) => {
  20 |     await page.getByRole('textbox', { name: 'add ingredient' }).fill('a');
  21 |     await page.getByLabel("add-btn").click();
  22 |     await page.getByRole('textbox', { name: 'add ingredient' }).fill('b');
  23 |     await page.getByLabel("add-btn").click();
  24 |
  25 |     const a = page.getByRole('button', { name: 'a', exact: true });
  26 |     const b = page.getByRole('button', { name: 'b', exact: true });
  27 |     
  28 |     await expect(a).toBeVisible();
  29 |     await expect(b).toBeVisible();
  30 | });
  31 |
  32 | test("delete btn can be used after item was selected", async ({ page }) => {
  33 |     await page.getByRole('textbox', { name: 'add ingredient' }).fill('a');
  34 |     await page.getByLabel("add-btn").click();
  35 |     await page.getByRole('textbox', { name: 'add ingredient' }).fill('b');
  36 |     await page.getByLabel("add-btn").click();
  37 |
  38 |     const a = page.getByRole('button', { name: 'a', exact: true });
  39 |     const b = page.getByRole('button', { name: 'b', exact: true });
  40 |
  41 |     await expect(a).toBeVisible();
  42 |     await expect(b).toBeVisible();
  43 |
  44 |     await a.click();
  45 |     await b.click();
  46 |
  47 |     await page.getByLabel("delete-btn").click();
  48 |
  49 |     await expect(a).not.toBeVisible();
  50 |     await expect(b).not.toBeVisible();
  51 | });
  52 |
  53 | test('should navigate back to the homepage when clicking Done', async ({ page }) => {
  54 |     await page.getByLabel("done-btn").click();
  55 |     await expect(page).toHaveURL('http://localhost:5173/');
  56 |   });
  57 |
```