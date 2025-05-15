# Test info

- Name: recipes box and back button will show on the screen
- Location: /Users/shitianhao/Documents/SnackStack/client/tests/e2e/RecipeHistory.spec.ts:62:1

# Error details

```
Error: Timed out 5000ms waiting for expect(locator).toBeVisible()

Locator: getByText('Your Generated Recipes')
Expected: visible
Received: <element(s) not found>
Call log:
  - expect.toBeVisible with timeout 5000ms
  - waiting for getByText('Your Generated Recipes')

    at /Users/shitianhao/Documents/SnackStack/client/tests/e2e/RecipeHistory.spec.ts:65:58
```

# Page snapshot

```yaml
- banner:
  - navigation:
    - text: Snack Stack
    - link "Home":
      - /url: /
    - link "Cook":
      - /url: /cook
    - link "Inventory":
      - /url: /inventory
    - link "History":
      - /url: /history
    - button "Open user button":
      - img "Cat Lazy's logo"
    - button "Sign Out"
- heading "Generate Your Perfect Recipe" [level=5]
- paragraph: Based on your preferences, dietary needs, and available ingredients.
- heading "How many servings? *" [level=6]
- slider: "1"
- paragraph: "1"
- heading "What kind of meal? *" [level=6]
- button "Main"
- button "Appetizer"
- button "Dessert"
- button "Breakfast"
- button "Snack"
- heading "Preferences" [level=6]
- button "American"
- button "Italian"
- button "Chinese"
- button "Mexican"
- button "Japanese"
- button "Indian"
- button "Middle Eastern"
- button "Thai"
- text: Add Preference
- textbox "Add Preference"
- button "add-preference": Add
- heading "Allergies" [level=6]
- button "Gluten"
- button "Dairy"
- button "Nuts"
- button "Eggs"
- button "Soy"
- button "Shellfish"
- text: Add Allergy
- textbox "Add Allergy"
- button "add-allergy": Add
- heading "Notes (Optional)" [level=6]
- textbox "Any specific preferences or instructions?"
- button "Generate Recipe"
- contentinfo:
  - paragraph: © 2025 Snack Stack. All rights reserved.
```

# Test source

```ts
   1 | import { expect, test } from "@playwright/test";
   2 | import { setupClerkTestingToken } from "@clerk/testing/playwright";
   3 | const USER = process.env.E2E_CLERK_USER_USERNAME!;
   4 | const PASS = process.env.E2E_CLERK_USER_PASSWORD!;
   5 | const MOCK_USERNAME = "Cat";
   6 | const MOCK_USER_ID = 123;
   7 |
   8 | test.beforeEach(async ({ page, context }) => {
   9 |   await context.clearCookies();
  10 |   await page.addInitScript(() => localStorage.clear());
  11 |   await setupClerkTestingToken({ page });
  12 |   await page.route("**/api/users/email/*", (route) => {
  13 |     route.fulfill({
  14 |       status: 200,
  15 |       contentType: "application/json",
  16 |       body: JSON.stringify({
  17 |         userId: MOCK_USER_ID,
  18 |         email: USER,
  19 |         userName: MOCK_USERNAME,
  20 |         isProfileComplete: true,
  21 |       }),
  22 |     });
  23 |   });
  24 |
  25 |   await page.route("**/api/users/exists/email/*", (route) => {
  26 |     route.fulfill({
  27 |       status: 200,
  28 |       contentType: "application/json",
  29 |       body: JSON.stringify({ id: 123 }),
  30 |     });
  31 |   });
  32 |   // Go to the app and sign in
  33 |   await page.goto("http://localhost:5173/");
  34 |
  35 |   // Open sign in dialog
  36 |   await page.getByRole("button", { name: /^sign in$/i }).click();
  37 |   const dialog = page.getByRole("dialog");
  38 |   await expect(dialog).toBeVisible();
  39 |
  40 |   // Enter email
  41 |   const emailInput = dialog.getByRole("textbox", { name: /email/i });
  42 |   await expect(emailInput).toBeVisible();
  43 |   await emailInput.fill(USER);
  44 |   await emailInput.press("Enter");
  45 |
  46 |   // Enter password
  47 |   const pwdInput = dialog.getByPlaceholder("Enter your password");
  48 |   await expect(pwdInput).toBeVisible();
  49 |   await pwdInput.fill(PASS);
  50 |   await pwdInput.press("Enter");
  51 |
  52 |   // Since we're mocking the backend to say user exists,
  53 |   // we should go straight to the home page (no profile completion)
  54 |   await page.waitForURL("**/", { timeout: 5000 });
  55 |
  56 |   // Verify we're logged in
  57 |   await expect(page.getByRole("button", { name: /^sign out$/i })).toBeVisible();
  58 |
  59 |   await page.getByRole("button", { name: "Find Recipes", exact: true }).click();
  60 | });
  61 |
  62 | test("recipes box and back button will show on the screen", async ({
  63 |   page,
  64 | }) => {
> 65 |   await expect(page.getByText("Your Generated Recipes")).toBeVisible();
     |                                                          ^ Error: Timed out 5000ms waiting for expect(locator).toBeVisible()
  66 |   await expect(page.getByLabel("recipes-page-back-btn")).toBeVisible();
  67 |   await expect(page.getByTestId("recipe-card-0")).toBeVisible();
  68 |   await expect(page.getByTestId("recipe-card-0")).toBeVisible();
  69 |   await expect(page.getByTestId("recipe-card-0")).toBeVisible();
  70 | });
  71 |
  72 | test("should navigate back to the cook page when clicking back", async ({
  73 |   page,
  74 | }) => {
  75 |   await page.getByLabel("recipes-page-back-btn").click();
  76 |   await expect(page).toHaveURL("http://localhost:5173/cook");
  77 | });
  78 |
```