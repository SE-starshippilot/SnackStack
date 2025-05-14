# Test info

- Name: Inventory (no mocks) >> delete btn can be used after item was selected
- Location: C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\InventoryManagement.spec.ts:59:3

# Error details

```
Error: Timed out 5000ms waiting for expect(locator).toBeVisible()

Locator: getByRole('button', { name: 'a', exact: true })
Expected: visible
Received: <element(s) not found>
Call log:
  - expect.toBeVisible with timeout 5000ms
  - waiting for getByRole('button', { name: 'a', exact: true })

    at C:\Users\s_mar\cs32\SnackStack\client\tests\e2e\InventoryManagement.spec.ts:69:72
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
- heading "Inventory Management" [level=1]
- text: "Failed to fetch ingredients: Request failed with status code 500 add ingredient"
- textbox "add ingredient"
- button "add-btn": Add
- button "delete-btn" [disabled]: DELETE
- button "done-btn": Done
- contentinfo:
  - paragraph: © 2025 Snack Stack. All rights reserved.
```

# Test source

```ts
   1 | import { expect, test } from "@playwright/test";
   2 |
   3 |
   4 | const TEST_EMAIL = "test@test.test";
   5 |
   6 | test.beforeAll(async ({ request }) => {
   7 |   await request.post(`http://localhost:8080/api/users`, {
   8 |     data: { userName: "test_user", email: TEST_EMAIL },
   9 |   });
   10 | });
   11 |
   12 |
   13 | test.describe("Inventory (no mocks)", () => {
   14 |   test.beforeEach(async ({ page }) => {
   15 |     // skip Clerk UI; your .env.local has VITE_DISABLE_AUTH=true & VITE_TEST_USERNAME=test_user
   16 |     await page.goto("/inventory");
   17 |     await expect(page.getByRole("heading", { name: "Inventory Management" })).toBeVisible();
   18 |   });
   19 |
   20 |     test("input box and buttons show on the inventory management page", async ({ page }) => {
   21 |       await expect(page.getByLabel("add-ingredient")).toBeVisible();
   22 |       await expect(page.getByLabel("add-btn")).toBeVisible();
   23 |       await expect(page.getByLabel("delete-btn")).toBeVisible();
   24 |       await expect(page.getByLabel("done-btn")).toBeVisible();
   25 |   });
   26 |   
   27 |   test("delete btn is disabled before item was selected", async ({ page }) => {
   28 |       await expect(page.getByLabel("delete-btn")).toBeDisabled();
   29 |   });
   30 |   
   31 |   test("add items", async ({ page, request }) => {
   32 |       // should be changed to the real username later
   33 |       
   34 |       let res = await request.get(`http://localhost:8080/api/users/test_user/inventory`);
   35 |       expect(await res.json()).toEqual([]);
   36 |   
   37 |       // add via UI
   38 |       await page.getByRole("textbox", { name: /add ingredient/i }).fill("mushroom");
   39 |       await page.getByRole("button", { name: /^add$/i }).click();
   40 |       await expect(page.getByRole("button", { name: "mushroom" })).toBeVisible();
   41 |   
   42 |       // confirm real API has it
   43 |       res = await request.get(`http://localhost:8080/api/users/test_user/inventory`);
   44 |       expect(await res.json()).toContain("mushroom");
   45 |   
   46 |       // await page.getByRole('textbox', { name: 'add ingredient' }).fill('a');
   47 |       // await page.getByLabel("add-btn").click();
   48 |       // await page.getByRole('textbox', { name: 'add ingredient' }).fill('b');
   49 |       // await page.getByLabel("add-btn").click();
   50 |   
   51 |       // const a = page.getByRole('button', { name: 'a', exact: true });
   52 |       // const b = page.getByRole('button', { name: 'b', exact: true });
   53 |       
   54 |       // await expect(a).toBeVisible();
   55 |       // await expect(b).toBeVisible();
   56 |   });
   57 |   
   58 |
   59 |   test("delete btn can be used after item was selected", async ({ page, request }) => {
   60 |     await request.post(`http://localhost:8080/api/users/test_user/inventory`, {
   61 |       data: { ingredientName: "a" },
   62 |     });
   63 |     await request.post(`http://localhost:8080/api/users/test_user/inventory`, {
   64 |       data: { ingredientName: "b" },
   65 |     });
   66 |   
   67 |   
   68 |     await page.goto("/inventory");
>  69 |     await expect(page.getByRole("button", { name: "a", exact: true })).toBeVisible();
      |                                                                        ^ Error: Timed out 5000ms waiting for expect(locator).toBeVisible()
   70 |     await expect(page.getByRole("button", { name: "b", exact: true })).toBeVisible();
   71 |   
   72 |     // 3) Click them and Delete
   73 |     await page.getByRole("button", { name: "a", exact: true }).click();
   74 |     await page.getByRole("button", { name: "b", exact: true }).click();
   75 |     await page.getByRole("button", { name: /delete/i }).click();
   76 |   
   77 |     // 4) Assert they’re gone in the UI
   78 |     await expect(page.getByRole("button", { name: "a", exact: true })).not.toBeVisible();
   79 |     await expect(page.getByRole("button", { name: "b", exact: true })).not.toBeVisible();
   80 |   
   81 |     // 5) And confirm the real backend is empty
   82 |     const res = await request.get("http://localhost:8080/api/users/test_user/inventory");
   83 |     expect(await res.json()).toEqual([]);
   84 |   });
   85 |   
   86 |   test('should navigate back to the homepage when clicking Done', async ({ page }) => {
   87 |       await page.getByLabel("done-btn").click();
   88 |       await expect(page).toHaveURL('http://localhost:5173/');
   89 |     });
   90 |   
   91 |   });
   92 |   
   93 |
   94 |   // test.afterAll(async ({ request }) => {
   95 |   //   // tear down
   96 |   //   await request.delete(
   97 |   //     `http://localhost:8080/api/users/${encodeURIComponent(TEST_EMAIL)}`
   98 |   //   );
   99 |   // });
  100 |
  101 |
```