package courseplaywtests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @Test
    void testDynamicCheckbox() {

        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
        page.waitForLoadState(LoadState.LOAD);
        // Находим чекбокс с атрибутом type="checkbox".
        Locator checkbox = page.locator("input[type='checkbox']");
        assertTrue(checkbox.isVisible(), "Чекбокс должен быть видимым");
        // Кликаем на кнопку "Remove".
        Locator removeCheckbox = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove"));
        removeCheckbox.click();
        // Ожидаем исчезновения чекбокса.
        removeCheckbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        // Проверяем, что появляется текст "It's gone!".
        assertThat(page.getByText("It's gone!")).isVisible();
        // Кликаем на кнопку "Add".
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add")).click();
        // Проверяем, что чекбокс снова отображается.
        removeCheckbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}