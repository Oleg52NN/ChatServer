package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static server.Server.WORK_DIR;
import static server.Server.userMap;


public class ChatHistoryAndLogging {
    private final LinkedList<String> briefChatHistory = new LinkedList<>();


    public void addStoryEl(String text) throws IOException {
        if (briefChatHistory.size() >= 7) {
            briefChatHistory.removeFirst();
            briefChatHistory.add(text);
        } else {
            briefChatHistory.add(text);
        }
    }


    public void printStory(BufferedWriter writer) throws IOException {
        if(briefChatHistory.size() > 0) {
            writeLog(outData() + " Клиенту отправлена краткая история сообщений и количество действующих участников чата");
            try {
                writer.write("Recent posts" + "\n");
                for (String vr : briefChatHistory) {
                    writer.write(vr + "\n");
                }
                writer.write("-------------" + "\n");
                writer.write("Now in the chat " + userMap.size() + " of visitors\n");
                writer.flush();
            } catch (IOException ignored) {}

        }

    }
static void writeLog(String text) throws IOException {

        Path path = Path.of(WORK_DIR + "//server_chat.log");
        if(!Files.isDirectory(WORK_DIR)){
        Files.createDirectory(WORK_DIR);
        }
        try {
            String s = text + "\n";
            Files.write(path,
                    s.getBytes(),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            );

        } catch (IOException e) {
            ChatHistoryAndLogging.writeLog(outData() + " " + e.getMessage());
            e.printStackTrace();
        }

    }
    public static String outData(){
        Date time = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.yy | HH:mm:ss");
        return dt1.format(time);
    }
}
