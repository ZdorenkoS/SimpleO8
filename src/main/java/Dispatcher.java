import controller.BrowserController;
import controller.Controller;
import controller.Task;
import org.apache.log4j.Logger;
import view.View;

import javax.swing.*;

public class Dispatcher {
    private final static Logger log;
    static {log = Logger.getLogger(Dispatcher.class.getName());}

    public static void main(String[] args) {
        log.info("Старт программы");
        Controller controller = new Controller();
        controller.getConnect();
        BrowserController browserController = new BrowserController(BrowserController.browsr.CHROME);
      //  browserController.start();

        Task task = new Task(controller,browserController);
        new Thread(task).start();
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}
        SwingUtilities.invokeLater(new View(task));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                controller.disconnect();
                browserController.disconnect();
                log.info("Конец работы программы");
            }
        });
    }
}
