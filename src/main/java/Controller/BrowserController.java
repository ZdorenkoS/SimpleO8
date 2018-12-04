package Controller;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class BrowserController {
    public enum browsr{CHROME,FIREFOX}
    private static WebDriver driver;
    private final static Logger log = Logger.getLogger(BrowserController.class.getName());

    public BrowserController() {}
    public BrowserController(browsr b) {
        if (b.equals(browsr.CHROME)){
            System.setProperty("chromedriver.chrome.driver", "G:\\Java Project\\SimpleO8");
            driver = new ChromeDriver();
        }
        if (b.equals(browsr.FIREFOX)){
            System.setProperty("webdriver.firefox.driver", "G:\\Java Project\\SimpleO8");
            driver = new FirefoxDriver();
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        log.info("Браузер готов к работе");
    }

    public void start(){
        driver.get("http://jde.nautilus.allo.ua/jde/E1Menu.maf?jdeowpBackButtonProtect=PROTECTED");
        driver.findElement(By.id("User")).sendKeys("zdorenkos");
        driver.findElement(By.id("Password")).sendKeys("3Bd4");
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

        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        driver.switchTo().parentFrame();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Открытые приложения'])[2]/following::td[2]")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.switchTo().frame("wcFrame0");
        driver.findElement(By.id("tileDescription_7")).click();
        log.info("ЕРП готов к работе");
    }

    public void createO8(){

    }

    public void disconnect() {
        driver.close();
        log.info("Браузер закрыт");
    }

}
