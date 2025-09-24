package courseplaywtests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodoApi() throws Exception {
        // 1. Делаем GET-запрос
        APIResponse response = requestContext.get("/todos/1");

        // 2. Проверяем статус-код
        assertEquals(200, response.status(),
                "Ошибка - статус код должен быть 200, а получили - " +  response.status());

        // 3. Парсим JSON
        String body = response.text();
        JsonNode jsonNode = objectMapper.readTree(body);
        System.out.println(jsonNode);

        // 4. Проверка структуры (ожидаем поля userId, id, title, completed)
        assertTrue(jsonNode.has("userId"), "Нет поля userId");
        assertTrue(jsonNode.has("id"), "Нет поля id");
        assertTrue(jsonNode.has("title"), "Нет поля title");
        assertTrue(jsonNode.has("completed"), "Нет поля completed");

        // Дополнительно: проверим конкретные значения
        assertEquals(1, jsonNode.get("id").asInt());
        assertEquals(1, jsonNode.get("userId").asInt());
        assertNotNull(jsonNode.get("title").asText());
        assertNotNull(jsonNode.get("completed").asBoolean());
    }

    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }
}