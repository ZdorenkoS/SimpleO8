package Model;

import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
public class GetMail {
   private List<String> lines = new ArrayList<>();
   private static Message[] messages;
   private final static Logger log;

        static {
        log = Logger.getLogger(GetMail.class.getName());
        Properties props = new Properties();
        props.put("mail.debug", "false");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.port", "IMAP_port");
            try {Session session = Session.getDefaultInstance(props);
                 Store store = session.getStore();
                 store.connect("imap.ukr.net", "d.a.o.s@ukr.net", "Nikita2012");
                 Folder folder = store.getFolder("INBOX");
                 folder.open(Folder.READ_WRITE);
                 messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                 log.info("Соединение с почтовым сервером установлено");
                 if (messages==null) log.info("Новых писем нет");
                 else log.info("Получено "+messages.length+" писем");
        } catch (MessagingException ex) {
                log.error("Сбой при попытке соединения с почтовым сервером",ex);}
    }




    public ArrayList<O8> getMails() {
          ArrayList<O8> list = new ArrayList<>();
            String s ="";
          for (Message m : messages){
                try {
                 s=getTextFromMessage(m)
                      .replaceAll("\\p{Cntrl}", "@")
                      .replaceAll("@.3001","#3001")
                      .replaceAll("@.5005","#5005")
                      .replaceAll("@@@@@@@@@@@@", "_ _")
                      .replaceAll("@@@@@@@@", "_ _")
                      .replaceAll("@@@@@@", "_")
                      .replaceAll("@@@@", "_")
                      .replaceAll("@", "");
                    lines.addAll(Arrays.asList(s.split("#")));

                    for (int i = 0; i < lines.size(); i++) {
                        if (lines.get(i).startsWith("_")) lines.remove(i);
                    }

                } catch (MessagingException | IOException ex){
                    log.error("Сбой при попытке чтения текста сообщения", ex);
                }
            }

            for (String str :lines) {
                System.out.println(str);
                list.add(new O8());
            }
            log.info("О8 сформированы");


        return list;
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
