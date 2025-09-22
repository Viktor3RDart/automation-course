package courseplaywtests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {

    private static final ThreadLocal<Playwright> playwrightThread = ThreadLocal.withInitial(Playwright::create);
    private static final ThreadLocal<Browser> browserThread = ThreadLocal.withInitial(() ->
            playwrightThread.get().chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setArgs(List.of("--start-fullscreen"))));


    @AfterAll
    static void tearDownAll() {
        playwrightThread.remove();
    }

    // Тест 1: Основные страницы навигации
    @ParameterizedTest
    @ValueSource(strings = {"/", "/login", "/dropdown"})
    void testBasicNavigationPages(String path) {
        BrowserContext context = browserThread.get().newContext();
        Page page = context.newPage();
        String url = "https://the-internet.herokuapp.com" + path;
        System.out.println("Testing basic navigation: " + url);

        page.navigate(url);

        // Основные проверки
        assertThat(page).hasURL(url);
        assertThat(page).hasTitle(Pattern.compile(".+"));

        // Специфические проверки для каждой страницы
        switch (path) {
            case "/" -> {
                assertThat(page.locator("h1")).isVisible();
                assertThat(page.locator("h1")).hasText("Welcome to the-internet");
            }
            case "/login" -> {
                assertThat(page.locator("h2")).isVisible();
                assertThat(page.locator("h2")).hasText("Login Page");
                assertThat(page.locator("#username")).isVisible();
                assertThat(page.locator("#password")).isVisible();
            }
            case "/dropdown" -> {
                assertThat(page.locator("h3")).isVisible();
                assertThat(page.locator("h3")).hasText("Dropdown List");
                assertThat(page.locator("#dropdown")).isVisible();
            }
        }
    }

    // Тест 2: Страницы с JavaScript взаимодействием
    @ParameterizedTest
    @ValueSource(strings = {"/javascript_alerts", "/checkboxes", "/hovers"})
    void testJavaScriptInteractionPages(String path) {
        BrowserContext context = browserThread.get().newContext();
        Page page = context.newPage();
        String url = "https://the-internet.herokuapp.com" + path;
        System.out.println("Testing JavaScript interaction: " + url);

        page.navigate(url);

        // Основные проверки
        assertThat(page).hasURL(url);
        assertThat(page.locator("h1, h2, h3")).isVisible();

        // Специфические проверки для каждой страницы
        switch (path) {
            case "/javascript_alerts" -> {
                assertThat(page.locator("h3")).hasText("JavaScript Alerts");
                assertThat(page.locator("button:has-text('Click for JS Alert')")).isVisible();
            }
            case "/checkboxes" -> {
                assertThat(page.locator("h3")).hasText("Checkboxes");
                assertThat(page.locator("input[type='checkbox']")).hasCount(2);
            }
            case "/hover" -> {
                assertThat(page.locator("h3")).hasText("Hovers");
                assertThat(page.locator(".figure")).hasCount(3);
            }
        }
    }

    // Тест 3: Страницы с HTTP статусами и дополнительной функциональностью
    @ParameterizedTest
    @ValueSource(strings = {"/status_codes", "/checkboxes", "/dropdown"})
    void testStatusCodeAndFunctionalPages(String path) {
        BrowserContext context = browserThread.get().newContext();
        Page page = context.newPage();
        String url = "https://the-internet.herokuapp.com" + path;
        System.out.println("Testing status codes and functional pages: " + url);

        page.navigate(url);

        // Основные проверки
        assertThat(page).hasURL(url);
        assertThat(page).hasTitle(Pattern.compile(".+"));
        assertThat(page.locator("h1, h2, h3")).isVisible();

        // Специфические проверки для каждой страницы
        switch (path) {
            case "/status_codes" -> {
                assertThat(page.locator("h3")).hasText("Status Codes");
                assertThat(page.locator("a[href*='status_codes/']")).hasCount(4);
            }
            case "/checkboxes" -> {
                assertThat(page.locator("h3")).hasText("Checkboxes");
                // Проверяем, что чек-боксы кликабельны
                assertThat(page.locator("input[type='checkbox']").first()).isEnabled();
            }
            case "/dropdown" -> {
                assertThat(page.locator("h3")).hasText("Dropdown List");
                assertThat(page.locator("#dropdown")).isEnabled();
            }
        }
    }
}