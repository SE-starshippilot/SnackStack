
import { test, expect, APIRequestContext } from "@playwright/test";
import { setupClerkTestingToken } from "@clerk/testing/playwright";
import { Client } from "pg";

const API_URL = "http://localhost:8080/api/users";
const USER = process.env.E2E_CLERK_USER_USERNAME!;
const PASS = process.env.E2E_CLERK_USER_PASSWORD!;

// Database cleanup after all tests
async function cleanupTestUser() {
  const db = new Client({
    connectionString: process.env.DATABASE_URL,
  });
  await db.connect();
  await db.query(
    `DELETE FROM users WHERE email = $1`,
    [USER]
  );
  await db.end();
}

test.describe("Clerk Sign-In",
   () => {
  test.beforeEach(async ({ page, context }) => {
    await context.clearCookies();
    await page.addInitScript(() => localStorage.clear());
    await setupClerkTestingToken({ page });
    // *** STUB the backend profile-check to always “not found” ***
    await page.route("**/api/users/email/*", (route) =>
      route.fulfill({ status: 404, contentType: "application/json", body: "{}" })
    );

    await page.goto("/");
  });




  test("sign in with email + password adding first/last names", async ({ page, request }) => {
    // open
    await page.getByRole("button", { name: /^sign in$/i }).click();
    const dialog = page.getByRole("dialog");
    await expect(dialog).toBeVisible();

    // email
    const emailInput = dialog.getByRole("textbox", { name: /email/i });
    await expect(emailInput).toBeVisible();
    await emailInput.fill(USER);
    await emailInput.press("Enter");

    // password
    const pwdInput = dialog.getByPlaceholder("Enter your password");
    await expect(pwdInput).toBeVisible();
    await pwdInput.fill(PASS);
    await pwdInput.press("Enter");

    await page.waitForURL("**/complete-profile");
    await expect(
      page.getByRole("heading", { name: "Complete Your Profile" })
    ).toBeVisible();

    // fill in the user name
    const firstNameInput = page.getByRole("textbox", { name: /username/i });
    await firstNameInput.fill("Cat");

    await page.getByRole("button", { name: /save & continue/i }).click();

    await page.waitForTimeout(1000);

    // back on your app, signed in
    await expect(
      page.getByRole("button", { name: /^sign out$/i })
    ).toBeVisible();
  

  await page.waitForURL("**/");

    // --- now use the `request` fixture to GET the newly created user ---
    console.log(`${API_URL}/email/${encodeURIComponent(USER)}`);
    const apiRes = await request.get(`${API_URL}/email/${encodeURIComponent(USER)}`);
    expect(apiRes.status()).toBe(200);

    const userObj = await apiRes.json();
    expect(userObj).toHaveProperty("userId");
    expect(userObj.email).toBe(USER);
    expect(userObj.userName).toBe("Cat");
  });
  test.afterAll(async () => {
    await cleanupTestUser();
  });
});

test.describe("Protected routes (signed out)", () => {
  test.beforeEach(async ({ page, context }) => {
    // make sure we’re signed out
    await context.clearCookies();
    await page.addInitScript(() => localStorage.clear());
  });

  for (const route of ["/cook", "/inventory"]) {
    test(`GET ${route} shows login prompt`, async ({ page }) => {
      await page.goto(route);

      const dialog = page.getByRole("dialog");
      await expect(dialog).toBeVisible();

 
      await expect(
        page.getByRole("heading", { name: "Sign in required" })
      ).toBeVisible();

      await expect(
        page.getByText("You need to be logged in to access this feature.")
      ).toBeVisible();

      await expect(
        page.getByRole("button", { name: "Sign In" })
      ).toBeVisible();
      await expect(
        page.getByRole("button", { name: "Cancel" })
      ).toBeVisible();
    });
  }
});