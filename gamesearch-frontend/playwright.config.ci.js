const { defineConfig, devices } = require('@playwright/test');

// Config dédiée au CI — diffère de playwright.config.js sur deux points :
// 1. Pas de webServer : l'app est démarrée par docker compose avant ce job.
// 2. baseURL pointe sur le hostname Docker interne "frontend" (port 80),
//    pas sur localhost:3000 qui n'est pas accessible depuis le container Playwright.
module.exports = defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  forbidOnly: true,
  retries: 2,
  workers: 1,
  reporter: [
    ['html', { open: 'never', outputFolder: 'playwright-report' }],
    ['junit', { outputFile: 'playwright-report/results.xml' }],
  ],
  use: {
    baseURL: process.env.PLAYWRIGHT_BASE_URL || 'http://frontend',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    viewport: { width: 1280, height: 720 },
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
