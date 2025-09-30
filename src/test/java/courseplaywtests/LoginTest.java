package courseplaywtests;

import customreport.CustomReportBaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends CustomReportBaseTest {

    @Test
    public void testLogin() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.locator("#username").fill("tomsmith");
        page.locator("#password").fill("SuperSecretPassword!");
        page.locator("button[type='submit']").click();
        assertTrue(page.locator(".flash.success").isVisible());
    }

    @AfterEach
    public void teardown() {
        if (page != null) {
            page.close();
        }
    }
}