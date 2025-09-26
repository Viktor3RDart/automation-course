package tests;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;

import pages.DynamicContentPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FakerGenerTest {
    static Playwright playwright;
    static Browser browser;
    Page page;
    DynamicContentPage dynamicContentPage;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void tearDownClass() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void setUp() {
        page = browser.newPage();
        dynamicContentPage = new DynamicContentPage(page);
    }

    @AfterEach
    void tearDown() {
        page.close();
    }

    @Test
    @Story("Практическое задание 20: Применение современных подходов")
    @Description("Тест проверяет перехват запроса и замену в нём ответа на сгенерированное имя")
    void testDynamicContentWitFakerGenerAndMockedName() {
        // Генерация данных
        Faker faker = new Faker();
        String mockName = faker.name().fullName();

        // Мокирование API
        page.route("**/dynamic_content*", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("text/html")
                .setBody("<div class=\"large-10 columns\">" + mockName + "</div>")
        ));

        // Переходим и проверяем
        dynamicContentPage.navigate();
        String content = dynamicContentPage.getContentText();

        System.out.println("Сгенерированное имя: " + mockName);
        System.out.println("Контент страницы: " + content);

        assertTrue(content.contains(mockName), "Имя должно отображаться на странице");
    }
}