import Controller.Controller;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.util.concurrent.TimeUnit;

public class Dispatcher {
    private final static Logger log;
    static {log = Logger.getLogger(Dispatcher.class.getName());}

    public static void main(String[] args) {
        log.info("Старт программы");
        Controller controller = new Controller();
        controller.getConnect();
    //    BrowserController browserController = new BrowserController(BrowserController.browsr.CHROME);
    //    browserController.start();

       Boolean b = true;
        while (b){
            controller.getMesssages();
            if (controller.getMess().size()>0){
                controller.getLines();
                controller.makeO8();
                try {controller.getFolder().close();} catch (MessagingException e) {e.printStackTrace();}
                //            browserController.createO8(controller.getString());
            }
            try {TimeUnit.SECONDS.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
        }
        controller.disconnect();
    //    browserController.disconnect();
        log.info("Конец работы программы");
    }
}
