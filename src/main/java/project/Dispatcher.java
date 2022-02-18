package project;


import project.controller.BrowserController;
import project.controller.Controller;
import project.view.View;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

public class Dispatcher implements Runnable {
    private volatile boolean isRunning;
    private Controller controller;
    private BrowserController browserController;


    public Dispatcher() {
        isRunning = true;
        controller = new Controller();
        controller.getConnect();
        browserController = new BrowserController(BrowserController.browsr.FIREFOX);
        browserController.start();
        controller.getView().setDispatcher(this);
    }

    public static void main(String[] args) {
        Dispatcher dispatcher = null;
        try {
            dispatcher = new Dispatcher();
            new Thread(dispatcher).start();
        } catch (Exception ex) {
            ex.printStackTrace();
            telegramNotify(ex.getMessage(), true);
            dispatcher.controller.disconnect();
            dispatcher.browserController.disconnect();

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

                try {
                    browserController.setTemp(controller.getString());
                    telegramNotify(controller.getTelegramString(), false);
                    browserController.createO8();

                } catch (Exception ex) {
                    try {
                        browserController.disconnect();
                        browserController = new BrowserController(BrowserController.browsr.CHROME);
                        browserController.start();
                        browserController.createO8();
                    } catch (Exception e) {
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

    private static void telegramNotify(String message, Boolean fail) {
        Properties prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream("src/main/resources/config.properties"), "cp1251"));
        } catch (IOException ex) {
        }
        String urlString = prop.getProperty("urlTelegram");
        String apiToken = fail ? prop.getProperty("apiTokenFail") : prop.getProperty("apiTokenLog");
        String chatId = prop.getProperty("chatId");
        String text;
        try {
            text = URLEncoder.encode(message, "UTF-8");
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
