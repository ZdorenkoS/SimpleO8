package Controller;

import Model.Connect;
import Model.Goods;
import Model.O8;
import org.apache.log4j.Logger;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Controller {
    private final static Logger log = Logger.getLogger(Controller.class.getName());
    private ArrayList<String> lines = new ArrayList<>();
    private ArrayList<O8> o8s = new ArrayList<>();
    private static Message[] messages;
    private Connect connect;

    public Controller() {
        connect = new Connect();
    }

    public void getConnect(){
        connect.connect();
    }

    public void disconnect() {
        connect.disconnect();
    }

    public ArrayList<O8> getO8s() {
        return o8s;
    }

    public void getMesssages() {
        try {
            messages = connect.getFolder().search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (messages == null) log.info("Новых писем нет");
            else log.info("Получено писем: " + messages.length);
        } catch (MessagingException ex) {
            log.info("Сбой при попытке доступа к папкам", ex);
        }
    }
//TODO удалить пробелы из номера ттн
    public void getLines() {
        String s = "";
        try {
            for (Message m : messages) {
                s = getTextFromMessage(m)
                        .replaceAll("\\p{Cntrl}", "@")
                        .replaceAll("@.3001", "#3001")
                        .replaceAll("@.5005", "#5005")
                        .replaceAll("@@@@@@@@@@@@", "_ _")
                        .replaceAll("@@@@@@@@", "_ _")
                        .replaceAll("@@@@@@", "_")
                        .replaceAll("@@@@", "_")
                        .replaceAll("@", "");
                lines.addAll(Arrays.asList(s.split("#")));

                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).startsWith("_") || lines.get(i).equals("")) lines.remove(i);
                }

            }
        } catch (MessagingException | IOException ex) {
            log.info("Ошибка при парсинге строк", ex);
        }
        log.info("Строки прочитаны");
    }
// TODO сделать проверку на одинаковые коды товара и цену
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

                 log.info("Создан О8 № "+x);
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
                     log.info("Создан О8 № " + x);
                 }
            }
        }
    }

    public String getString(){
        StringBuilder sb = new StringBuilder();
        int i =1;
        for (O8 o8:o8s) {
            for (int j = 0; j <o8.getGoods().size() ; j++) {
                sb.append(i + "\t");
                sb.append(o8.getStock()+"\t");
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
        log.info("Данные для ерп сформированы");
        return sb.toString();
    }



    static private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    static private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
