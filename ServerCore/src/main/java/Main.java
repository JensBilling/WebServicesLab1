import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(80)){
            Socket client = serverSocket.accept();
            executorService.submit(() -> handleConnection(client));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void handleConnection(Socket client) {

        try {
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String url = readUrl(clientInput);

            OutputStream clientOutput = client.getOutputStream();

            sendJsonResponse(clientOutput);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendJsonResponse(OutputStream clientOutput) throws IOException {

        List<Formula1Driver> listOfDrivers = new ArrayList<>();
        listOfDrivers.add(new Formula1Driver("Lewis Hamilton", 35, true));
        listOfDrivers.add(new Formula1Driver("Ayrton Senna", 70, false));
        listOfDrivers.add(new Formula1Driver("Nikita Mazepin", 21, true));

        Gson gson = new Gson();

        String toJson = gson.toJson(listOfDrivers);
        byte[] jsonData = toJson.getBytes(StandardCharsets.UTF_8);

        String header = "HTTP/1.1 200 OK\r\nContent-Type: applications/json\r\nContent-length: " + jsonData.length + "\r\n\r\n";
        byte[] headerData = header.getBytes();

        clientOutput.write(headerData);
        clientOutput.write(jsonData);


    }

    private static String readUrl(BufferedReader clientInput) throws IOException {
        String url = "";
        while (true) {
            String line = clientInput.readLine();

            if  (line.startsWith("GET")) {
                url = line.split(" ")[1];
            }
            if (line == null || line.isEmpty()){
                break;
            }
        }
        return url;
    }
}