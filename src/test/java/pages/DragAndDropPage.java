package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class DragAndDropPage {
    private static Page page;

    public DragAndDropPage(Page page) {
        this.page = page;
    }

    @Step("Получить название колонки, передав наименование локатора - {column}")
    public static String getColumnText(String column) {
        return page.locator(column).textContent().trim();
    }

    @Step(" Перетаскивание с помощью JS (т.к. dragAndDrop в Playwright не всегда работает на этом сайте)")
    public void dragAndDrop(String sourceSelector, String targetSelector) {
        page.evaluate("([sourceSelector, targetSelector]) => {" +
                "const source = document.querySelector(sourceSelector);" +
                "const target = document.querySelector(targetSelector);" +
                "const dataTransfer = new DataTransfer();" +
                "source.dispatchEvent(new DragEvent('dragstart', { dataTransfer }));" +
                "target.dispatchEvent(new DragEvent('drop', { dataTransfer }));" +
                "source.dispatchEvent(new DragEvent('dragend', { dataTransfer }));" +
                "}", new Object[]{sourceSelector, targetSelector});
    }
}