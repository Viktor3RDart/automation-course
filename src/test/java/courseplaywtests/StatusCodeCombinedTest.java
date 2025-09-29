package courseplaywtests;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import com.example.config.EnvConfig;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;


import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StatusCodeCombinedTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;
    private static EnvConfig config;
    private final String baseUrlJob = "https://the-internet.herokuapp.com";

    @BeforeAll
    static void loadConfig() {
        config = ConfigFactory.create(EnvConfig.class, System.getProperties());
    }

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // API контекст с базовым URL из конфига
        // Добавлено для того чтобы джоба в Actions не падала
        if (config.baseUrl() == null) {
            apiRequest = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL(baseUrlJob));
        } else {
            apiRequest = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL(config.baseUrl()));
        }

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(100)
        );

        page = browser.newPage();
        page.setDefaultTimeout(40000);
    }

    @ParameterizedTest
    @Story("Практическое задание 24: API и UI-тесты + Parameterized")
    @Description("Сравнение статус-кодов, полученных через API и UI.")
    @ValueSource(ints = {200, 404})
    void testStatusCodeCombined(int statusCode) {
        int apiStatusCode = getApiStatusCode(statusCode);
        int uiStatusCode = getUiStatusCode(statusCode);
        // API проверка
        assertEquals(statusCode, apiStatusCode,
                "API: Неверный статус код для " + statusCode);
        // UI проверка
        assertEquals(statusCode, uiStatusCode,
                "API: Неверный статус код для " + statusCode);

        // Сравнение результатов
        assertEquals(apiStatusCode, uiStatusCode, "Коды не соответствуют");
    }

    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        assertEquals(code, response.status(),
                "API: Неверный статус код для " + code);
        return response.status();
    }

    private int getUiStatusCode(int code) {
        try {
            // Навигация на страницу статус кодов
            // Добавлено для того чтобы джоба в Actions не падала
            if (config.baseUrl() == null) {
                page.navigate(baseUrlJob + "/status_codes");
            } else {
                page.navigate(config.baseUrl() + "/status_codes");
            }

            page.waitForSelector("div.example");

            // Локатор
            Locator link = page.locator(
                    String.format("a[href*='status_codes/%d']", code)
            ).first();

            // Перехват ответа перед кликом
            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click(new Locator.ClickOptions().setTimeout(10000))
            );

            return response.status();

        } catch (Exception e) {
            String errorName = e.getClass().getSimpleName();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String screenshotName = String.format("error_%s_%s.png", errorName, timestamp);
            // Скриншот с именем, включающим код ошибки
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(screenshotName))
                    .setFullPage(true));
            throw new RuntimeException("UI проверка упала для кода " + code, e);
        }
    }

    @AfterEach
    void teardown() {
        apiRequest.dispose();
        page.close();
        browser.close();
        playwright.close();
    }
}