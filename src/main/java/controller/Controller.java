package controller;

import model.Email;
import model.Goods;
import model.O8;
import org.apache.log4j.Logger;

import javax.mail.Message;
import java.util.ArrayList;
import java.util.Collections;

public class Controller extends Thread{
    private final static Logger log = Logger.getLogger(Controller.class.getName());
    private ArrayList<String> lines = new ArrayList<>();
    private ArrayList<O8> o8s = new ArrayList<>();
    private ArrayList<O8> o8sFail = new ArrayList<>();
    private ArrayList<Message> messages;
    private Email email;

    public Controller() {
        email = new Email();
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

    public ArrayList<O8> getO8s() {
        return o8s;
    }

    public ArrayList<O8> getO8sFail() {
        return o8sFail;
    }

    public void makeO8(){
        ArrayList<String[]> parts = new ArrayList<>();                                  // лист для "кусочков" линии
        Collections.sort(lines);                                                        // сортируем линии, получим нужные строки подряд
        for (String s :lines) {
            parts.add(s.split("_"));                                              // разделяем линию на составляющие
        }
        int x = 0;                                                                      // счетчик количества О8
        for (int i = 0; i <parts.size() ; i++) {
             String [] str = parts.get(i);
             // создаем первый О8
             if (i==0) {o8s.add(new O8(str[0], str[1], str[2], str[3], str[4]));
                       try {
                           if (str.length > 10) o8s.get(0).setParcel(str[11]);              // может отсутствовать
                           if (str.length > 10) o8s.get(0).setDeferment(str[12]);           // может отсутствовать
                       } catch (IndexOutOfBoundsException ex ){}
                        o8s.get(0).getGoods().add(new Goods(str[7],str[8], str[9]));     // добавляем товары из первой линии

                 log.info("Создан О8 № "+(x+1));
             }
             // для всех строк кроме 1
             else {
                 // проверяем линия от нового О8 или продолжает уже созданный
                 if (parts.get(i)[0].equals(parts.get(i-1)[0]) && parts.get(i)[2].equals(parts.get(i - 1)[2]) && parts.get(i)[3].equals(parts.get(i - 1)[3])){
                  o8s.get(x).getGoods().add(new Goods(str[7], str[8], str[9]));      // добавляем товары
                 }
                 // создаем новый О8
                 else {
                     o8s.add(new O8(str[0], str[1], str[2], str[3], str[4]));
                     x++;
                     try{
                     if (str.length > 10) o8s.get(x).setParcel(str[11]);
                     if (str.length > 10) o8s.get(x).setDeferment(str[12]);
                     } catch(IndexOutOfBoundsException ex ){
                 }
                     o8s.get(x).getGoods().add(new Goods(str[7], str[8], str[9]));
                     log.info("Создан О8 № " + (x + 1));
                 }
            }
        }
    }

    public void o8Validation(){
        O8 o8 = new O8();
        o8.validation(o8s, o8sFail);
    }

    public void updateView(){

    }

    public String getString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o8s.size() ; i++) {
           sb.append(o8s.get(i).buildString(i+1));
        }
        messages.clear();
        lines.clear();
        o8s.clear();
        log.info("Данные для ерп сформированы");
        System.out.println(sb.toString());
        return sb.toString();
    }




}
