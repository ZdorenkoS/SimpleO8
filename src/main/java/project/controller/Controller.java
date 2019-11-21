package project.controller;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import project.model.GetMail;
import project.model.Goods;
import project.model.O8;
import project.model.SendMail;
import project.view.View;

import javax.mail.Message;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Controller extends Thread{
    private final static Logger log = Logger.getLogger(Controller.class.getName());
    private volatile ArrayList<String> lines = new ArrayList<>();
    private volatile ArrayList<O8> o8s = new ArrayList<>();
    private volatile ArrayList<O8> o8sFail = new ArrayList<>();
    private volatile ArrayList<Message> messages;
    private GetMail getMail;
    private SendMail sendMail;
    private View view;
    private Properties prop;
    private Properties prop_adresses;
    private Properties prop_names;

    public Controller() {
        prop = new Properties();
        prop_adresses = new Properties();
        prop_names = new Properties();

        try {
            prop.load(new InputStreamReader(new FileInputStream("src/main/resources/config.properties"),"cp1251"));
            prop_adresses.load(new InputStreamReader(new FileInputStream("src/main/resources/send_list.properties"),"cp1251"));
            prop_names.load(new InputStreamReader(new FileInputStream("src/main/resources/supplier.properties"),"cp1251"));
        }
        catch (IOException ex){}

        getMail = new GetMail();
        sendMail = new SendMail(prop.getProperty("gmail"),prop.getProperty("gmailPassword"));

        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        view = new View();
        SwingUtilities.invokeLater(view);
    }

    public View getView() {
        return view;
    }

    public void getConnect(){
        getMail.connect();
    }

    public void disconnect() {
        getMail.disconnect();
    }

    public ArrayList<Message> getMess() {return messages;}

    public void getMesssages() {
        messages = getMail.getMessages();
    }

    public void sendMesssages(HashMap<String, String> map) {
        try {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<String, String> pair = iterator.next();
                sendMail.send("Создан О8 № "+ pair.getKey() + " от " +prop_names.getProperty(pair.getValue()),"Это автоматическое сообщение о создании нового прихода товара в ИТ-системе компании \"Алло\". \n " +
                        "Создание прихода не означает, что склад готов к приему товара.\n" +
                        "По возникающим вопросам обращайтесь к своему менеджеру", prop_adresses.getProperty(pair.getValue()));
            }
        } catch (Exception ex){
            System.out.println("Ошибка при рассылке: " + ex.getCause());
        }

    }

    public void getLines() {
        lines = getMail.getLines(messages);
    }


    public void makeO8(){
        ArrayList<String[]> parts = new ArrayList<>();                                  // лист для "кусочков" линии
        Collections.sort(lines, new Comparator<String>() {                              // сортируем линии, получим нужные строки подряд
            @Override
            public int compare(String o1, String o2) {
                int s1 = StringUtils.ordinalIndexOf(o1,"#",5) + 1;
                int s2 = StringUtils.ordinalIndexOf(o2,"#",5) + 1;
                return o1.substring(0,s1).compareToIgnoreCase(o2.substring(0,s2));
            }
        });

        for (String s :lines) {
            parts.add(s.split("#"));                                              // разделяем линию на составляющие
        }

        for (int i = parts.size()-1; i >= 0 ; i--) {
            if (parts.get(i).length <  10) parts.remove(parts.get(i));
        }

        int x = 0;                                                                      // счетчик количества О8

            for (int i = 0; i < parts.size(); i++) {
                String[] str = parts.get(i);


                // создаем первый О8
                if (i == 0) {
                      o8s.add(new O8(str[0], str[1], str[2], str[3], str[4]));
                    try {
                        if (str.length > 10) o8s.get(0).setParcel(str[11]);              // может отсутствовать
                        if (str.length > 11) o8s.get(0).setDeferment(str[12]);           // может отсутствовать
                        if (str.length > 12) o8s.get(0).setDate(str[13]);                // может отсутствовать
                    } catch (IndexOutOfBoundsException ex) {}
                    o8s.get(0).getGoods().add(new Goods(str[7], str[8], str[9]));        // добавляем товары из первой линии
                }

                // для всех строк кроме 1
                else {
                    // проверяем линия от нового О8 или продолжает уже созданный
                    String[] s1 = parts.get(i);
                    String[] s2 = parts.get(i - 1);

                    if (s1[0].equals(s2[0]) && s1[2].equals(s2[2]) && s1[3].equals(s2[3]) && s1[4].equals(s2[4])) {
                        o8s.get(x).getGoods().add(new Goods(str[7], str[8], str[9]));      // добавляем товары
                    }
                    // создаем новый О8
                    else {
                        o8s.add(new O8(str[0], str[1], str[2], str[3], str[4]));
                        x++;
                        try {
                            if (str.length > 10) o8s.get(x).setParcel(str[11]);
                            if (str.length > 11) o8s.get(x).setDeferment(str[12]);
                            if (str.length > 12) o8s.get(x).setDate(str[13]);
                        } catch (IndexOutOfBoundsException ex) {}
                        try {
                            o8s.get(x).getGoods().add(new Goods(str[7], str[8], str[9]));
                        } catch (Exception ex){}
                    }

                 }
        }
        log.info("Сформировано " + (x + 1) + " О8");
    }

    public void o8Validation(){
        O8 o8 = new O8();
        o8.validation(o8s, o8sFail);
        view.updateJtextAreas(o8s,o8sFail);
    }

    public void o8Merge(){
        for (int i = o8s.size()-1; i > 0; i--) {
            for (int j = i-1; j >= 0; j--) {
                if (o8s.get(i).equals(o8s.get(j))) {
                    o8s.get(i).getGoods().addAll(o8s.get(j).getGoods());
                    o8s.remove(o8s.get(j));
                }
            }
        }
    }


    public String getString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o8s.size() ; i++) {
           sb.append(o8s.get(i).buildString(i+1));
        }
        messages.clear();
        lines.clear();
        o8s.clear();
        o8sFail.clear();
        log.info("Данные для ерп сформированы");
        return sb.toString();
    }
}
