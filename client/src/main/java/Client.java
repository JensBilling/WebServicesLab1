import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("---What do you want to do?---");
        System.out.println("---1. Create new user---");
        System.out.println("---2. Retrieve user information---");
        int menuChoice = sc.nextInt();
        sc.nextLine();

        switch (menuChoice) {
            case 1:
                createAndUploadNewUser();
                break;
            case 2:
                retrieveUserFromDatabase();
                break;
        }
    }

    private static void retrieveUserFromDatabase() {
        System.out.println("---Enter id of user---");
        int userId = sc.nextInt();
        sc.nextLine();

        try {
            Socket socket = new Socket("localhost", 80);
            PrintWriter output = new PrintWriter(socket.getOutputStream());

            output.print("GET /getuser?id="+ userId + " HTTP/1.1\r\n");
            output.print("Host: localhost\r\n");
            output.print("\r\n");
            output.flush();

            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int contentLength = 0;

            while (true) {
                String line = inputFromServer.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                if (line.contains("Content-length:")) {
                    String[] splitLine = line.split(" ");
                    contentLength = Integer.parseInt(splitLine[1]);
                }
            }

            responseBody(socket, output, inputFromServer, contentLength);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void responseBody(Socket socket, PrintWriter output, BufferedReader inputFromServer, int contentLength) throws IOException {
        String responseBody = "";
        for (int i = 0; i < contentLength; i++) {
            int asciiInt = inputFromServer.read();
            responseBody += Character.toString(asciiInt);
        }

        System.out.println(responseBody);
        inputFromServer.close();
        output.close();
        socket.close();
    }

    private static void createAndUploadNewUser() {
        System.out.println("---Enter username---");
        String username = sc.nextLine();
        System.out.println("---Enter password---");
        String password = sc.nextLine();

        ClientUser newUser = new ClientUser(username, password);

        Gson gson = new Gson();
        String toJson = gson.toJson(newUser);

        try {
            Socket socket = new Socket("localhost", 80);
            PrintWriter output = new PrintWriter(socket.getOutputStream());

            output.print("POST / HTTP/1.1\r\n");
            output.print("Host: localhost\r\n");
            output.print("Content-Type: application/json\r\n");
            output.print("Content-Length: " + toJson.length() + "\r\n");
            output.print("\r\n");
            output.print(toJson);
            output.flush();

            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int contentLength = 0;

            while (true) {
                String line = inputFromServer.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                System.out.println(line);
                if (line.contains("Content-length:")) {
                    String[] splitLine = line.split(" ");
                    contentLength = Integer.parseInt(splitLine[1]);
                }
            }

            responseBody(socket, output, inputFromServer, contentLength);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

