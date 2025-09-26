package com.example;

import com.microsoft.playwright.*;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import com.example.config.EnvironmentConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeTest {
    private EnvironmentConfig config;
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setUp() {
        // Загружаем конфиг с учетом env (dev/prod)
        config = ConfigFactory.create(EnvironmentConfig.class, System.getenv());
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    @Story("Практическое задание 22: Настройте тесты для разных окружений")
    @Description("Тест для проверки статусных кодов страницы /status_codes")
    void testStatus200() {
        // Response добавлен в тест вместо page.evaluate("window.status"),
        // так как windows.status сейчас в основном возвращает "", а не конкретный статус
        Response response;
        // Добавлено для того чтобы джоба в Actions не падала
        if (config.baseUrl() == null) {
            response = page.navigate("https://the-internet.herokuapp.com" + "/status_codes/200");
        } else {
            response = page.navigate(config.baseUrl() + "/status_codes/200");
        }
        assertEquals(200, response.status(), "Должен вернуться код 200");
//      assertEquals(200, page.evaluate("window.status"), "Должен вернуться код 200");

    }
}