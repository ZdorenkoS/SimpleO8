package project.controller;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.utils.ConfigProperties;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
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
            System.setProperty("webdriver.firefox.driver", "G:\\Java project\\SimpleO8");
            driver = new FirefoxDriver();
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10,TimeUnit.SECONDS);
        driver.manage().window().setPosition(new Point(0,0));
    //  driver.manage().window().maximize();
        log.info("Браузер готов к работе");
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void start(){
        driver.get(ConfigProperties.getProperty("erpUrl"));
        driver.findElement(By.id("User")).sendKeys(ConfigProperties.getProperty("erpUser"));
        driver.findElement(By.id("Password")).sendKeys(ConfigProperties.getProperty("erpPassword"));
        driver.findElement(By.id("F1")).submit();
        driver.findElement(By.id("drop_mainmenu")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Справка'])[1]/following::td[4]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[11]/following::span[2]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[20]/following::span[2]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[23]/following::span[2]")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Профиль задачи'])[26]/following::a[1]")).click();

        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.xpath("//div[@id='div']/font")).click();
        driver.findElement(By.xpath("//table[@id='HE0_62']/tbody/tr/td[2]/span/nobr")).click();
        driver.switchTo().parentFrame();
        try{TimeUnit.SECONDS.sleep(1);}catch (InterruptedException e){}
        driver.findElement(By.id("listOCL_0")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.switchTo().frame("wcFrame0");
        driver.findElement(By.id("tileDescription_7")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("hc_Find")).click();
        driver.switchTo().parentFrame();
        log.info("ЕРП готов к работе");
    }

    public void createO8(){
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(temp),null);
        pause(2);
        driver.findElement(By.id("listOCL_1")).click();
        pause(2);
        driver.switchTo().frame("e1menuAppIframe");
        //FIXME вылетает тут
        driver.findElement(By.id("C0_24")).click();
        driver.findElement(By.className("JSTextfield")).sendKeys(Keys.chord(Keys.CONTROL+"v"));
        pause(3);
        driver.findElement(By.id("hc_OK")).click();
        pause(1);
        try {driver.findElement(By.id("hc_Find")).click();
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            driver.findElement(By.id("hc_OK")).click();
            driver.findElement(By.id("hc_Find")).click();
        }
        driver.findElement(By.xpath("//div[@id='div']/font")).click();
        driver.findElement(By.xpath("//table[@id='HE0_26']/tbody/tr/td[2]/span/nobr")).click();
        driver.switchTo().parentFrame();
        pause(1);
        driver.findElement(By.id("listOCL_2")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("hc_Find")).click();
        pause(2);
        try {WebElement element = driver.findElement(By.id("GOTOLAST0_1"));
            if (element.isDisplayed()) element.click();}
        catch (Exception ex){}
        driver.findElement(By.id("selectAll0_1")).click();
        driver.findElement(By.xpath("(//div[@id='div']/font)[2]")).click();
        driver.findElement(By.xpath("//table[@id='HE0_117']/tbody/tr/td[2]/span/nobr")).click();
        driver.switchTo().parentFrame();
        temp = new String();
        log.info("О8 созданы");
    }

    public void disconnect() {
        driver.close();
        log.info("Браузер закрыт");
    }

    private void pause(int i){
        try {driver.manage().timeouts().wait(i*1000);} catch (InterruptedException e) {}
    }
}
