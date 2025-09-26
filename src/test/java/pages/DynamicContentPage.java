package pages;

        import com.microsoft.playwright.Locator;
        import com.microsoft.playwright.Page;
        import io.qameta.allure.Step;

public class DynamicContentPage extends BasePage{

    private final String url = "https://the-internet.herokuapp.com/dynamic_content";
    private final Locator contentLocator;

    public DynamicContentPage(Page page) {
        super(page);
        this.contentLocator = page.locator(".large-10.columns").first();
    }

    @Step("Переходим неа страницу {url}")
    public void navigate() {
        page.navigate(url);
    }

    @Step("Получаем текст со страницы")
    public String getContentText() {
        return contentLocator.textContent();
    }
}