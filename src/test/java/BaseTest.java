import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.AfterStep;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.ExecutionContext;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import selector.Selector;
import selector.SelectorFactory;
import selector.SelectorType;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class BaseTest {

    private Logger logger = LoggerFactory.getLogger(getClass());
    protected static AppiumDriver<MobileElement> appiumDriver;
    protected static FluentWait<AppiumDriver> appiumFluentWait;
    public static boolean localAndroid = true;
    protected static Selector selector;
    DesiredCapabilities capabilities;
    URL localUrl;


    @BeforeScenario
    public void beforeScenario(ExecutionContext executionContext) {
        try {
            logger.info("************************************  BeforeScenario  ************************************");
            logger.info("SCENARIO NAME: " + executionContext.getCurrentScenario().getName());
            logger.info(executionContext.getAllTags().toString());

            localUrl = new URL("http://127.0.0.1:4723/wd/hub");

            if (StringUtils.isEmpty(System.getenv("key"))) {
                if (localAndroid) {
                    logger.info("Android ortamında test ayağa kalkacak");
                    appiumDriver = new AndroidDriver(localUrl, androidCapabilities(true));
                }
            }
            selector = SelectorFactory
                    .createElementHelper(SelectorType.ANDROID );

            appiumFluentWait = new FluentWait(appiumDriver);
            appiumFluentWait.withTimeout(Duration.ofSeconds(15))
                    .pollingEvery(Duration.ofMillis(250))
                    .ignoring(NoSuchElementException.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public DesiredCapabilities androidCapabilities(boolean isLocal) {
        capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        capabilities.setCapability(MobileCapabilityType.FULL_RESET, false);
        capabilities.setCapability("unicodeKeyboard", false);
        capabilities.setCapability("resetKeyboard", false);
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.hmh.api");
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.hmh.api.ApiDemos");


        if (isLocal) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM, MobilePlatform.ANDROID);
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);
        } else {
            capabilities.setCapability("key", System.getenv("key"));

        }
        return capabilities;
    }


    @AfterScenario
    public void afterScenario() {

        if (appiumDriver != null) {
            appiumDriver.quit();
        }

        logger.info("AfterScenario*************************************************************************" + "\r\n");
    }

    @AfterStep
    public void afterStep(ExecutionContext executionContext) {

        if (executionContext.getCurrentStep().getIsFailing()) {
            logger.info(executionContext.getCurrentStep().getErrorMessage());
            logger.info(executionContext.getCurrentStep().getStackTrace());
        }
    }

}
