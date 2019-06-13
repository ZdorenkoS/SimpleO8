package project.model;

import org.apache.log4j.Logger;
import project.utils.ConfigProperties;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Email {
    private final static Logger log = Logger.getLogger(Email.class.getName());
    private Properties props;
    private Session session;
    private Store store;
    private Folder folder;
    Properties supp;

    public Email() {
        props = new Properties();
        props.put("mail.debug", "false");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.port", "IMAP_port");
        session = Session.getInstance(props);
        try {store = session.getStore();} catch (NoSuchProviderException ex) {log.error("Сбой при попытке соединения с почтовым сервером", ex);}

    }



    public void connect() {
        try {store.connect(ConfigProperties.getProperty("emailHost"), ConfigProperties.getProperty("emailUser"), ConfigProperties.getProperty("emailPassword"));
             folder = store.getFolder("INBOX");
            log.info("Соединение с почтовым сервером установлено");
        } catch (MessagingException ex) {
            log.error("Сбой при попытке доступа к папкам", ex);
        }

        supp = new Properties();
        try {
            supp.load(new InputStreamReader(new FileInputStream("src/main/resources/supplier.properties"), "cp1251"));
        } catch (IOException ex) {
            System.out.println("Не удалось загрузить список поставщиков");
        }
    }


    public ArrayList<Message> getMessages(){
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            folder.open(Folder.READ_WRITE);
            messages = new ArrayList<Message>(Arrays.asList(folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))));
            if (messages.size()<1) {
                log.info("Новых писем нет");
            }
            else {
                log.info("Получено писем: " + messages.size());}

        } catch (MessagingException ex) {
            log.info("Сбой при попытке доступа к папкам", ex);
        }
        finally {
            if (messages.size()==0) try {folder.close();}
            catch (MessagingException e) {e.printStackTrace();}
            catch (IllegalStateException e) {e.printStackTrace();}
            return messages;
        }
    }

    public ArrayList<String> getLines (ArrayList<Message> messages){
        String s = "";
       ArrayList<String> lines = new ArrayList<>();
        try {
            for (Message m : messages) {
                System.out.println(getTextFromMessage(m).replaceAll("(\\r\\n|\\r|\\n)", "@"));
               s = getTextFromMessage(m).replaceAll("(\\r\\n|\\r|\\n)", "@")
                        .replaceAll("P3001", "~3001")
                        .replaceAll("Р3001", "~3001")
                        .replaceAll("M5005", "~5005")
                        .replaceAll("М5005", "~5005")
                        .replaceAll("@@@@@@", "# # #")
                        .replaceAll("@@@@", "# #")
                        .replaceAll("@@", "#")
                        .replaceAll("@", "");
                lines.addAll(Arrays.asList(s.split("~")));

                for (int i = lines.size() - 1; i >= 0; i--) {
                    if (lines.get(i).length() < 9) {lines.remove(i); continue;}
                    if (!(lines.get(i).startsWith("3001")) && !(lines.get(i).startsWith("5005"))) lines.remove(i);

                }
            }
        } catch (MessagingException | IOException ex) {
            log.info("Ошибка при парсинге строк", ex);
        }
        log.info("Строки прочитаны");
        try {folder.close();} catch (MessagingException e) {e.printStackTrace();}
        return lines;
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

    static private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
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

    public void disconnect() {
        try {store.close();} catch (MessagingException ex) {log.error("Сбой при закрытии коннекта", ex);}
    }
}

