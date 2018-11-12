import Controller.Controller;
import Model.O8;
import org.apache.log4j.Logger;

public class Dispatcher {
    private final static Logger log;
    static {
        log = Logger.getLogger(Dispatcher.class.getName());}

    public static void main(String[] args) {
        log.info("Старт программы");
        Controller controller = new Controller();
        controller.getConnect();
        controller.getMesssages();
        controller.getLines();
        controller.createO8();

        for (O8 o:controller.getO8s()) {
            System.out.println(o.toString());
        }

        controller.disconnect();
        log.info("Конец работы программы");
    }
}
