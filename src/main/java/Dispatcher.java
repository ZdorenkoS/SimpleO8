import Controller.Controller;

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

        for (String s:controller.lines) {
            System.out.println(s);
        }



        controller.disconnect();
        log.info("Конец работы программы");
    }
}
