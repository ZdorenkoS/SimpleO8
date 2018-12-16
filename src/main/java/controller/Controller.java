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
        for (O8 o:o8s) {o.validation();}
    }


    public String getString(){
        StringBuilder sb = new StringBuilder();
        int i =1;
        for (O8 o8:o8s) {
            for (int j = 0; j <o8.getGoods().size() ; j++) {
                sb.append(i + "\t");
                if (o8.getStock().equals("3001")) sb.append("P3001\t");
                if (o8.getStock().equals("5005")) sb.append("M5005\t");
                if (o8.getCurrency().equalsIgnoreCase("БЕЗНАЛ")) sb.append("UAH\t");
                if (o8.getCurrency().equalsIgnoreCase("НАЛ")) sb.append("UA2\t");
                if (o8.getCurrency().equalsIgnoreCase("USD")) sb.append("USD\t");
                sb.append(o8.getSupplier()+"\t");
                sb.append(o8.getGoods().get(j).getSku()+"\t");
                sb.append(o8.getGoods().get(j).getQuantity()+"\t");
                sb.append(o8.getGoods().get(j).getPrice()+"\t");
                sb.append(" \t");
                try {
                    if (o8.getDelivery().equalsIgnoreCase("КУРЬЕР")) sb.append("01\t");
                    else if (Long.parseLong(o8.getParcel()) > 0) sb.append("02"+"\t").append(o8.getParcel() + "\t");
                    else sb.append(" \t");
                } catch (NumberFormatException ex) {
                    sb.append(" \t \t");
                }

                sb.append(o8.getInvoice()+"\t");
               try {
                  if (Integer.parseInt(o8.getDeferment()) >0 )sb.append(o8.getDeferment() + "\t");
               } catch (NumberFormatException ex){
                   sb.append(" \t");
               }
                sb.append("\n");
            }
            i++;
        }
        messages.clear();
        lines.clear();
        o8s.clear();
        log.info("Данные для ерп сформированы");
        return sb.toString();
    }




}
