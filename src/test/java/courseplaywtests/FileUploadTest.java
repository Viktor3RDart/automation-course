package courseplaywtests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class FileUploadTest {
    private static Playwright playwright;
    private static APIRequestContext request;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        request = playwright.request().newContext();
    }

    @AfterAll
    static void tearDown() {
        if (request != null) request.dispose();
        if (playwright != null) playwright.close();
    }

    @Test
    void testFileUploadAndDownload() throws Exception {
        // Генерация PNG-файла в памяти
        byte[] testFileBytes = generateTestPng();

        //Загрузка файла на сервер
        APIResponse uploadResponse = request.post(
                "https://httpbin.org/post", RequestOptions.create().setMultipart(
                        FormData.create().set("file", new FilePayload("file", ".png", testFileBytes)))
        );

        //Проверка получения файла сервером
        String responseBody = uploadResponse.text();
        Assertions.assertTrue(responseBody.contains("data:.png;base64"), "Файл не загружен");

        //Проверка содержимого
        String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
        String base64Content = base64Data.split(",")[1];
        byte[] receivedBytes = Base64.getDecoder().decode(base64Content);
        Assertions.assertArrayEquals(testFileBytes, receivedBytes,
                "Содержимое загруженного файла должно совпадать с исходным");

        //Скачивание эталонного PNG-файла
        APIResponse downloadResponse = request.get("https://httpbin.org/image/png");

        // Проверка MIME-типа
        String contentType = downloadResponse.headers().get("content-type");
        Assertions.assertEquals("image/png", contentType,
                "MIME-тип должен быть image/png");

        // Валидация формата через сигнатуру
        byte[] downloadedBytes = downloadResponse.body();
        verifyPngSignature(downloadedBytes);
    }

    private byte[] generateTestPng() throws Exception {
        // Создание PNG через временный файл
        Path tempFile = Files.createTempFile("test", ".png");
        // Создание скриншота как тестового PNG
        try (Browser browser = playwright.chromium().launch()) {
            Page page = browser.newPage();
            page.setContent("<html><body><div style='width:100px;height:100px;background:red'>Test</div></body></html>");
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(tempFile)
                    .setType(ScreenshotType.PNG));
            Files.delete(tempFile); // Тут удаляем временный файл
            return screenshot;
        }
    }

    private void verifyPngSignature(byte[] content) {
        // Проверка сигнатуры PNG - первые 8 байт
        Assertions.assertEquals(0x89, content[0] & 0xFF, "Неверная PNG сигнатура - байт 1");
        Assertions.assertEquals(0x50, content[1] & 0xFF, "Неверная PNG сигнатура - байт 2"); // P
        Assertions.assertEquals(0x4E, content[2] & 0xFF, "Неверная PNG сигнатура - байт 3"); // N
        Assertions.assertEquals(0x47, content[3] & 0xFF, "Неверная PNG сигнатура - байт 4"); // G
        Assertions.assertEquals(0x0D, content[4] & 0xFF, "Неверная PNG сигнатура - байт 5");
        Assertions.assertEquals(0x0A, content[5] & 0xFF, "Неверная PNG сигнатура - байт 6");
        Assertions.assertEquals(0x1A, content[6] & 0xFF, "Неверная PNG сигнатура - байт 7");
        Assertions.assertEquals(0x0A, content[7] & 0xFF, "Неверная PNG сигнатура - байт 8");
    }
}