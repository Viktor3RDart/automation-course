package courseplaywtests;

import base.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class GitHubSearchInterceptionTest extends BaseTest {

    @BeforeMethod
    void setUp() {
        // Перехват запроса поиска
        context.route("**/search**", route -> {
            // Получаем оригинальный URL
            String originalUrl = route.request().url();

            // Декодируем и модифицируем параметры
            String modifiedUrl = originalUrl.contains("q=")
                    ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
                    : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";

            // Продолжаем запрос с модифицированным URL
            route.resume(new Route.ResumeOptions().setUrl(modifiedUrl));
        });
    }

    @Test
    void testSearchModification() {
        page.navigate("https://github.com/search?q=java&type=repositories");

        // Ожидаем появления результатов поиска
        page.getByTestId("results-list").waitFor();

        // Проверяем модифицированный запрос в UI
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search or jump to…")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search")).waitFor();
        assertThat(page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search"))).hasValue("stars:>10000");

    }

}
