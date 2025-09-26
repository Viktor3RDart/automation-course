package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import pages.DynamicControlsPage;
import support.TestContext;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DynamicControlsTest {
    private TestContext context;
    private DynamicControlsPage controlsPage;

    @BeforeEach
    void setUp() {
        context = new TestContext();
        controlsPage = new DynamicControlsPage(context.getPage()); // через DI
        controlsPage.navigate();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    @Story("Практическое задание 21: DI для создания гибких тестов")
    @Description("Внедрение Di и проверка, что чекбокс исчезает после нажатия кнопки")
    void testCheckboxRemoval() {
        controlsPage.clickRemoveButton();
        assertFalse(controlsPage.isCheckboxVisible(false), "Чекбокс должен исчезнуть после нажатия Remove");
    }
}