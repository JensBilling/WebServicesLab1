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
        System.out.println("---2. Log in---");
        int menuChoice = sc.nextInt();
        sc.nextLine();

        switch (menuChoice) {
            case 1:
                //post request to send user data via json
                createAndUploadNewUser();
                break;
            case 2:
                //post request to send user data via json
                //signInExistingUser();
                break;
        }


        try {
            Socket socket = new Socket("localhost", 80);


            PrintWriter output = new PrintWriter(socket.getOutputStream());

            output.print("GET /f1 HTTP/1.1\r\n");
            output.print("Host: localhost\r\n");
            output.print("\r\n");
            output.flush();

            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String line = inputFromServer.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                System.out.println(line);
            }

            inputFromServer.close();
            output.close();
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void createAndUploadNewUser() {
        System.out.println("---Enter username---");
        String username = sc.nextLine();
        System.out.println("---Enter password---");
        String password = sc.nextLine();

        ClientUser newUser = new ClientUser(username, password);

        Gson gson = new Gson();

        // Convert user information to Json
        String toJson = gson.toJson(newUser);



    }
}

