package courseplaywtests;

import base.BaseTest;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

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

}
