# Test info

- Name: select and unselect a meal type
- Location: C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\recipeGeneration.spec.ts:25:1

# Error details

```
Error: page.goto: Test timeout of 30000ms exceeded.
Call log:
  - navigating to "http://localhost:5173/cook", waiting until "load"

    at C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\recipeGeneration.spec.ts:4:14
```

# Test source

```ts
   1 | import { expect, test } from "@playwright/test";
   2 |
   3 | test.beforeEach(async ({ page }) => {
>  4 |   await page.goto("http://localhost:5173/cook");
     |              ^ Error: page.goto: Test timeout of 30000ms exceeded.
   5 | });
   6 |
   7 | test("basic elements show on the recipe generation page", async ({ page }) => {
   8 |   await expect(page.getByText("Generate Your Perfect Recipe")).toBeVisible();
   9 |   await expect(page.getByText("How many servings?")).toBeVisible();
  10 |   await expect(page.getByText("What kind of meal?")).toBeVisible();
  11 |   await expect(
  12 |     page.getByRole("button", { name: "Generate Recipe" })
  13 |   ).toBeVisible();
  14 | });
  15 |
  16 | test("should be able to adjust servings using slider", async ({ page }) => {
  17 |   const slider = page.getByRole("slider");
  18 |   await expect(slider).toBeVisible();
  19 |   await slider.focus();
  20 |   await page.keyboard.press("ArrowRight");
  21 |   await page.keyboard.press("ArrowRight");
  22 |   await expect(page.getByTestId("serving-value")).toHaveText("3");
  23 | });
  24 |
  25 | test("select and unselect a meal type", async ({ page }) => {
  26 |   const mainBtn = page.getByRole("button", { name: "Main", exact: true });
  27 |   await expect(mainBtn).toBeVisible();
  28 |   await mainBtn.click();
  29 |   await mainBtn.click();
  30 | });
  31 |
  32 | test("add and delete a custom preference", async ({ page }) => {
  33 |   const input = page.getByLabel("Add Preference");
  34 |   const addBtn = page.getByLabel("add-preference");
  35 |
  36 |   await input.fill("CustomCuisine");
  37 |   await addBtn.click();
  38 |   const customChip = page.getByRole("button", {
  39 |     name: "CustomCuisine",
  40 |     exact: true,
  41 |   });
  42 |   await expect(customChip).toBeVisible();
  43 |   await customChip.locator("svg").click();
  44 |   await expect(customChip).not.toBeVisible();
  45 | });
  46 |
  47 | test("add and delete a custom allergy", async ({ page }) => {
  48 |   const input = page.getByLabel("Add Allergy");
  49 |   const addBtn = page.getByLabel("add-allergy");
  50 |
  51 |   await input.fill("CustomAllergy");
  52 |   await addBtn.click();
  53 |   const customChip = page.getByRole("button", {
  54 |     name: "CustomAllergy",
  55 |     exact: true,
  56 |   });
  57 |   await expect(customChip).toBeVisible();
  58 |   await customChip.locator("svg").click();
  59 |   await expect(customChip).not.toBeVisible();
  60 | });
  61 |
```