package server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.Files.notExists;
import static server.ChatHistoryAndLogging.outData;
import static server.ChatHistoryAndLogging.writeLog;

public class Server {
    static String fileSeparator = File.separator;
    static final String CHAT_DIR = "Chat" + fileSeparator;
    private static final String CONFIG_FILE = "server.cfg";
    static final Path WORK_DIR = Path.of(System.getProperty("user.home") + fileSeparator + CHAT_DIR);
    private static final Path CFG_FILE = Path.of(WORK_DIR + fileSeparator + CONFIG_FILE);
    private static final Properties properties = new Properties();
    private static int PORT;
    public static ConcurrentHashMap<Integer, String> userMap = new ConcurrentHashMap<>();
    public static LinkedList<WorkingWithChatVisitor> serverList = new LinkedList<>();
    public static ChatHistoryAndLogging story;
    public static final String serverStart = "Server Started";


    public void listen() throws IOException {
        try {
            fileConfig();
        } catch (IOException e) {
            ChatHistoryAndLogging.writeLog(outData() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
        story = new ChatHistoryAndLogging();
        System.out.println(serverStart);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            writeLog("<" + outData() + ">" + " " + serverStart);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    writeLog(outData() + " A client has joined the chat. Dedicated port:" + clientSocket.getPort());
                    serverList.add(new WorkingWithChatVisitor(clientSocket));
                } catch (IOException e) {
                    ChatHistoryAndLogging.writeLog(outData() + " " + e.getMessage());
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            ChatHistoryAndLogging.writeLog(outData() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void fileConfig() throws IOException {

        if (!Files.isDirectory(WORK_DIR) || !Files.exists(CFG_FILE)) {
            System.out.println("Configuration file not found");
            writeLog(outData() + " Configuration file not found");
            System.out.println("A configuration file has been created" + " " + CFG_FILE);
            PORT = 8080;
            System.out.println("port = " + PORT);
            writeLog(outData() + " A configuration file has been created" + CFG_FILE);
            writeLog(outData() + " A server configuration file has been created. Assigned Port: 8080");
            System.out.println(Charset.defaultCharset());
            if (notExists(WORK_DIR)) {
                Files.createDirectory(WORK_DIR);
            }
            Files.createFile(CFG_FILE);
            Files.writeString(CFG_FILE, "PORT=8080");
            new File(String.valueOf(CFG_FILE));
        } else {
            File file = new File(String.valueOf(CFG_FILE));
            properties.load(new FileReader(file));
            PORT = Integer.parseInt(properties.getProperty("PORT"));
        }
    }
}
