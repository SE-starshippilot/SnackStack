// tests/e2e/global-setup.ts
import * as dotenv from "dotenv";
import path from "path";
import { chromium, FullConfig } from "@playwright/test";
import { clerkSetup, clerk } from "@clerk/testing/playwright";

dotenv.config({ path: ".env.local" });


export default async function globalSetup(_config: FullConfig) {
  await clerkSetup();

  const baseURL = "http://localhost:5173";

  // dump the auth JSON
  const storagePath = path.join( process.cwd(),
  "tests",
  "e2e",
  ".auth",
  "user.json");

  // launch browser
  const browser = await chromium.launch();
  const context = await browser.newContext();
  const page = await context.newPage();

  // Clerk sign-in:
  await page.goto(baseURL);
  await clerk.signIn({
    page,
    signInParams: {
      strategy:   "password",
      identifier: process.env.E2E_CLERK_USER_USERNAME!,
      password:   process.env.E2E_CLERK_USER_PASSWORD!,
    },
  });

  // save storage state 
  await context.storageState({ path: storagePath });
  await browser.close();
}
