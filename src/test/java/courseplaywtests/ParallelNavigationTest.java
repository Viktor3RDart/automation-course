package courseplaywtests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {

    private static ThreadLocal<Playwright> playwrightThread;
    private static ThreadLocal<Browser> browserThread;

    @BeforeAll
    static void setupAll() {
        playwrightThread = ThreadLocal.withInitial(Playwright::create);
    }

    @AfterAll
    static void tearDownAll() {
        browserThread.get().close();
        playwrightThread.get().close();
    }

    @AfterEach
    void closeContext() {
        browserThread.get().newContext().close();
        browserThread.get().close();
        playwrightThread.get().close();
    }

    static Stream<Arguments> provideBrowserAndPathBasicPages() {
        return Stream.of(
                // Chromium тесты
                Arguments.of("chromium", "/"),
                Arguments.of("chromium", "/login"),
                Arguments.of("chromium", "/dropdown"),

                // Firefox тесты
                Arguments.of("firefox", "/"),
                Arguments.of("firefox", "/login"),
                Arguments.of("firefox", "/dropdown")
        );
    }

    static Stream<Arguments> provideBrowserAndPathJavaScriptPages() {
        return Stream.of(
                // Chromium тесты
                Arguments.of("chromium", "/javascript_alerts"),
                Arguments.of("chromium", "/checkboxes"),
                Arguments.of("chromium", "/hovers"),

                // Firefox тесты
                Arguments.of("firefox", "/javascript_alerts"),
                Arguments.of("firefox", "/checkboxes"),
                Arguments.of("firefox", "/hovers")
        );
    }

    static Stream<Arguments> provideBrowserAndPathFunctionalPages() {
        return Stream.of(
                // Chromium тесты
                Arguments.of("chromium", "/status_codes"),

                // Firefox тесты
                Arguments.of("firefox", "/status_codes")
        );
    }

    private void testPage(String path) {
        BrowserContext context = browserThread.get().newContext();
        Page page = context.newPage();
        String url = "https://the-internet.herokuapp.com" + path;
        System.out.println("Testing: " + url + " in " + browserThread.get().browserType().name());

        page.navigate(url);

        assertThat(page).hasURL(url);
        assertThat(page).hasTitle(Pattern.compile(".+"));
        if (path.equals("/")) {
            assertThat(page.locator("h1")).isVisible();
        } else {
            assertThat(page.locator("h1, h2, h3")).isVisible();
        }
    }

    private void setupBrowser(String browserType) {
        if (browserType.equals("firefox")) {
            browserThread = ThreadLocal.withInitial(() ->
                    playwrightThread.get().firefox().launch(new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setArgs(List.of("--start-fullscreen"))));
        } else {
            browserThread = ThreadLocal.withInitial(() ->
                    playwrightThread.get().chromium().launch(new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setArgs(List.of("--start-fullscreen"))));
        }
    }

    // Тест 1: Основные страницы
    @ParameterizedTest
    @MethodSource("provideBrowserAndPathBasicPages")
    @Tag("basic")
    void testBasicPages(String browserType, String path) {
        setupBrowser(browserType);
        testPage(path);
    }

    // Тест 2: JavaScript страницы
    @ParameterizedTest
    @MethodSource("provideBrowserAndPathJavaScriptPages")
    @Tag("javascript")
    void testJavaScriptPages(String browserType, String path) {
        setupBrowser(browserType);
        testPage(path);
    }

    // Тест 3: Функциональные страницы
    @ParameterizedTest
    @MethodSource("provideBrowserAndPathFunctionalPages")
    @Tag("functional")
    void testFunctionalPages(String browserType, String path) {
        setupBrowser(browserType);
        testPage(path);
    }

}