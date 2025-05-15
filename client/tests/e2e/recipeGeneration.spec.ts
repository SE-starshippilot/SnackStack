import { expect, test } from "@playwright/test";
import { setupClerkTestingToken } from "@clerk/testing/playwright";
const USER = process.env.E2E_CLERK_USER_USERNAME!;
const PASS = process.env.E2E_CLERK_USER_PASSWORD!;
const MOCK_USERNAME = "Cat";
const MOCK_USER_ID = 123;

test.beforeEach(async ({ page, context }) => {
  await context.clearCookies();
  await page.addInitScript(() => localStorage.clear());
  await setupClerkTestingToken({ page });
  await page.route("**/api/users/email/*", (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        userId: MOCK_USER_ID,
        email: USER,
        userName: MOCK_USERNAME,
        isProfileComplete: true,
      }),
    });
  });

  await page.route("**/api/users/exists/email/*", (route) => {
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({ id: 123 }),
    });
  });
  // Go to the app and sign in
  await page.goto("http://localhost:5173/");

  // Open sign in dialog
  await page.getByRole("button", { name: /^sign in$/i }).click();
  const dialog = page.getByRole("dialog");
  await expect(dialog).toBeVisible();

  // Enter email
  const emailInput = dialog.getByRole("textbox", { name: /email/i });
  await expect(emailInput).toBeVisible();
  await emailInput.fill(USER);
  await emailInput.press("Enter");

  // Enter password
  const pwdInput = dialog.getByPlaceholder("Enter your password");
  await expect(pwdInput).toBeVisible();
  await pwdInput.fill(PASS);
  await pwdInput.press("Enter");

  // Since we're mocking the backend to say user exists,
  // we should go straight to the home page (no profile completion)
  await page.waitForURL("**/", { timeout: 5000 });

  // Verify we're logged in
  await expect(page.getByRole("button", { name: /^sign out$/i })).toBeVisible();

  await page.getByRole("button", { name: "Find Recipes", exact: true }).click();
});

test("basic elements show on the recipe generation page", async ({ page }) => {
  await expect(page.getByText("Generate Your Perfect Recipe")).toBeVisible();
  await expect(page.getByText("How many servings?")).toBeVisible();
  await expect(page.getByText("What kind of meal?")).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Generate Recipe" })
  ).toBeVisible();
});

test("should be able to adjust servings using slider", async ({ page }) => {
  const slider = page.getByRole("slider");
  await expect(slider).toBeVisible();
  await slider.focus();
  await page.keyboard.press("ArrowRight");
  await page.keyboard.press("ArrowRight");
  await expect(page.getByTestId("serving-value")).toHaveText("3");
});

test("select and unselect a meal type", async ({ page }) => {
  const mainBtn = page.getByRole("button", { name: "Main", exact: true });
  await expect(mainBtn).toBeVisible();
  await mainBtn.click();
  await mainBtn.click();
});

test("add and delete a custom preference", async ({ page }) => {
  const input = page.getByLabel("Add Preference");
  const addBtn = page.getByLabel("add-preference");

  await input.fill("CustomCuisine");
  await addBtn.click();
  const customChip = page.getByRole("button", {
    name: "CustomCuisine",
    exact: true,
  });
  await expect(customChip).toBeVisible();
  await customChip.locator("svg").click();
  await expect(customChip).not.toBeVisible();
});

test("add and delete a custom allergy", async ({ page }) => {
  const input = page.getByLabel("Add Allergy");
  const addBtn = page.getByLabel("add-allergy");

  await input.fill("CustomAllergy");
  await addBtn.click();
  const customChip = page.getByRole("button", {
    name: "CustomAllergy",
    exact: true,
  });
  await expect(customChip).toBeVisible();
  await customChip.locator("svg").click();
  await expect(customChip).not.toBeVisible();
});
