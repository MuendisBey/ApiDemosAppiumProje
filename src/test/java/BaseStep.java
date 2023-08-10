import helper.StoreHelper;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.ElementOption;
import model.SelectorInfo;
import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

public class BaseStep extends BaseTest{


    private Logger logger = LoggerFactory.getLogger(getClass());

    public List<MobileElement> findElements(By by) throws Exception {
        List<MobileElement> webElementList = null;
        try {
            webElementList = appiumFluentWait.until(new ExpectedCondition<List<MobileElement>>() {
                @Nullable
                @Override
                public List<MobileElement> apply(@Nullable WebDriver driver) {
                    List<MobileElement> elements = driver.findElements(by);
                    return elements.size() > 0 ? elements : null;
                }
            });
            if (webElementList == null) {
                throw new NullPointerException(String.format("by = %s Web element list not found", by.toString()));
            }
        } catch (Exception e) {
            throw e;
        }
        return webElementList;
    }



    public MobileElement findElement(By by) throws Exception {
        MobileElement mobileElement;
        try {
            mobileElement = findElements(by).get(0);
        } catch (Exception e) {
            throw e;
        }
        return mobileElement;
    }



    public MobileElement findElementByKeyWithoutAssert(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        MobileElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileElement;
    }

    public MobileElement findElementByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);

        MobileElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Element not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElement;
    }



    public List<MobileElement> findElemenstByKey(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        List<MobileElement> mobileElements = null;
        try {
            mobileElements = findElements(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Elements not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElements;
    }



    private int getScreenHeight() {
        return appiumDriver.manage().window().getSize().height;
    }



    public void swipeUpAccordingToPhoneSize() {
        if (appiumDriver instanceof AndroidDriver) {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;
            System.out.println(width + "  " + height);

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 30) / 100;
            int swipeEndHeight = (height * 70) / 100;
            new TouchAction((AndroidDriver) appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        } else {
            Dimension d = appiumDriver.manage().window().getSize();
            int height = d.height;
            int width = d.width;

            int swipeStartWidth = width / 2, swipeEndWidth = width / 2;
            int swipeStartHeight = (height * 35) / 100;
            int swipeEndHeight = (height * 75) / 100;
            new TouchAction(appiumDriver)
                    .press(PointOption.point(swipeStartWidth, swipeEndHeight))
                    .waitAction(WaitOptions.waitOptions(ofMillis(2000)))
                    .moveTo(PointOption.point(swipeEndWidth, swipeStartHeight))
                    .release()
                    .perform();
        }
    }





    @Step("<StartX>,<StartY> coordinatından <EndX>,<EndY> coordinatına <times> kere swipe et")
    public void pointToPointSwipeWithCoordinats(int startX, int startY, int endX, int endY, int count) throws InterruptedException {
        Dimension d = appiumDriver.manage().window().getSize();


        for (int i = 0; i < count; i++) {
            waitBySecond(1);
            TouchAction action = new TouchAction(appiumDriver);
            action.press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(ofMillis(1000)))
                    .moveTo(PointOption.point(endX, endY))
                    .release().perform();
        }


    }

    @Step({"<key> elementinin görünürlüğü kontrol edilir"})
    public void existElement(String key) {
        assertTrue(findElementByKey(key).isDisplayed(), "Element sayfada bulunamadi !");
        logger.info(key + " elementi bulundu !");
    }

    @Step("<key>li elementi bulana kadar <limit> kere swipe yap ve elementi bul")
    public void swipeKeyy(String key, int limit) throws InterruptedException {


        boolean isAppear = false;

        int windowHeight = this.getScreenHeight();
        for (int i = 0; i < limit; ++i) {
            try {

                Dimension phoneSize = appiumDriver.manage().window().getSize();
                Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
                logger.info(elementLocation.x + "  " + elementLocation.y);
                Dimension elementDimension = findElementByKeyWithoutAssert(key).getSize();
                logger.info(elementDimension.width + "  " + elementDimension.height);
                // logger.info(appiumDriver.getPageSource());
                if ((0 < elementLocation.y) && (elementLocation.y <= phoneSize.height - 30)) {
                    isAppear = true;
                    logger.info("aranan elementi buldu");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Element ekranda görülmedi. Tekrar swipe ediliyor");
            }
            System.out.println("Element ekranda görülmedi. Tekrar swipe ediliyor");

            swipeUpAccordingToPhoneSize();
            waitBySecond(1);
        }

    }

    @Step("<key> elementinin hizasından sağdan sola <times> kere kaydır")
    public void swipeFromLeftToRightAligned(String key, int times) throws InterruptedException {
        Dimension d = appiumDriver.manage().window().getSize();

        int height = d.height;
        int width = d.width;
        Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
        pointToPointSwipeWithCoordinats(width - 50, elementLocation.getY(), 40, elementLocation.getY(), times);
    }



    @Step("<key> elementinin text degerini yazdir")
    public void checkIsValiaaadFormat(String key) {
        String elementStringValue = findElementByKey(key).getText();
        System.out.println(key+" elementinin text degeri : "+elementStringValue);
    }


    @Step({"<key> li elementi bul basılı tut"})
    public void basiliTut(String key) {
        MobileElement mobileElement = findElementByKey(key);

        TouchAction action = new TouchAction<>(appiumDriver);
        action
                .longPress(LongPressOptions.longPressOptions()
                        .withElement(ElementOption.element(mobileElement)).withDuration(Duration.ofSeconds(4)))
                .release()
                .perform();
        logger.info(key + " elementine uzun tiklanildi");

    }


    @Step("<key> elementinin text degerinde <isim> ismiyle hafizaya kaydet")
    public void checkIsValiaaadFormadert(String key, String isim) {

        String elementStringValue1 = findElementByKey(key).getText();

        StoreHelper.INSTANCE.saveValue(isim,elementStringValue1);
        System.out.println(elementStringValue1);

    }

    @Step("<isim> degeriyle hafizada saklanan text degeri <key> elementinin text degerini icerdigi kontrol edilir")
    public void checkIsValiaaadFormatt(String isim, String key) {

        String value = StoreHelper.INSTANCE.getValue(isim);

        String elementStringValue1 = findElementByKey(key).getText();

        assertTrue(elementStringValue1.replace(" ","").trim().replace("\n","").contains(value.replace(" ","").trim()),"Degerini içermiyor.");
        System.out.println("Metinler birbirini içeriyor");


    }

    @Step({"Elementine tıkla <key>"})
    public void clickByKey(String key) {
        findElementByKey(key).click();
        logger.info(key + " elemente tikladi");
    }

    @Step({"<key> elementi var mı"})
    public void existElementYazma(String key) {
        assertTrue(findElementByKey(key).isDisplayed(), "Element sayfada bulunamadi !");
        logger.info(key + " bulundu !");
    }

    @Step({"Element yok mu kontrol et <key>"})
    public void checkElementNotExists(String key) {
        SelectorInfo selectorInfo = selector.getSelectorInfo(key);

        int loopCount = 0;
        while (loopCount < 10) {
            if (appiumDriver.findElements(selectorInfo.getBy()).size() == 0) {
                logger.info(key + " elementinin olmadığı kontrol edildi.");
                return;
            }
            loopCount++;
        }
    }

    @Step({"<key> elementinin <text> textini içerdiği kontrol edilir"})
    public void equalsTextByKey(String key, String text) {
        assertTrue(appiumFluentWait.until(
                ExpectedConditions.textToBe(selector.getElementInfoToBy(key), text)));
        logger.info(key + " elementinin text değerinin " + text + " e eşit olduğu kontrol edildi");
    }

    @Step({"Wait <second> seconds"})
    public void waitBySecond(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"<key> li elementi bul, temizle ve <text> değerini yaz"})
    public void sendKeysByKey(String key, String text) {
        MobileElement webElement = findElementByKey(key);
        webElement.clear();
        webElement.setValue(text);
    }

    @Step("<key> li elementi rasgele sec")
    public void chooseRandomProduct(String key) {

        List<MobileElement> productList = new ArrayList<>();
        List<MobileElement> elements = findElemenstByKey(key);
        int elementsSize = elements.size();
        int height = appiumDriver.manage().window().getSize().height;
        for (int i = 0; i < elementsSize; i++) {
            MobileElement element = elements.get(i);
            int y = element.getCenter().getY();
            if (y > 0 && y < (height - 100)) {
                productList.add(element);
            }
        }
        Random random = new Random();
        int randomNumber = random.nextInt(productList.size());
        productList.get(randomNumber).click();
    }


}