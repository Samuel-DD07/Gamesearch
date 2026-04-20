const { test, expect } = require('@playwright/test');

test.describe('Home Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should display the main heading', async ({ page }) => {
    await expect(page.getByText('Discover Your Next Game')).toBeVisible();
  });

  test('should display search input and filters button', async ({ page }) => {
    await expect(page.getByPlaceholder('Search by title, publisher or description...')).toBeVisible();
    await expect(page.getByRole('button', { name: /Filters/i })).toBeVisible();
  });

  test('should filter games when searching', async ({ page }) => {
    const searchInput = page.getByPlaceholder('Search by title, publisher or description...');
    await searchInput.fill('Elden');
    // Wait for the debounce and check results
    await page.waitForTimeout(1000);
    // Assuming Elden Ring is in the catalog
    await expect(page.getByText(/Elden Ring/i).first()).toBeVisible();
  });
});
