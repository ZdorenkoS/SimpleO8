package project.controller;

import org.apache.log4j.Logger;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;
import project.utils.ConfigProperties;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BrowserController extends Thread{
    public enum browsr{CHROME,FIREFOX}
    private static WebDriver driver;
    private final static Logger log = Logger.getLogger(BrowserController.class.getName());
    private static String temp;

    public BrowserController() {}
    public BrowserController(browsr b) {
        if (b.equals(browsr.CHROME)){
            System.setProperty("chromedriver.chrome.driver", "G:\\Java project\\SimpleO8");
            driver = new ChromeDriver();

        }
        if (b.equals(browsr.FIREFOX)){
            FirefoxOptions options = new FirefoxOptions().setLegacy(true);
            System.setProperty("webdriver.firefox.driver", "G:\\Java project\\SimpleO8");
            driver = new FirefoxDriver(options);
        }
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.manage().window().setPosition(new Point(0,0));
        driver.manage().window().maximize();
        log.info("Браузер готов к работе");
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void start(){
        driver.get(ConfigProperties.getProperty("erpUrl"));
        try{TimeUnit.SECONDS.sleep(3);}catch (InterruptedException e){}
        driver.findElement(By.id("User")).sendKeys(ConfigProperties.getProperty("erpUser"));
        driver.findElement(By.id("Password")).sendKeys(ConfigProperties.getProperty("erpPassword"));
        driver.findElement(By.id("F1")).submit();
        try{TimeUnit.SECONDS.sleep(3);}catch (InterruptedException e){}
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("tab0")).click();
        driver.switchTo().parentFrame();
        driver.findElement(By.id("drop_mainmenu")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Справка'])[1]/following::td[4]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[11]/following::span[2]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[20]/following::span[2]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[23]/following::span[2]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[26]/following::a[1]")).click();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.xpath("//div[@id='div']/font")).click();
        driver.findElement(By.xpath("//table[@id='HE0_62']/tbody/tr/td[2]/span/nobr")).click();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.switchTo().parentFrame();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.findElement(By.id("listOCL_0")).click();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.switchTo().frame("e1menuAppIframe");
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.switchTo().frame(driver.findElement(By.className("wcFrame")));
        driver.findElement(By.id("tileDescription_7")).click();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("hc_Find")).click();
        driver.switchTo().parentFrame();
        log.info("ЕРП готов к работе");
    }

    public void createO8(){
        driver.findElement(By.id("listOCL_1")).click();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.switchTo().frame("e1menuAppIframe");

        try{
            try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
            driver.findElement(By.id("C0_24")).click();
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
            driver.findElement(By.id("C0_24")).click();
            try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(temp),null);
        driver.findElement(By.className("JSTextfield")).sendKeys(Keys.chord(Keys.CONTROL+"v"));

        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.findElement(By.id("hc_OK")).click();

        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.findElement(By.id("hc_Find")).click();
        temp = new String();


        driver.findElement(By.xpath("//div[@id='div']/font")).click();
        driver.findElement(By.xpath("//table[@id='HE0_26']/tbody/tr/td[2]/span/nobr")).click();
        try {TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) {}

        try {
            driver.findElement(By.id("AQFormQueryList")).click();
            new Select(driver.findElement(By.id("AQFormQueryList"))).selectByVisibleText("*!= Y");
            driver.findElement(By.xpath("//div[@id='div']/font")).click();
            driver.findElement(By.xpath("//table[@id='HE0_26']/tbody/tr/td[2]/span/nobr")).click();
        }catch (Exception ex) {}

        driver.switchTo().parentFrame();

        try {
            driver.findElement(By.id("listOCL_2")).click();
            driver.switchTo().frame("e1menuAppIframe");
            driver.findElement(By.id("hc_Find")).click();
        try{TimeUnit.SECONDS.sleep(5);}catch (InterruptedException e){}

        try {WebElement element = driver.findElement(By.id("GOTOLAST0_1"));
            if (element.isDisplayed()) element.click();}
        catch (Exception ex){}
        driver.findElement(By.id("selectAll0_1")).click();
        driver.findElement(By.xpath("(//div[@id='div']/font)[2]")).click();
        driver.findElement(By.xpath("//table[@id='HE0_117']/tbody/tr/td[2]/span/nobr")).click();
        driver.switchTo().parentFrame();
        log.info("О8 созданы");
        }
        catch (Exception ex){}
    }

    public void disconnect() {
        driver.close();
        log.info("Браузер закрыт");
    }
}
