package courseplaywtests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Epic("Playwright Java: Автоматизация тестирования. Полный курс")
@Feature("9.1 Ускорение тестов")
public class OptimizedLoginTest {
    static private Playwright playwright;
    static private Browser browser;
    private BrowserContext context;
    private Page page;
    static private List<Cookie> authCookies;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // Выполняем логин один раз перед всеми тестами
        BrowserContext loginContext = browser.newContext();
        Page loginPage = loginContext.newPage();
        authCookies = performLogin(loginPage);

        loginPage.close();
        loginContext.close();
    }

    @BeforeEach
    void setUp() {
        // Создаём новый контекст и добавляем сохранённые cookies для каждого теста
        context = browser.newContext();

        // Добавляем сохранённые cookies аутентификации
        if (authCookies != null && !authCookies.isEmpty()) {
            context.addCookies(authCookies);
        }

        page = context.newPage();
    }

    @Test
    @Story("Практическое задание 27: Оптимизация теста аутентификации")
    @Description("Тест входа в систему, чтобы токен аутентификации сохранялся между тестами")
    void testSecureArea() {
        page.navigate("https://the-internet.herokuapp.com/secure");
        // Проверяем, что пользователь аутентифицирован
        assertTrue(page.locator("h2").textContent().contains("Secure Area"));
    }

    @Step("Выполняем вход и возвращаем cookies для повторного использования")
    private static List<Cookie> performLogin(Page page) {

        // Переходим на страницу логина
        page.navigate("https://the-internet.herokuapp.com/login");
        page.waitForLoadState(LoadState.LOAD);

        // Заполняем форму логина
        Locator user = page.locator("#username");
        Locator password = page.locator("#password");

        assertThat(user).isEditable();
        assertThat(password).isEditable();
        user.fill("tomsmith");
        password.fill("SuperSecretPassword!");

        // Нажимаем кнопку логина
        page.locator("button[type='submit']").click();

        // Ждём перехода на защищённую страницу
        page.waitForURL("https://the-internet.herokuapp.com/secure");

        // Возвращаем cookies для повторного использования
        return page.context().cookies();
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}