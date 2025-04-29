// // how to run:
// // VITE_DISABLE_AUTH=false npx playwright test tests/e2e/Auth.spec.ts

import * as dotenv from "dotenv";
dotenv.config({ path: ".env.local" });

import { test, expect } from "@playwright/test";
import { setupClerkTestingToken } from "@clerk/testing/playwright";

const USER = process.env.E2E_CLERK_USER_USERNAME!;
const PASS = process.env.E2E_CLERK_USER_PASSWORD!;



test.describe("Clerk Sign-In", () => {
    test.beforeEach(async ({ page, context }) => {
      //start clean 
      await context.clearCookies();
      await page.addInitScript(() => localStorage.clear());
  
      // enable Clerk in tests
      await setupClerkTestingToken({ page });
  
      // load the app
      await page.goto("/");
    });


  test("sign in with email + password adding first/last names", async ({ page }) => {
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

    // fill in first/last names
    const firstNameInput = page.getByRole("textbox", { name: /first name/i });
  await firstNameInput.fill("Cat");

  const lastNameInput = page.getByRole("textbox", { name: /last name/i });
  await lastNameInput.fill("Lazy");

  await page.getByRole("button", { name: /save & continue/i }).click();

  // back on your app, signed in
  await expect(
    page.getByRole("button", { name: /^sign out$/i })
  ).toBeVisible();
});
});
