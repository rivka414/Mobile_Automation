import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class AppiumTest {

    private static AndroidDriver driver;
    private static WebDriverWait wait;
    private static AppiumTest page;

    @BeforeAll
    public static void InitOptions() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName("emulator-5554");
        options.setAutomationName("UiAutomator2");
        options.setPlatformName("Android");
        options.setPlatformVersion("14");
        options.setAppPackage("com.google.android.deskclock");
        options.setAppActivity("com.android.deskclock.DeskClock");
        options.setNoReset(false);
        options.setNewCommandTimeout(Duration.ofSeconds(150));

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        page = new AppiumTest();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(20)), page);
    }

    @AfterAll
    public static void CloseDriver() {
        if (driver != null)
            driver.quit();
    }


    @AndroidFindBy(xpath = "//*[@text='Clock' or @text='שעון' or @content-desc='Clock']")
    private WebElement clockAny;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'toolbar')]")
    private List<WebElement> toolbarAny;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'deskclock')]")
    private List<WebElement> deskclockAny;

    @AndroidFindBy(xpath = "//*[@text='Timer' or @text='טיימר']")
    private WebElement timerTabByText;

    @AndroidFindBy(accessibility = "Timer")
    private WebElement timerTabByA11y;

    @AndroidFindBy(xpath = "//*[@text='Add timer' or @text='הוסף טיימר' or @content-desc='Add timer']")
    private List<WebElement> addTimerAnyByTextOrDesc;

    @AndroidFindBy(accessibility = "Add timer")
    private List<WebElement> addTimerAnyByA11y;

    @AndroidFindBy(xpath = "//*[@text='Add timer' or @text='הוסף טיימר']")
    private WebElement addTimerClickByText;

    @AndroidFindBy(accessibility = "Add timer")
    private WebElement addTimerClickByA11y;

    @AndroidFindBy(xpath = "//*[@text='0']")
    private WebElement digit0;
    @AndroidFindBy(xpath = "//*[@text='1']")
    private WebElement digit1;
    @AndroidFindBy(xpath = "//*[@text='2']")
    private WebElement digit2;
    @AndroidFindBy(xpath = "//*[@text='3']")
    private WebElement digit3;
    @AndroidFindBy(xpath = "//*[@text='4']")
    private WebElement digit4;
    @AndroidFindBy(xpath = "//*[@text='5']")
    private WebElement digit5;
    @AndroidFindBy(xpath = "//*[@text='6']")
    private WebElement digit6;
    @AndroidFindBy(xpath = "//*[@text='7']")
    private WebElement digit7;
    @AndroidFindBy(xpath = "//*[@text='8']")
    private WebElement digit8;
    @AndroidFindBy(xpath = "//*[@text='9']")
    private WebElement digit9;


    @AndroidFindBy(accessibility = "Start")
    private WebElement startByA11y;

    @AndroidFindBy(xpath = "//*[@text='Start' or @text='הפעל' or @content-desc='Start']")
    private WebElement startByTextOrDesc;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'start') and (@clickable='true' or @enabled='true')]")
    private WebElement startByResourceId;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'timer_setup_time')]")
    private List<WebElement> timerSetupTime;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'timer_time')]")
    private List<WebElement> timerTime;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'time')]//*[contains(@text,':')]")
    private List<WebElement> timeWithColonUnderTime;

    @AndroidFindBy(xpath = "//*[contains(@text,':') and (contains(@resource-id,'timer') or contains(@resource-id,'time'))]")
    private List<WebElement> anyColonTimerOrTime;


    @Test
    public void Timer() throws InterruptedException {

        Assertions.assertTrue(isAnyElementPresent(
                page.clockAny,
                page.toolbarAny,
                page.deskclockAny
        ), "האפליקציה לא עלתה.");

        clickFirst(
                page.timerTabByText,
                page.timerTabByA11y
        );

        if (!isAnyElementPresent(
                page.addTimerAnyByTextOrDesc,
                page.addTimerAnyByA11y
        )) {
        } else {
            clickFirst(
                    page.addTimerClickByText,
                    page.addTimerClickByA11y
            );
        }

        pressDigit('0');
        pressDigit('1');
        pressDigit('0');
        pressDigit('0');

        String beforeStart = readTimerDisplay();
        Assertions.assertNotNull(beforeStart, "הטסט לא הצליח לקרוא את תצוגת הזמן העליונה.");
        Assertions.assertFalse(beforeStart.trim().isEmpty(), "תצוגת הזמן העליונה ריקה אחרי הקלדה.");
        Assertions.assertTrue(beforeStart.matches(".*\\d.*"), "תצוגת הזמן לא מכילה ספרות: " + beforeStart);

        clickFirst(
                page.startByA11y,
                page.startByTextOrDesc,
                page.startByResourceId
        );

        Thread.sleep(30_000);

        String after30s = readTimerDisplay();
        Assertions.assertNotNull(after30s, "הטסט לא הצליח לקרוא את תצוגת הזמן אחרי 30 שניות.");
        Assertions.assertNotEquals(beforeStart, after30s,
                "הזמן לא השתנה אחרי 30 שניות. לפני: " + beforeStart + " אחרי: " + after30s);
    }


    private static void pressDigit(char digit) {
        WebElement el = switch (digit) {
            case '0' -> page.digit0;
            case '1' -> page.digit1;
            case '2' -> page.digit2;
            case '3' -> page.digit3;
            case '4' -> page.digit4;
            case '5' -> page.digit5;
            case '6' -> page.digit6;
            case '7' -> page.digit7;
            case '8' -> page.digit8;
            case '9' -> page.digit9;
            default -> throw new IllegalArgumentException("Unsupported digit: " + digit);

        };
        wait.until(ExpectedConditions.elementToBeClickable(el)).click();
    }


    private static String readTimerDisplay() {
        List<List<WebElement>> candidates = List.of(
                page.timerSetupTime,
                page.timerTime,
                page.timeWithColonUnderTime,
                page.anyColonTimerOrTime);

        for (List<WebElement> list : candidates) {
            if (list == null || list.isEmpty()) continue;
            for (WebElement el : list) {
                try {
                    String txt = el.getText();
                    if (txt != null && !txt.trim().isEmpty()) {
                        return txt.trim();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }


    @SafeVarargs
    private final void clickFirst(WebElement... elements) {
        for (WebElement el : elements) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(el)).click();
                return;
            } catch (Exception ignored) {
            }
        }
        Assertions.fail("לא נמצא אלמנט לחיץ באף אחד מה-locators שסופקו.");
    }


    private boolean isAnyElementPresent(Object... candidates) {
        for (Object candidate : candidates) {
            try {
                if (candidate instanceof WebElement el) {
                    // Accessing any property forces lookup with PageFactory proxies
                    el.isDisplayed();
                    return true;
                }
                if (candidate instanceof List<?> list && !list.isEmpty()) {
                    Object first = list.get(0);
                    if (first instanceof WebElement el) {
                        el.isDisplayed();
                        return true;
                    }
                }
            } catch (NoSuchElementException ignored) {
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}