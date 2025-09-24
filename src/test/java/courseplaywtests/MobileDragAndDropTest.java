package courseplaywtests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MobileDragAndDropTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Ручная настройка параметров Samsung Galaxy S22 Ultra
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Mobile Safari/537.36")
                .setViewportSize(873, 873)
                .setDeviceScaleFactor(3.5)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false) // для визуализации
        );
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testDragAndDropMobile() {
        // Переходим на страницу
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        Locator columnA = page.locator("#column-a");
        Locator columnB = page.locator("#column-b");

        // Проверка начального состояния
        assertEquals("A", columnA.textContent().trim(), "В колонке A должен быть текст 'A'");
        assertEquals("B", columnB.textContent().trim(), "В колонке B должен быть текст 'B'");

        // Перетаскивание с помощью JS (т.к. dragAndDrop в Playwright не всегда работает на этом сайте)
        page.evaluate(
                "([sourceSelector, targetSelector]) => {" +
                        "const source = document.querySelector(sourceSelector);" +
                        "const target = document.querySelector(targetSelector);" +
                        "const dataTransfer = new DataTransfer();" +
                        "source.dispatchEvent(new DragEvent('dragstart', { dataTransfer }));" +
                        "target.dispatchEvent(new DragEvent('drop', { dataTransfer }));" +
                        "source.dispatchEvent(new DragEvent('dragend', { dataTransfer }));" +
                        "}",
                new Object[]{"#column-a", "#column-b"}
        );

        // Проверка результата
        columnA.waitFor();
        assertEquals("A", columnB.textContent().trim(), "После перетаскивания колонка B должна содержать 'A'");
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}