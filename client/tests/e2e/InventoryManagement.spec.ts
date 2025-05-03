import { expect, test } from "@playwright/test";


test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/inventory");
});

test("input box and buttons show on the inventory management page", async ({ page }) => {
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
    await page.route('**/api/users/john_doe/inventory', async route => {
        if (route.request().method() === 'POST') {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ success: true })
          });
        } else {
          route.continue();
        }
      });


    await page.getByRole('textbox', { name: 'add ingredient' }).fill('a');
    await page.getByLabel("add-btn").click();
    await page.getByRole('textbox', { name: 'add ingredient' }).fill('b');
    await page.getByLabel("add-btn").click();

    const a = page.getByRole('button', { name: 'a', exact: true });
    const b = page.getByRole('button', { name: 'b', exact: true });
    
    await expect(a).toBeVisible();
    await expect(b).toBeVisible();
});

test("delete btn can be used after item was selected", async ({ page }) => {
    await page.route('**/api/users/john_doe/inventory', async route => {
        if (route.request().method() === 'POST') {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ success: true })
          });
        } else {
          route.continue();
        }
      });

    await page.getByRole('textbox', { name: 'add ingredient' }).fill('a');
    await page.getByLabel("add-btn").click();
    await page.getByRole('textbox', { name: 'add ingredient' }).fill('b');
    await page.getByLabel("add-btn").click();

    const a = page.getByRole('button', { name: 'a', exact: true });
    const b = page.getByRole('button', { name: 'b', exact: true });

    await expect(a).toBeVisible();
    await expect(b).toBeVisible();

    await a.click();
    await b.click();

    // mock DELETE inventory requests. should be changed to real username later
    await page.route('**/api/users/john_doe/inventory/**', async route => {
        if (route.request().method() === 'DELETE') {
          await route.fulfill({
            status: 204,
          });
        } else {
          route.continue();
        }
      });

    await page.getByLabel("delete-btn").click();

    await expect(a).not.toBeVisible();
    await expect(b).not.toBeVisible();
});

test('should navigate back to the homepage when clicking Done', async ({ page }) => {
    await page.getByLabel("done-btn").click();
    await expect(page).toHaveURL('http://localhost:5173/');
  });
