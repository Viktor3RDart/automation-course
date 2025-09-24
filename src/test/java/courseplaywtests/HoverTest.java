package courseplaywtests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class HoverTest {
    static Playwright playwright;
    static Browser browser;
    Page page;

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setup() {
        page = browser.newContext().newPage();
    }

    @Test
    void testHoverProfiles() {
        page.navigate("https://the-internet.herokuapp.com/hovers");
        page.waitForLoadState(LoadState.LOAD);
        Locator figures = page.locator(".figure");
        for (int i = 0; i < figures.count(); i++) {
            Locator figure = figures.nth(i);

            figure.hover();
            Locator profileLink = figure.locator("text=View profile");
            assertTrue(profileLink.isVisible());

            profileLink.click();
            assertTrue(page.url().contains("/users/" + (i + 1)), "Открылся неверный URL - " + page.url());

            page.goBack();
        }
    }

    @AfterEach
    void tearDown() {
        page.close();
    }

    @AfterAll
    static void teardownClass() {
        browser.close();
        playwright.close();
    }
}