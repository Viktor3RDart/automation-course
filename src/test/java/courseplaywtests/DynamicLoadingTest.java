package courseplaywtests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicLoadingTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @Test
    void testDynamicLoading() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        BrowserContext context = browser.newContext();
        // Настройка трассировки
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        page.onResponse(response -> {
            if (response.url().contains("/dynamic_loading")) {
                assertEquals(200, response.status(), "Ошибка : " + response.status());
            }
        });

        page.click("button");
        Locator finishText = page.locator("#finish");
        finishText.waitFor();
        assertTrue(finishText.textContent().contains("Hello World!"),
                "Ошибка текст не совпадает, найден иной текст - " + finishText.textContent());

        context.tracing().
                stop(new Tracing.StopOptions().
                        setPath(Paths.get("trace/trace-success.zip")));
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}