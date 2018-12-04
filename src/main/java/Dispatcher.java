import Controller.BrowserController;
import org.apache.log4j.Logger;

public class Dispatcher {
    private final static Logger log;
    static {
        log = Logger.getLogger(Dispatcher.class.getName());}

    public static void main(String[] args) {
        log.info("Старт программы");
 /*       Controller controller = new Controller();
        controller.getConnect();
        controller.getMesssages();
        controller.getLines();
        controller.createO8();
        System.out.println(controller.getString());
*/
        BrowserController browserController = new BrowserController(BrowserController.browsr.CHROME);
        browserController.start();

//      controller.disconnect();
//      browserController.disconnect();
        log.info("Конец работы программы");
    }
}
