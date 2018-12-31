package project;

import org.apache.log4j.Logger;
import project.controller.BrowserController;
import project.controller.Controller;
import project.utils.ConfigProperties;

import java.util.concurrent.TimeUnit;

public class Dispatcher implements Runnable{
    private final static Logger log;
    static {log = Logger.getLogger(Dispatcher.class.getName());}
    private volatile boolean isRunning;
    private Controller controller;
    private BrowserController browserController;

    public Dispatcher() {
        log.info("Старт программы");
        isRunning = true;
        controller = new Controller();
        controller.getConnect();
        browserController = new BrowserController(BrowserController.browsr.CHROME);
        //  browserController.start();
        controller.getView().setDispatcher(this);
    }

    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher();
        new Thread(dispatcher).start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                dispatcher.controller.disconnect();
                dispatcher.browserController.disconnect();
                log.info("Конец работы программы");
            }
        });
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            controller.getMesssages();
            if (controller.getMess().size() > 0) {
                controller.getLines();
                controller.makeO8();
                controller.o8Validation();
                controller.getString();
                //     browserController.createO8(controller.getString());

            }
            try {
                TimeUnit.SECONDS.sleep(Long.parseLong(ConfigProperties.getProperty("sleepTime")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Поток выполняется");

            synchronized (this) {
                if (!isRunning) {
                    System.out.println("Поток приостановлен");
                    while (!isRunning) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("Поток возобновил работу");
                }
            }
        }
    }


    public synchronized void changeThreadState() {
        isRunning = !isRunning;
        notifyAll();
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public Controller getController() {
        return controller;
    }
}