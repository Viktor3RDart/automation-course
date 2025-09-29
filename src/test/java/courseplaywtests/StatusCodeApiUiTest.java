package courseplaywtests;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeApiUiTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Настройка API контекста
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com")
        );

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );

        page = browser.newPage();

        // Навигация на страницу статус кодов один раз
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
    }

    @Test
    @Story("Практическое задание 23: API и UI-тесты")
    @Description("Сравнение статус-кодов, полученных через API и UI.")
    void testStatusCodesCombined() {
        int[] codes = {200, 404};
        for (int code : codes
        ) {
            assertEquals(getApiStatusCode(code), getUiStatusCode(code), "Коды не соответствуют");
        }
    }

    @Step("Получаем статус bp API запроса")
    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        assertEquals(response.status(), code,
                "Код отличается от искомого, ждём - " + code + ", получаем - " + response.status());
        return response.status();
    }

    @Step("Получаем статус с UI")
    private int getUiStatusCode(int code) {
        Locator link = page.locator("text=" + code).first();
        Response response = page.waitForResponse(
                res -> res.url().endsWith("/status_codes/" + code),
                () -> link.click(new Locator.ClickOptions().setTimeout(15000))
        );
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
        return response.status();
    }

    @AfterEach
    void tearDown() {
        apiRequest.dispose();
        page.close();
        browser.close();
        playwright.close();
    }
}
