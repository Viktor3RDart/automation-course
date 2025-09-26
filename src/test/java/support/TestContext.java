package support;

import com.microsoft.playwright.*;

public class TestContext {
    private final Playwright playwright;
    private final Browser browser;
    private final Page page;

    public TestContext() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    public Page getPage() {
        return page;
    }

    public void close() {
        browser.close();
        playwright.close();
    }
}