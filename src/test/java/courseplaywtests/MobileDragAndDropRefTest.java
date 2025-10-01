package courseplaywtests;

import base.BaseRefTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.DragAndDropPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pages.DragAndDropPage.getColumnText;

@Epic("Playwright Java: Автоматизация тестирования. Полный курс")
@Feature("11.2 Рефакторинг тестов")
public class MobileDragAndDropRefTest extends BaseRefTest {

    DragAndDropPage dragAndDropPage;

    @BeforeEach
    void openPage() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
        dragAndDropPage = new DragAndDropPage(page);
    }

    @ParameterizedTest
    @Story("Практическое задание 31: Рефакторинг тестов")
    @Description("Тест на устранение дублирования через POM и параметризации одного теста с использованием @ParameterizedTest")
    @CsvSource({
            "#column-a, #column-b, A, B",
            "#column-b, #column-a, B, A"
    })
    void testDragAndDropMobile(String source, String target, String expectedSourceText, String expectedTargetText) {
        assertEquals(expectedSourceText, getColumnText(source),
                "В исходной колонке должен быть текст " + expectedSourceText);

        assertEquals(expectedTargetText, getColumnText(target),
                "В целевой колонке должен быть текст " + expectedTargetText);

        dragAndDropPage.dragAndDrop(source, target);

        assertEquals(expectedSourceText, getColumnText(target),
                "После перетаскивания в целевой колонке должен быть " + expectedSourceText);
    }

}