package Controller;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        log.info("Браузер готов к работе");
    }

    public void start(){
        driver.get("http://jde.nautilus.allo.ua/jde/E1Menu.maf?jdeowpBackButtonProtect=PROTECTED");
        driver.findElement(By.id("User")).sendKeys("zdorenkos");
        driver.findElement(By.id("Password")).sendKeys("3Bd4");
        driver.findElement(By.id("F1")).submit();
//FIXME переписать код чтобы работал и в файрфокс
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
        WebElement wait = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("listOCL_0")));
        driver.findElement(By.id("listOCL_0")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.switchTo().frame("wcFrame0");
//FIXME разобраться откуда такая большая задержка
        driver.findElement(By.id("tileDescription_7")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("hc_Find")).click();
        driver.switchTo().parentFrame();
        log.info("ЕРП готов к работе");
    }

    public void createO8(String s){
        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("listOCL_1")));
        driver.findElement(By.id("listOCL_1")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("C0_24")).click();
        driver.findElement(By.className("JSTextfield")).sendKeys(s);
        driver.findElement(By.id("hc_OK")).click();
        driver.findElement(By.id("hc_Find")).click();
        driver.findElement(By.xpath("//div[@id='div']/font")).click();
        driver.findElement(By.xpath("//table[@id='HE0_26']/tbody/tr/td[2]/span/nobr")).click();
        driver.switchTo().parentFrame();
        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.id("listOCL_2")));
        driver.findElement(By.id("listOCL_2")).click();
        driver.switchTo().frame("e1menuAppIframe");
        driver.findElement(By.id("hc_Find")).click();
        new WebDriverWait(driver, 15).until(ExpectedConditions.presenceOfElementLocated(By.className("JSSelectGrid selectedModifier")));
        try {WebElement element = driver.findElement(By.id("GOTOLAST0_1"));
            if (element.isDisplayed()) element.click();}
        catch (Exception ex){}
        driver.findElement(By.id("selectAll0_1")).click();
        driver.findElement(By.xpath("(//div[@id='div']/font)[2]")).click();
        driver.findElement(By.xpath("//table[@id='HE0_117']/tbody/tr/td[2]/span/nobr")).click();
    }

    public void disconnect() {
        driver.close();
        log.info("Браузер закрыт");
    }

}
