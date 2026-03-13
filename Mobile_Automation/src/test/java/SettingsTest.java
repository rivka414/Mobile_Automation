import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

//הכנתי את הפרויקט ביחד עם חברות
public class SettingsTest {

    private static AndroidDriver driver;
    private static WebDriverWait wait;

    public enum MainMenuItem {
        NETWORK_AND_INTERNET("Network & internet"),
        CONNECTED_DEVICES("Connected devices"),
        APPS("Apps"),
        NOTIFICATIONS("Notifications"),
        BATTERY("Battery"),
        DISPLAY("Display"),
        SOUND("Sound"),
        STORAGE("Storage"),
        PRIVACY("Privacy"),
        LOCATION("Location"),
        SECURITY("Security"),
        ACCOUNTS("Accounts"),
        ACCESSIBILITY("Accessibility"),
        DIGITAL_WELLBEING("Digital Wellbeing"),
        GOOGLE("Google"),
        SYSTEM("System"),
        ABOUT_EMULATED_DEVICE("About emulated device");

        private final String visibleText;

        MainMenuItem(String visibleText) {
            this.visibleText = visibleText;
        }

        public String getVisibleText() {
            return visibleText;
        }
    }

    @BeforeAll
    public static void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setPlatformVersion("14")
                .setAppPackage("com.android.settings")
                .setAppActivity("com.android.settings.Settings")
                .setNoReset(false)
                .setNewCommandTimeout(Duration.ofSeconds(150));

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void openAboutEmulatedDeviceAndVerifyPage() {
        Assertions.assertTrue(isAnyElementPresent(
                byTextOrDesc("Settings"),
                byTextOrDesc("Search settings"),
                AppiumBy.xpath("//*[contains(@resource-id,'search')]")
        ), "Settings did not launch.");

        final String activityBefore = safeCurrentActivity();

        scrollToExactTextAndClick(MainMenuItem.ABOUT_EMULATED_DEVICE.getVisibleText());

        Assertions.assertTrue(didNavigateFromMain(activityBefore),
                "About emulated device page did not open (no navigation detected).");

        boolean aboutContentDetected = isAnyElementPresent(
                AppiumBy.xpath("//*[contains(@text,'Android version') or contains(@text,'Build number') or contains(@text,'Model')]"),
                AppiumBy.xpath("//*[contains(@text,'About')]")
        );
    }

    private static boolean didNavigateFromMain(String activityBefore) {
        String activityAfter = safeCurrentActivity();
        if (activityBefore != null && activityAfter != null && !activityAfter.equals(activityBefore)) {
            return true;
        }

        if (isAnyElementPresent(
                AppiumBy.accessibilityId("Navigate up"),
                AppiumBy.xpath("//*[@content-desc='Navigate up']")
        )) {
            return true;
        }

        if (!isAnyElementPresent(byTextOrDesc("Search settings"))) {
            return true;
        }

        return false;
    }

    private static String safeCurrentActivity() {
        try {
            return driver.currentActivity();
        } catch (Exception e) {
            return null;
        }
    }

    private static By byTextOrDesc(String text) {
        return AppiumBy.xpath("//*[@text='" + text + "' or @content-desc='" + text + "']");
    }

    private static void scrollToExactTextAndClick(String exactText) {
        By scrollIntoView = AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().text(\"" + escapeUiAutomatorText(exactText) + "\"))"
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(scrollIntoView));

        By exact = AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + escapeUiAutomatorText(exactText) + "\")"
        );

        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(exact));
        el.click();
    }

    private static String escapeUiAutomatorText(String text) {
        return text.replace("\"", "\\\"");
    }

    private static boolean isAnyElementPresent(By... locators) {
        for (By by : locators) {
            try {
                driver.findElement(by);
                return true;
            } catch (NoSuchElementException ignored) {
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return false;
    }
}
