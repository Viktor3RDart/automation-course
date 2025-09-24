package courseplaywtests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Настройка параметров iPad Pro 11
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );

        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {
        // Открываем страницу
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        // Кликаем по кнопке Enable
        page.click("button[onclick='swapInput()']");

        // Ждем пока поле станет активным
        Locator field = page.getByRole(AriaRole.TEXTBOX);
        field.waitFor();

        // Проверка: поле ввода должно быть доступно
        assertThat(field).isVisible();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Enable"))).not().isVisible();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Disable"))).isVisible();
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}