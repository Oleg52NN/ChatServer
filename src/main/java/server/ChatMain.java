package server;

import java.io.*;

public class ChatMain {


    public static void main(String[] args) throws IOException {
    Server server = new Server();
    server.listen();
    }
}
