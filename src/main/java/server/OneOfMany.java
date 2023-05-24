package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static server.ServerChat.userMap;
import static server.Story.outData;
import static server.Story.writeLog;

class OneOfMany extends Thread {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    public  String nickName;

    public OneOfMany(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        try {
            nickName = selectNick();
            writeLog(outData() + " Client with assigned port " + socket.getPort() + " chose a nickname: " + nickName);
            userMap.put(socket.getPort(), nickName);
        } catch (IOException e) {
            Story.writeLog(outData() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
        ServerChat.story.printStory(out);
        start();
    }
    @Override
    public void run() {
        String message;
            try {
                while (true) {
                    message = in.readLine();
                    if(message.contains("/exit")) {
                        writeLog(outData() + " Client with assigned port " + socket.getPort() + " and a nickname: " + userMap.get(socket.getPort()) + " left the chat");
                        userMap.remove(socket.getPort());
                        this.downService();
                        break;
                    }
                    System.out.println("Echo: " + message);
                    if(message.contains("/exit")){
                        message = outData() + " " + message;
                    }
                    writeLog(message);
                    ServerChat.story.addStoryEl(" " + message);
                    for (OneOfMany vr : ServerChat.serverList) {
                        vr.send(message);
                    }
                }
        } catch (IOException e) {
                try {
                    Story.writeLog(outData() + " " + e.getMessage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                this.downService();
        }
    }

    String selectNick() throws IOException {
        String message;
        while (true) {
            message = in.readLine();
            if (userMap.containsValue("@" + message)) {
                out.write("Nick is already busy. choose another one:\n");
                out.flush();
            } else {
                out.write("@" + message + "\n");
                out.flush();
                nickName = "@" + message;
                break;
            }
        }
        return nickName;
    }
    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                int i = socket.getPort();
                socket.close();
                in.close();
                out.close();
                userMap.remove(i);
                writeLog("<" + outData() + ">" + " The server has finished its work with the port " + i);
            }
        } catch (IOException ignored) {}
    }
}