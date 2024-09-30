package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{
    private final Socket socket;
    public final static ArrayList<ClientManager> clients = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    ClientManager (Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату");
            broadcastMessage(name + " подключился к чату");
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String message) {
        try {
            String[] d = message.split(" ");
            if (d[1].equals("/tell")) {
                if (d.length > 3) {
                    StringBuilder messageBuilder = new StringBuilder(name).append(" написал вам личное сообщение: ");
                    for (int i = 3; i < d.length; i++) {
                        messageBuilder.append(d[i]).append(" ");
                    }
                    message = messageBuilder.toString();
                    boolean isFounded = false;
                    for (ClientManager client : clients) {
                        if (client.name.equals(d[2])) {
                            client.bufferedWriter.write(message);
                            client.bufferedWriter.newLine();
                            client.bufferedWriter.flush();
                            isFounded = true;
                            break;
                        }
                    }
                    if (!isFounded) {
                        this.bufferedWriter.write("Пользователь " + d[2] + " не найден");
                        this.bufferedWriter.newLine();
                        this.bufferedWriter.flush();
                    }
                }
            } else {
                for (ClientManager client : clients) {
                    if (!client.name.equals(name)) {
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                }
            }
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат");
        broadcastMessage(name + " покинул чат");
    }
}
