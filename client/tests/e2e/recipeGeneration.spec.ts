import { expect, test } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:5173/cook");
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
