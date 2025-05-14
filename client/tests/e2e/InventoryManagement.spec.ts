/// <reference types="node" />
import { expect, test } from "@playwright/test";
import { setupClerkTestingToken } from "@clerk/testing/playwright";
const USER = process.env.E2E_CLERK_USER_USERNAME!;
const PASS = process.env.E2E_CLERK_USER_PASSWORD!;
const MOCK_USERNAME = "Cat";
const MOCK_USER_ID = 123;

// Create a new test fixture that includes authentication
test.beforeEach(async ({ page, context }) => {
  // Clear any existing session data
  await context.clearCookies();
  await page.addInitScript(() => localStorage.clear());

  // Setup Clerk testing token
  await setupClerkTestingToken({ page });

  // 1. MOCK USER CONTEXT - Simulate user already exists with username
  // This injects values into the UserContext that your component uses
  await page.addInitScript(() => {
    window.localStorage.setItem(
      "userProfile",
      JSON.stringify({
        username: "Cat",
        isProfileComplete: true,
      })
    );
  });

  // 2. MOCK USER API ENDPOINT - Return existing user data
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

  // 3. MOCK INVENTORY GET ENDPOINT - Return empty inventory initially
  await page.route(
    /\/api\/users\/[^/]+\/inventory(\/.*)?$/, // regex so trailing “/a” also matches
    async (route) => {
      switch (route.request().method()) {
        case "GET":
          return route.fulfill({
            status: 200,
            contentType: "application/json",
            body: "[]",
          });

        case "POST": {
          const { ingredientName } = JSON.parse(route.request().postData()!);
          return route.fulfill({
            status: 200,
            contentType: "application/json",
            body: JSON.stringify({
              success: true,
              message: `Added ${ingredientName}`,
            }),
          });
        }

        /* pre‑flight */
        case "OPTIONS":
          return route.fulfill({ status: 204 });

        /* actual delete */
        case "DELETE":
          return route.fulfill({ status: 204 });
      }
    }
  );

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

  // Go to the inventory management page
  await page
    .getByRole("button", { name: "Update Inventory", exact: true })
    .click();
});

test("input box and buttons show on the inventory management page", async ({
  page,
}) => {
  await expect(page.getByLabel("add-ingredient")).toBeVisible();
  await expect(page.getByLabel("add-btn")).toBeVisible();
  await expect(page.getByLabel("delete-btn")).toBeVisible();
  await expect(page.getByLabel("done-btn")).toBeVisible();
});

test("delete btn is disabled before item was selected", async ({ page }) => {
  await expect(page.getByLabel("delete-btn")).toBeDisabled();
});

test("add items", async ({ page }) => {
  // should be changed to the real username later
  await page.route("**/api/users/john_doe/inventory", async (route) => {
    if (route.request().method() === "POST") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ success: true }),
      });
    } else {
      route.continue();
    }
  });

  await page.getByRole("textbox", { name: "add ingredient" }).fill("a");
  await page.getByLabel("add-btn").click();
  await page.getByRole("textbox", { name: "add ingredient" }).fill("b");
  await page.getByLabel("add-btn").click();

  const a = page.getByRole("button", { name: "a", exact: true });
  const b = page.getByRole("button", { name: "b", exact: true });

  await expect(a).toBeVisible();
  await expect(b).toBeVisible();
});

test("delete btn can be used after item was selected", async ({ page }) => {
  await page.getByRole("textbox", { name: "add ingredient" }).fill("a");
  await page.getByLabel("add-btn").click();
  await page.getByRole("textbox", { name: "add ingredient" }).fill("b");
  await page.getByLabel("add-btn").click();

  const a = page.getByRole("button", { name: "a", exact: true });
  const b = page.getByRole("button", { name: "b", exact: true });

  await expect(a).toBeVisible();
  await expect(b).toBeVisible();

  await a.click();
  await b.click();

  await page.getByLabel("delete-btn").click();

  await expect(a).not.toBeVisible();
  await expect(b).not.toBeVisible();
});

test("should navigate back to the homepage when clicking Done", async ({
  page,
}) => {
  await page.getByLabel("done-btn").click();
  await expect(page).toHaveURL("http://localhost:5173/");
});
