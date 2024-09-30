package org.example;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите свое имя: ");
            String name = scanner.nextLine();
            Socket socket = new Socket("localhost",1400);
            Client client = new Client(socket,name);

            client.listenForMessage();
            client.sendMessage();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}