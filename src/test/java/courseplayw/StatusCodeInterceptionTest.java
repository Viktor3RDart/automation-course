package courseplayw;

import base.BaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class StatusCodeInterceptionTest extends BaseTest {

    @Test
    void testMockedStatusCode() {
        // Перехват запроса к /status_codes/404
        page.route("**/status_codes/404", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                .setBody("<h3>Mocked Success Response</h3>")
        ));

        page.navigate("https://the-internet.herokuapp.com/status_codes");

        // Клик по ссылке "404"
        page.waitForRequest("**/status_codes/404",
                () -> page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("404")).click());
        // Проверка мок-текста
        String content = page.textContent("body");
        Assert.assertTrue(content.contains("Mocked Success Response"),
                "Должен отображаться мок-текст - Mocked Success Response, а отображается  - " + content);
    }

}

