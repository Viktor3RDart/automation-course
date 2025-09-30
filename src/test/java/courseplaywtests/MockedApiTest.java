package courseplaywtests;

import com.microsoft.playwright.*;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Epic("Playwright Java: Автоматизация тестирования. Полный курс")
@Feature("9.1 Ускорение тестов")
public class MockedApiTest {
    static Playwright playwright;
    static Browser browser;
    private BrowserContext context;
    private Page page;

    // Мок-сервис для имитации API
    private static ApiService apiService;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );

        // Создаем мок ApiService
        apiService = mock(ApiService.class);

        // Настраиваем поведение мока - возвращаем тестовые данные
        when(apiService.fetchUserData()).thenReturn("{\"name\": \"Test User\", \"email\": \"test@example.com\"}");
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Практическое задание 28: Мокирование медленных API-запросов")
    @Description("Тест замены медленных внешних API на мок-заглушку")
    void testUserProfileWithMockedApi() {
        // Используем мок вместо реального API
        String userData = apiService.fetchUserData();

        // Заменяем реальный вызов API на мок
        page.navigate("https://the-internet.herokuapp.com/dynamic_content");
        Object result = page.evaluate("window.data = '" + userData + "'");
        assertNotNull(result);
        assertTrue(result.toString().contains("Test User"));
        assertTrue(result.toString().contains("test@example.com"));
    }


    // Тестовый класс-заглушка для API сервиса
    static class ApiService {
        public String fetchUserData() {
            // Имитация медленного API-запроса
            try {
                Thread.sleep(3000); // 3 секунды задержки
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "{\"name\": \"Real User\", \"email\": \"real@example.com\"}";
        }
    }

    @AfterEach
    void tearDown() {
        if (context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}