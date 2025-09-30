package courseplaywtests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.Random;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Playwright Java: Автоматизация тестирования. Полный курс")
@Feature("9.2 Анализ производительности")
public class PerformanceAnalysisTest {

    static Playwright playwright;
    static Browser browser;
    private BrowserContext context;
    private Page page;
    Random rnd = new Random();
    boolean shouldTrace;
    
    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));

    }

    @BeforeEach
    void setUp() {
        // Создаём новый контекст
        shouldTrace = rnd.nextDouble() < 0.10; // 10% запусков
        context = browser.newContext();
        if (shouldTrace) {
            context.tracing().start(new Tracing.StartOptions()
                    .setSnapshots(true)
                    .setScreenshots(true));
        }  // если не трассируем, ничего не делаем

        page = context.newPage();
    }

    @Test
    @Story("Практическое задание 29: Анализ производительности")
    @Description("Тест анализа производительности критического сценария и интеграции проверок в тесты")
    void testLoginPerformance() {
        long start = System.currentTimeMillis();

        // Переходим на страницу логина
        page.navigate("https://the-internet.herokuapp.com/login");

        // Заполняем форму логина
        Locator user = page.locator("#username");
        Locator password = page.locator("#password");

        assertThat(user).isEditable();
        assertThat(password).isEditable();
        user.fill("tomsmith");
        password.fill("SuperSecretPassword!");

        // Нажимаем кнопку логина
        page.locator("button[type='submit']").click();

        // Проверяем что вход осуществлён
        page.waitForLoadState(LoadState.LOAD);
        assertThat(page.locator("#flash")).containsText("You logged into a secure area! ×");

        // Выполняем проверку, что время выполнения не превышает 3 секунд
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 3000, "Login took " + duration + "ms (exceeds 3000ms limit)");
    }

    @AfterEach
    void cleanup() {
        // Сохраняем трассировку для 10% запусков
        if (shouldTrace) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("slow-login-trace.zip")));
        }
        context.close();
    }

    @AfterAll
    static void tearDownAll() {
        browser.close();
        playwright.close();
    }

}

