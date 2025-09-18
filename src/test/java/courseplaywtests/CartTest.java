package courseplaywtests;

import base.BaseTest;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.testng.ITestResult;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public class CartTest extends BaseTest {

    @Test
    void testCartActions() {
        // Переходим на страницу интернет-магазина (пример URL)
        page.navigate("https://www.saucedemo.com/");

        // Логинимся (пример для демо сайта)
        page.fill("#user-name", "standard_user");
        page.fill("#password", "secret_sauce");
        page.click("#login-button");

        // Ждем загрузки страницы товаров
        page.waitForSelector(".inventory_item");

        // Добавляем первый товар в корзину
        page.click(".btn_inventory:first-of-type");

        // Делаем скриншот корзины после добавления товара
        page.locator(".shopping_cart_container").screenshot(
                new Locator.ScreenshotOptions()
                        .setPath(Paths.get(artifactsDir + "/cart_after_add.png")));

        // Переходим в корзину
        page.click(".shopping_cart_link");

        // Проверяем, что товар добавлен
        Assert.assertTrue(page.locator(".cart_item").isVisible());

        // Делаем скриншот страницы корзины
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(artifactsDir + "/cart_page.png")));

        // Удаляем товар из корзины
        page.click(".cart_button");

        // Делаем скриншот после удаления товара
        page.locator(".shopping_cart_container").screenshot(
                new Locator.ScreenshotOptions()
                        .setPath(Paths.get(artifactsDir + "/cart_after_remove.png")));

        // Проверяем, что корзина пуста
        Assert.assertTrue(page.locator(".removed_cart_item").isVisible() ||
                !page.locator(".cart_item").isVisible());
    }


    @Attachment(value = "{attachmentName}", type = "image/png")
    private byte[] attachScreenshotToAllure(String attachmentName) {
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true));
            Allure.addAttachment(attachmentName, "image/png",
                    new ByteArrayInputStream(screenshot), ".png");
            return screenshot;
        } catch (Exception e) {
            Allure.addAttachment("Screenshot Error", "text/plain",
                    "Failed to take screenshot: " + e.getMessage());
            return new byte[0];
        }
    }

    @Test(groups = "needAttach")
    void testCartActions2() {
        Allure.step("Переход на страницу интернет-магазина", () -> {

        });
        Allure.step("Переходим на страницу интернет-магазина (пример URL)", () -> {
        page.navigate("https://www.saucedemo.com/");
        });
      Allure.step("Логинимся (пример для демо сайта)", () -> {
        page.fill("#user-name", "standard_user");
        page.fill("#password", "secret_sauce");
        page.click("#login-button");
      });
      Allure.step("Ждем загрузки страницы товаров", () -> {
        page.waitForSelector(".inventory_item");
      });
      Allure.step("Добавляем первый товар в корзину", () -> {
        page.click(".btn_inventory:first-of-type");
      });
      Allure.step("Делаем скриншот корзины после добавления товара", () -> {
        page.locator(".shopping_cart_container").screenshot(
                new Locator.ScreenshotOptions()
                        .setPath(Paths.get(artifactsDir + "/cart_after_add.png")));
      });
      Allure.step("Переходим в корзину", () -> {
        page.click(".shopping_cart_link");
      });
      Allure.step("Проверяем, что товар добавлен", () -> {
        Assert.assertTrue(page.locator(".cart_item").isVisible());
      });
      Allure.step("Делаем скриншот страницы корзины", () -> {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(artifactsDir + "/cart_page.png")));
      });
      Allure.step("Удаляем товар из корзины", () -> {
        page.click(".cart_button");
      });
      Allure.step("Делаем скриншот после удаления товара", () -> {
        page.locator(".shopping_cart_container").screenshot(
                new Locator.ScreenshotOptions()
                        .setPath(Paths.get(artifactsDir + "/cart_after_remove.png")));
      });
      Allure.step("Проверяем, что корзина пуста", () -> {
        Assert.assertTrue(page.locator(".removed_cart_item").isVisible() ||
                !page.locator(".cart_item").isVisible());
      });
    }

    @AfterMethod(onlyForGroups = "needAttach")
    void attachScreenshotOnFailure(ITestResult result) {
        try {
            // Проверяем, был ли тест неуспешным
            if (result.getMethod() != null && result.getTestClass() != null) {
                // Прикрепляем скриншот в конце теста
                attachScreenshotToAllure("Screenshot at teardown - " + result.getName());

                // Если тест упал, добавляем дополнительный скриншот
                if (result.getStatus() == ITestResult.FAILURE) {
                    attachScreenshotToAllure("FAILURE - " + result.getName());
                }
            }
        } catch (Exception e) {
            try {
                Class<?> allureClass = Class.forName("io.qameta.allure.Allure");
                java.lang.reflect.Method addAttachmentMethod = allureClass.getMethod("addAttachment",
                        String.class, String.class, java.io.InputStream.class, String.class);
                addAttachmentMethod.invoke(null, "Error in teardown", "text/plain",
                        new java.io.ByteArrayInputStream(e.getMessage().getBytes()), ".txt");
            } catch (Exception allureError) {
                System.out.println("Error in teardown: " + e.getMessage());
            }
        }

    }

    @Test
    public void testHomePageVisual() throws IOException {
        // 1. Переходим на страницу
        page.navigate("https://the-internet.herokuapp.com");

        // 2. Делаем скриншот страницы
        Path actualScreenshot = Paths.get(artifactsDir + "/actual_homepage.png");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(actualScreenshot)
                .setFullPage(true));

        // 3. Определяем путь к эталонному изображению
        Path expectedScreenshot = Paths.get("src/test/resources/visual/expected_homepage.png");

        // 4. Создаем директории если их нет
        Files.createDirectories(expectedScreenshot.getParent());
        Files.createDirectories(Paths.get(artifactsDir));

        // 5. Если эталона нет - сохраняем текущий скриншот как эталон
        if (!Files.exists(expectedScreenshot)) {
            Files.copy(actualScreenshot, expectedScreenshot, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Создан новый эталон: " + expectedScreenshot);
            Assert.assertTrue(true, "Эталон создан, проверка пропущена");
            return;
        }

        // 6. Сравниваем скриншоты через Files.mismatch()
        long mismatchPosition = Files.mismatch(actualScreenshot, expectedScreenshot);

        // 7. Проверяем что файлы идентичны (-1 означает отсутствие различий)
        if (mismatchPosition == -1) {
            Assert.assertTrue(true, "Визуальная проверка пройдена: скриншоты идентичны");
        } else {
            // 8. Сохраняем diff-файл и информацию
            saveDiffInfo(actualScreenshot, expectedScreenshot, mismatchPosition);
            createVisualDiffImage(actualScreenshot, expectedScreenshot);

            // Тест падает с информацией о различиях
            Assert.fail("Обнаружены визуальные различия! Позиция первого различия: " + mismatchPosition +
                    ". Подробности в: " + artifactsDir);
        }
    }

    private void saveDiffInfo(Path actual, Path expected, long mismatchPosition) throws IOException {
        // Сохраняем информацию о различиях в текстовый файл
        Path diffInfo = Paths.get(artifactsDir + "/diff_info.txt");
        String info = String.format(
                """
                        Визуальные различия обнаружены!
                        Время теста: %s
                        Позиция первого различия: %d
                        Актуальный скриншот: %s
                        Эталонный скриншот: %s
                        Размер актуального файла: %d bytes
                        Размер эталонного файла: %d bytes
                        """,
                LocalDateTime.now(),
                mismatchPosition,
                actual.toString(),
                expected.toString(),
                Files.size(actual),
                Files.size(expected)
        );

        Files.write(diffInfo, info.getBytes(), StandardOpenOption.CREATE);
    }

    private void createVisualDiffImage(Path actualPath, Path expectedPath) {
        try {
            // Загружаем изображения
            BufferedImage actualImage = ImageIO.read(actualPath.toFile());
            BufferedImage expectedImage = ImageIO.read(expectedPath.toFile());

            // Создаем изображение для diff
            BufferedImage diffImage = new BufferedImage(
                    actualImage.getWidth(),
                    actualImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Сравниваем пиксели
            for (int y = 0; y < actualImage.getHeight(); y++) {
                for (int x = 0; x < actualImage.getWidth(); x++) {
                    Color actualColor = new Color(actualImage.getRGB(x, y));
                    Color expectedColor = new Color(expectedImage.getRGB(x, y));

                    if (!actualColor.equals(expectedColor)) {
                        // Различия подсвечиваем красным
                        diffImage.setRGB(x, y, Color.RED.getRGB());
                    } else {
                        // Совпадающие области делаем серыми
                        diffImage.setRGB(x, y, Color.GRAY.getRGB());
                    }
                }
            }
            // Сохраняем diff изображение
            Path diffImagePath = Paths.get(artifactsDir + "/visual_diff.png");
            ImageIO.write(diffImage, "PNG", diffImagePath.toFile());

        } catch (Exception e) {
            System.err.println("Ошибка при создании visual diff: " + e.getMessage());
        }
    }

}
