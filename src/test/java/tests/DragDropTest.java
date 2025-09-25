package tests;

import com.microsoft.playwright.*;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import pages.DragDropPage;

public class DragDropTest {
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    public static void setupAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
        );
    }

    @AfterAll
    public static void tearDownAll() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    @Story("Практическое задание 19: Реализовать продвинутый POM")
    @Description("Тест проверяет перемещение 'A' в 'B' и то, что в зоне B появился текст 'A'")
    public void testDragAndDrop() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        DragDropPage dragDropPage = new DragDropPage(page);
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        // Выполняем действие
        dragDropPage.dragDropArea().dragAToB();

        // Проверяем, что в зоне B теперь текст A
        String textB = dragDropPage.dragDropArea().getTextB();
        Assertions.assertEquals("A", textB, "Ожидалось, что в колонке B появится текст 'A'");

        context.close();
    }
}