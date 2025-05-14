import { expect, test } from "@playwright/test";


const TEST_EMAIL = "test@test.test";

test.beforeAll(async ({ request }) => {
  await request.post(`http://localhost:8080/api/users`, {
    data: { userName: "test_user", email: TEST_EMAIL },
  });
});


test.describe("Inventory (no mocks)", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/inventory");
    await expect(page.getByRole("heading", { name: "Inventory Management" })).toBeVisible();
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
  
  test("add items", async ({ page, request }) => {
      // should be changed to the real username later
      
      let res = await request.get(`http://localhost:8080/api/users/test_user/inventory`);
      expect(await res.json()).toEqual([]);
  
      // add via UI
      await page.getByRole("textbox", { name: /add ingredient/i }).fill("mushroom");
      await page.getByRole("button", { name: /^add$/i }).click();
      await expect(page.getByRole("button", { name: "mushroom" })).toBeVisible();
  
      // confirm real API has it
      res = await request.get(`http://localhost:8080/api/users/test_user/inventory`);
      expect(await res.json()).toContain("mushroom");
  
      // await page.getByRole('textbox', { name: 'add ingredient' }).fill('a');
      // await page.getByLabel("add-btn").click();
      // await page.getByRole('textbox', { name: 'add ingredient' }).fill('b');
      // await page.getByLabel("add-btn").click();
  
      // const a = page.getByRole('button', { name: 'a', exact: true });
      // const b = page.getByRole('button', { name: 'b', exact: true });
      
      // await expect(a).toBeVisible();
      // await expect(b).toBeVisible();
  });
  

  test("delete btn can be used after item was selected", async ({ page, request }) => {
    await request.post(`http://localhost:8080/api/users/test_user/inventory`, {
      data: { ingredientName: "a" },
    });
    await request.post(`http://localhost:8080/api/users/test_user/inventory`, {
      data: { ingredientName: "b" },
    });
  
  
    await page.goto("/inventory");
    await expect(page.getByRole("button", { name: "a", exact: true })).toBeVisible();
    await expect(page.getByRole("button", { name: "b", exact: true })).toBeVisible();
  
    // 3) Click them and Delete
    await page.getByRole("button", { name: "a", exact: true }).click();
    await page.getByRole("button", { name: "b", exact: true }).click();
    await page.getByRole("button", { name: /delete/i }).click();
  
    // 4) Assert they’re gone in the UI
    await expect(page.getByRole("button", { name: "a", exact: true })).not.toBeVisible();
    await expect(page.getByRole("button", { name: "b", exact: true })).not.toBeVisible();
  
    // 5) And confirm the real backend is empty
    const res = await request.get("http://localhost:8080/api/users/test_user/inventory");
    expect(await res.json()).toEqual([]);
  });
  
  test('should navigate back to the homepage when clicking Done', async ({ page }) => {
      await page.getByLabel("done-btn").click();
      await expect(page).toHaveURL('http://localhost:5173/');
    });
  
  });
  

  // test.afterAll(async ({ request }) => {
  //   // tear down
  //   await request.delete(
  //     `http://localhost:8080/api/users/${encodeURIComponent(TEST_EMAIL)}`
  //   );
  // });

