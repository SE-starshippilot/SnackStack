# Test info

- Name: Clerk Sign-In >> sign in with email + password adding first/last names
- Location: C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\Auth.spec.ts:29:3

# Error details

```
Error: locator.click: Target page, context or browser has been closed
Call log:
  - waiting for getByRole('button', { name: /^sign in$/i })

    at C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\Auth.spec.ts:31:60
```

# Test source

```ts
   1 | // // how to run:
   2 | // // VITE_DISABLE_AUTH=false npx playwright test tests/e2e/Auth.spec.ts
   3 |
   4 | import * as dotenv from "dotenv";
   5 | dotenv.config({ path: ".env.local" });
   6 |
   7 | import { test, expect } from "@playwright/test";
   8 | import { setupClerkTestingToken } from "@clerk/testing/playwright";
   9 |
  10 | const USER = process.env.E2E_CLERK_USER_USERNAME!;
  11 | const PASS = process.env.E2E_CLERK_USER_PASSWORD!;
  12 |
  13 |
  14 |
  15 | test.describe("Clerk Sign-In", () => {
  16 |     test.beforeEach(async ({ page, context }) => {
  17 |       //start clean 
  18 |       await context.clearCookies();
  19 |       await page.addInitScript(() => localStorage.clear());
  20 |   
  21 |       // enable Clerk in tests
  22 |       await setupClerkTestingToken({ page });
  23 |   
  24 |       // load the app
  25 |       await page.goto("/");
  26 |     });
  27 |
  28 |
  29 |   test("sign in with email + password adding first/last names", async ({ page }) => {
  30 |     // open
> 31 |     await page.getByRole("button", { name: /^sign in$/i }).click();
     |                                                            ^ Error: locator.click: Target page, context or browser has been closed
  32 |     const dialog = page.getByRole("dialog");
  33 |     await expect(dialog).toBeVisible();
  34 |
  35 |     // email 
  36 |     const emailInput = dialog.getByRole("textbox", { name: /email/i });
  37 |     await expect(emailInput).toBeVisible();
  38 |     await emailInput.fill(USER);
  39 |     await emailInput.press("Enter");
  40 |
  41 |     // password
  42 |     const pwdInput = dialog.getByPlaceholder("Enter your password");
  43 |     await expect(pwdInput).toBeVisible();
  44 |     await pwdInput.fill(PASS);
  45 |     await pwdInput.press("Enter");
  46 |
  47 |     await page.waitForURL("**/complete-profile");
  48 |     await expect(
  49 |     page.getByRole("heading", { name: "Complete Your Profile" })
  50 |   ).toBeVisible();
  51 |
  52 |     // fill in first/last names
  53 |     const firstNameInput = page.getByRole("textbox", { name: /first name/i });
  54 |   await firstNameInput.fill("Cat");
  55 |
  56 |   const lastNameInput = page.getByRole("textbox", { name: /last name/i });
  57 |   await lastNameInput.fill("Lazy");
  58 |
  59 |   await page.getByRole("button", { name: /save & continue/i }).click();
  60 |
  61 |   // back on your app, signed in
  62 |   await expect(
  63 |     page.getByRole("button", { name: /^sign out$/i })
  64 |   ).toBeVisible();
  65 | });
  66 | });
  67 |
```