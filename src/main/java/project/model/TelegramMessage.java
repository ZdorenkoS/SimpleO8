package project.model;


import java.io.*;
import java.net.URL;


public class TelegramMessage {
    public static void main(String[] args) throws IOException {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        String apiToken = "1120677318:AAHztDjcxXQ2kH03jaQVMRVeVQCFSjdp6sU";
        String chatId = "801801009";
        String text = "Неужели получилось??";
        urlString = String.format(urlString, apiToken, chatId, text);
        URL url = new URL(urlString);
        url.openConnection().getInputStream();


    }
    }


