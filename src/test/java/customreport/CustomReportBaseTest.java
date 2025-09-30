package customreport;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith(CustomReportExtension.class)
public class CustomReportBaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected Page page;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void createPage() {
        page = browser.newPage();
    }


    @AfterAll
    static void teardown() throws IOException {
        // Генерация отчета после всех тестов
        HtmlReportGenerator.generateReport(
                CustomReportExtension.getResults(),
                "test-report.html"
        );
        browser.close();
        playwright.close();
    }

    public Page getPage() {
        return page;
    }
}