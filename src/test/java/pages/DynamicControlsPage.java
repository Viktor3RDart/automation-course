package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

public class DynamicControlsPage {
    private final Page page;

    public DynamicControlsPage(Page page) {
        this.page = page;
    }

    @Step("Переходим на страницу")
    public void navigate() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    @Step("Кликнуть удалить чек-бокс")
    public void clickRemoveButton() {
        page.locator("button:has-text('Remove')").click();
    }

    @Step("Передать состояние чек-бокса")
    public boolean isCheckboxVisible(boolean visible) {
        Locator checkBox = page.locator("#checkbox");
        if(visible){
            checkBox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        }else {
            checkBox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        }
        return checkBox.isVisible();
    }
}