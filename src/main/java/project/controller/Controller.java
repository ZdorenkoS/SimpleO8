package project.controller;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import project.model.Email;
import project.model.Goods;
import project.model.O8;
import project.view.View;

import javax.mail.Message;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Controller extends Thread{
    private final static Logger log = Logger.getLogger(Controller.class.getName());
    private volatile ArrayList<String> lines = new ArrayList<>();
    private volatile ArrayList<O8> o8s = new ArrayList<>();
    private volatile ArrayList<O8> o8sFail = new ArrayList<>();
    private volatile ArrayList<Message> messages;
    private Email email;
    private View view;


    public Controller() {
        email = new Email();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        view = new View();
        SwingUtilities.invokeLater(view);
    }

    public View getView() {
        return view;
    }

    public void getConnect(){
        email.connect();
    }

    public void disconnect() {
        email.disconnect();
    }

    public ArrayList<Message> getMess() {return messages;}

    public void getMesssages() {
        messages = email.getMessages();
    }

    public void getLines() {
        lines = email.getLines(messages);
    }

    //TODO может лучше - одно письмо = один О8 ??
    public void makeO8(){
        ArrayList<String[]> parts = new ArrayList<>();                                  // лист для "кусочков" линии
        Collections.sort(lines, new Comparator<String>() {                              // сортируем линии, получим нужные строки подряд
            @Override
            public int compare(String o1, String o2) {
                int s1 = StringUtils.ordinalIndexOf(o1,"#",5) + 1;
                int s2 = StringUtils.ordinalIndexOf(o2,"#",5) + 1;
                return o1.substring(0,s1).compareTo(o2.substring(0,s2));
            }
        });

        for (String s :lines) {
            parts.add(s.split("#"));                                              // разделяем линию на составляющие
        }

        int x = 0;                                                                      // счетчик количества О8

            for (int i = 0; i < parts.size(); i++) {
                String[] str = parts.get(i);

                if (str.length<10) continue;
                // создаем первый О8
                if (i == 0) {
                      o8s.add(new O8(str[0], str[1], str[2], str[3], str[4]));
                    try {
                        if (str.length > 10) o8s.get(0).setParcel(str[11]);              // может отсутствовать
                        if (str.length > 10) o8s.get(0).setDeferment(str[12]);           // может отсутствовать
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
                            if (str.length > 10) o8s.get(x).setDeferment(str[12]);
                        } catch (ArrayIndexOutOfBoundsException ex) {}
                        o8s.get(x).getGoods().add(new Goods(str[7], str[8], str[9]));
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
