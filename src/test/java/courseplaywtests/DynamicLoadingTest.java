package courseplaywtests;

import com.microsoft.playwright.*;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый Класс DynamicLoadingTest демонстрирует пример авто теста
 * с использованием Playwright и JUnit5.
 * Функционал теста:
 *     Открывает страницу с динамической загрузкой.
 *     Проверяет корректность ответа сервера (код 200).
 *     Запускает процесс динамической загрузки, и ожидает результат.
 *     Проверяет, что результат содержит текст "Hello World!".
 *     Сохраняет трассировку выполнения (trace) для анализа.
 * Интеграция с Allure:
 * Для генерации отчётов используется Allure. После прогона тестов 
 * необходимо выполнить команду:
 *   mvn allure:serve,
 *  чтобы открыть HTML-отчёт.
 */

@Epic("Playwright Java: Автоматизация тестирования. Полный курс")
@Feature("11.4 Документирование")
public class DynamicLoadingTest {

    private Playwright playwright;
    private Browser browser;
    private Page page;

    /**
     * Основной тест динамической загрузки.
     * Шаги:
     *     Запуск браузера в не headless-режиме.
     *     Переход на страницу <a href="https://the-internet.herokuapp.com/dynamic_loading/1">Dynamic Loading</a>.
     *     Проверка, что сервер возвращает код ответа 200.
     *     Клик по кнопке запуска.
     *     Ожидание появления текста.
     *     Проверка, что текст равен "Hello World!".
     *     Сохранение трассировки (trace-success.zip).
     */

    @Test
    @Story("Практическое задание 32: Документирование тестов")
    @Description("Тест динамической загрузки")
    void testDynamicLoading() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
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
                assertEquals(200, response.status(),
                        "Ошибка: " + response.status());
            }
        });

        page.click("button");
        Locator finishText = page.locator("#finish");
        finishText.waitFor();

        assertTrue(finishText.textContent().contains("Hello World"),
                "Ошибка: найден иной текст → " + finishText.textContent());

        // Остановка трассировки и сохранение отчёта
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace/trace-success.zip")));
    }

    /**
     * Завершение работы после каждого теста:
     *     Закрывает вкладку.
     *     Закрывает браузер.
     *     Закрывает Playwright.
     */
    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}