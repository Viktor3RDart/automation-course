package base;

import com.microsoft.playwright.*;
import config.ConfigLoader;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected String artifactsDir;
    protected static Properties config;

    protected BrowserType setupConfig() {
        config = ConfigLoader.load();
        // Выбор браузера на основе параметра
        return switch (config.getProperty("browser")) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();  // Значение по умолчанию
        };
    }

    @BeforeMethod
    public void setup() {
        // Создаем директорию для артефактов с текущей датой/временем
        artifactsDir = "testsData/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        // Создаем playwright
        playwright = Playwright.create();
        // Создаем и определяем параметры browser
        browser = setupConfig().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(config.getProperty("headless")))
                .setArgs(List.of("--start-maximized"//"--auto-open-devtools-for-tabs",
                )));
        // Создаем и определяем параметры context - основные параметры
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setLocale("ru-RU")
                .setPermissions(java.util.List.of("geolocation")));
        // Создаем и определяем параметры context - параметры для видео
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get(artifactsDir + "/videos/"))
                .setViewportSize(1920, 1080));
        // Стартуем tracing
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Закрываем tracing
        context.tracing().stop(new Tracing.StopOptions()
                .setPath((Paths.get("traces/" + result.getName() + ".zip"))));
        // Закрываем всё
        try {
            if (page != null) page.close();
            if (context != null) context.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}