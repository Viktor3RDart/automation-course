package courseplaywtests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Добрый день. Меня зовут Виктор.
 * Я работаю QAA. Мы сейчас активно переходим на playwright после selenide. Ваш курс оказался очень кстати.
 * По замечаниям на отдельные тесты я вас понял, буду делать 1 задание - 1 класс
 *
*/

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class ParallelTests {

    private static final ThreadLocal<Playwright> playwrightThread = ThreadLocal.withInitial(Playwright::create);
    private static final ThreadLocal<Browser> browserThread = ThreadLocal.withInitial(() ->
            playwrightThread.get().chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setArgs(List.of("--start-fullscreen"))));

    @Test
    void testLoginPage() {
        BrowserContext context = browserThread.get().newContext();
        Page page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/login");
        assertEquals("The Internet", page.title());
        context.close();
    }

    @Test
    void testAddRemoveElements() {
        BrowserContext context = browserThread.get().newContext();
        Page page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
        page.click("button:text('Add Element')");
        assertTrue(page.isVisible("button.added-manually"));
        context.close();
    }

}