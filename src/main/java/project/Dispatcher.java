package project;


import com.google.inject.internal.cglib.proxy.$CallbackFilter;
import org.apache.log4j.Logger;
import project.controller.BrowserController;
import project.controller.Controller;
import project.utils.ConfigProperties;
import project.view.View;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
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
        browserController.start();
        controller.getView().setDispatcher(this);
    }

    public static void main(String[] args) {
        Dispatcher dispatcher = null;
        try{
           dispatcher = new Dispatcher();
           new Thread(dispatcher).start();}
       catch (Exception ex){
           telegramNotify(ex.getMessage(),true);
           dispatcher.controller.disconnect();
           dispatcher.browserController.disconnect();
           log.info("Конец работы программы");
       }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            controller.getMesssages();
            if (controller.getMess().size() > 0) {
                controller.getLines();
                controller.makeO8();
                controller.o8Validation();
                controller.o8Merge();

                try{
                    browserController.setTemp(controller.getString());
                    telegramNotify(controller.getTelegramString(),false);
                    browserController.createO8();

                } catch (Exception ex){
                    try {
                        log.debug(ex.getMessage() + ex.getCause());
                        browserController.disconnect();
                        browserController = new BrowserController(BrowserController.browsr.CHROME);
                        browserController.start();
                        browserController.createO8();
                    } catch (Exception e){
                        telegramNotify("Сбой:", true);
                        telegramNotify(e.getMessage(), true);
                    }
                }
            }

            new View.Countdown(controller.getView()).run();

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

    private static void telegramNotify(String message, Boolean fail){
        Properties prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream("src/main/resources/config.properties"),"cp1251"));
        }
        catch (IOException ex){}
        String urlString = prop.getProperty("urlTelegram");
        String apiToken = fail ? prop.getProperty("apiTokenFail") : prop.getProperty("apiTokenLog");
        String chatId = prop.getProperty("chatId");
        String text;
        try {
            text =  URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            text = "не_получилось_преобразовать_URL";
        }

        urlString = String.format(urlString, apiToken, chatId, text);

        URL url = null;
        try {
            url = new URL(urlString);
            url.openConnection().getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
