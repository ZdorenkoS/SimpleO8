package Controller;

import Model.Connect;
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

public class Controller {
    private final static Logger log = Logger.getLogger(Controller.class.getName());
    public ArrayList<String> lines = new ArrayList<>();
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

    public void getMesssages() {
        try {
            messages = connect.getFolder().search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (messages == null) log.info("Новых писем нет");
            else log.info("Получено " + messages.length + " писем");
        } catch (MessagingException ex) {
            log.info("Сбой при попытке доступа к папкам", ex);
        }
    }

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
                    if (lines.get(i).startsWith("_")) lines.remove(i);
                }

            }
        } catch (MessagingException | IOException ex) {
            log.info("Ошибка при парсинге строк", ex);
        }
        log.info("Строки прочитаны");
    }

    public void createO8(){
        for (String s : lines) {
            String [] element = s.split("_");
            if (o8s.size()==0) o8s.add(new O8());
        }


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
