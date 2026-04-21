const { test, expect } = require('@playwright/test');

test.describe('Admin Dashboard', () => {
  test.beforeEach(async ({ page }) => {
    // Login as admin
    await page.goto('/login');
    await page.getByPlaceholder('Ex: admin').fill('admin');
    await page.getByPlaceholder('••••••••').fill('admin123');
    await page.getByRole('button', { name: 'Se connecter' }).click();
    await expect(page).toHaveURL(/\/admin/);
  });

  test('should display ingestion dashboard', async ({ page }) => {
    await expect(page.getByText('Partner Dashboard')).toBeVisible();
    await expect(page.getByText('Catalog Ingestion')).toBeVisible();
    await expect(page.getByText('System Status')).toBeVisible();
  });

  test('should show sync button and allow triggering sync', async ({ page }) => {
    const syncBtn = page.getByRole('button', { name: /Sync Now/i });
    await expect(syncBtn).toBeVisible();
    await syncBtn.click();
    
    // Check for monitor appearance
    await expect(page.getByText('Live Ingestion Monitor')).toBeVisible();
  });
});
