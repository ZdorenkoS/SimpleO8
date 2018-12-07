import Controller.BrowserController;
import org.apache.log4j.Logger;

public class Dispatcher {
    private final static Logger log;
    static {
        log = Logger.getLogger(Dispatcher.class.getName());}

    public static void main(String[] args) {
        log.info("Старт программы");
/*      Controller controller = new Controller();
        controller.getConnect();
        controller.getMesssages();
        controller.getLines();
        controller.makeO8();*/

        String s = "1\tP3001\tUA2\t\196079\t235647\t1\t2882,600\t\t\t20400111394695\n" +
                "1\tP3001\tUA2\t\196079\t235647\t1\t2880,600\t\t\t20400111394695\n";

        BrowserController browserController = new BrowserController(BrowserController.browsr.CHROME);
        browserController.start();
        browserController.createO8(s);

//      controller.disconnect();
//      browserController.disconnect();
        log.info("Конец работы программы");
    }
}
