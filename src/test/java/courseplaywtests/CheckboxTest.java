package courseplaywtests;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.testng.Assert;


import java.io.ByteArrayInputStream;

@Epic("Веб-интерфейс тестов")
@Feature("Работа с чек-боксами")
public class CheckboxTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();

        // Настройка трассировки для скриншотов при ошибках
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();
    }

    @Test
    @Story("Проверка работы чек-боксов")
    @TmsLink("Тестирование выбора/снятия чек-боксов")
    @Description("Тест проверяет функциональность чек-боксов: начальное состояние, выбор и снятие выделения")
    void testCheckboxes() {
        navigateToCheckboxesPage();
        verifyInitialState();
        toggleCheckboxes();
        verifyToggleState();
    }

    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
        String pageTitle = page.title();
        Assert.assertEquals(pageTitle, "The Internet", "Заголовок страницы должен быть 'The Internet'");

        // Проверяем, что мы на правильной странице
        String heading = page.locator("h3").textContent();
        Assert.assertEquals(heading, "Checkboxes", "Заголовок должен быть 'Checkboxes'");
    }

    @Step("Проверка начального состояния чек-боксов")
    private void verifyInitialState() {
        // Локаторы для чек-боксов
        Locator checkbox1 = page.locator("input[type='checkbox']:nth-of-type(1)");
        Locator checkbox2 = page.locator("input[type='checkbox']:nth-of-type(2)");

        // Проверяем начальное состояние первого чекбокса (не выбран)
        Assert.assertFalse(checkbox1.isChecked(), "Первый чекбокс должен быть не выбран изначально");

        // Проверяем начальное состояние второго чекбокса (выбран)
        Assert.assertTrue(checkbox2.isChecked(), "Второй чекбокс должен быть выбран изначально");

        Allure.addAttachment("Начальное состояние", "text/plain",
                "Чекбокс 1: " + (checkbox1.isChecked() ? "выбран" : "не выбран") + "\n" +
                        "Чекбокс 2: " + (checkbox2.isChecked() ? "выбран" : "не выбран"));
    }

    @Step("Изменение состояния чек-боксов")
    private void toggleCheckboxes() {
        Locator checkbox1 = page.locator("input[type='checkbox']:nth-of-type(1)");
        Locator checkbox2 = page.locator("input[type='checkbox']:nth-of-type(2)");

        // Кликаем на первый чекбокс (должен стать выбранным)
        checkbox1.click();
        Assert.assertTrue(checkbox1.isChecked(), "Первый чекбокс должен быть выбран после клика");

        // Кликаем на второй чекбокс (должен стать не выбранным)
        checkbox2.click();
        Assert.assertFalse(checkbox2.isChecked(), "Второй чекбокс должен быть не выбран после клика");

        // Делаем скриншот после изменения состояния
        Allure.addAttachment("Состояние после изменений", "image/png",
                new ByteArrayInputStream(page.screenshot()), ".png");
    }

    @Step("Проверка состояния после изменений")
    private void verifyToggleState() {
        Locator checkbox1 = page.locator("input[type='checkbox']:nth-of-type(1)");
        Locator checkbox2 = page.locator("input[type='checkbox']:nth-of-type(2)");
        // Финальная проверка состояний
        Assert.assertTrue(checkbox1.isChecked(), "Первый чекбокс должен оставаться выбранным");
        Assert.assertFalse(checkbox2.isChecked(), "Второй чекбокс должен оставаться не выбранным");

        Allure.addAttachment("Финальное состояние", "text/plain",
                "Чекбокс 1: " + (checkbox1.isChecked() ? "выбран" : "не выбран") + "\n" +
                        "Чекбокс 2: " + (checkbox2.isChecked() ? "выбран" : "не выбран"));
    }

    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        try {
            // Сохраняем трассировку для отладки
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(java.nio.file.Paths.get("trace.zip")));
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении трассировки: " + e.getMessage());
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}