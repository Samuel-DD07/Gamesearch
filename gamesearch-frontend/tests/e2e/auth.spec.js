const { test, expect } = require('@playwright/test');

test.describe('Authentication', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
  });

  test('should display login form', async ({ page }) => {
    await expect(page.getByText('Administration')).toBeVisible();
    await expect(page.getByPlaceholder('Ex: admin')).toBeVisible();
    await expect(page.getByPlaceholder('••••••••')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Se connecter' })).toBeVisible();
  });

  test('should show error on invalid credentials', async ({ page }) => {
    await page.getByPlaceholder('Ex: admin').fill('wronguser');
    await page.getByPlaceholder('••••••••').fill('wrongpass');
    await page.getByRole('button', { name: 'Se connecter' }).click();

    // Fixing language issue identified in previous report
    await expect(page.getByText('Invalid username or password')).toBeVisible();
  });

  test('should login successfully with valid credentials', async ({ page }) => {
    await page.getByPlaceholder('Ex: admin').fill('admin');
    await page.getByPlaceholder('••••••••').fill('admin123');
    await page.getByRole('button', { name: 'Se connecter' }).click();

    await expect(page).toHaveURL(/\/admin/);
    await expect(page.getByText('Partner Dashboard')).toBeVisible();
  });

  test('should logout successfully', async ({ page }) => {
    // Login first
    await page.getByPlaceholder('Ex: admin').fill('admin');
    await page.getByPlaceholder('••••••••').fill('admin123');
    await page.getByRole('button', { name: 'Se connecter' }).click();
    await expect(page).toHaveURL(/\/admin/);

    await page.getByRole('button', { name: 'Logout' }).click();
    await expect(page).toHaveURL(/\/$/);
    await expect(page.getByText('Discover Your Next Game')).toBeVisible();
  });
});
