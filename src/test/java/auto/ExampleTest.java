package auto;

import base.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleTest extends BaseTest {
    @Test
    void dummyTest() {
        assertTrue(true);
    }

    @Test
    void testNetworkMonitoring() {
        page.navigate("https://example.com");

        // Логирование URL всех запросов
        page.onRequest(request ->
                System.out.println(">> " + request.method() + " " + request.url())
        );

        // Логирование статуса ответов
        page.onResponse(response ->
                System.out.println("<< " + response.status() + " " + response.url())
        );
    }

}