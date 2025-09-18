package courseplaywtests;

import base.BaseTest;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;

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

    @Test
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

    @AfterMethod
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

}
