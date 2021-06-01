import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(80)) {

            while (true) {
                Socket client = serverSocket.accept();
                executorService.submit(() -> handleConnection(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket client) {

        try {
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            RequestObject request = readRequest(clientInput);

            OutputStream outputToClient = client.getOutputStream();

            // Respond to client with correct output
            sendFileToClient(request, outputToClient);



            clientInput.close();
            outputToClient.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFileToClient(RequestObject request, OutputStream clientOutput) throws IOException {
        String header = "";
        byte[] data = new byte[0];


        File f = new File(request.getUrl());


        if (!(f.exists() && !f.isDirectory())) {
            header = "HTTP/1.1 404 Not Found\r\nContent-length: 0\r\n\r\n";
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(f)) {
                data = new byte[(int) f.length()];
                fileInputStream.read(data);
                var contentType = Files.probeContentType(f.toPath());

                header = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\nContent-length: " + data.length + "\r\n\r\n";
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        clientOutput.write(header.getBytes());
        clientOutput.write(data);
        clientOutput.flush();
    }


    private static void sendf1JsonResponse(OutputStream clientOutput) throws IOException {

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
        clientOutput.flush();


    }

    private static RequestObject readRequest(BufferedReader clientInput) throws IOException {
        // Perhaps bodgy initialize of request
        RequestObject request = new RequestObject("", "");
        while (true) {
            String line = clientInput.readLine();

            if (line.startsWith("GET")) {
                String url = line.split(" ")[1];
                request = RequestManagement.getManager(url);

            } else if (line.startsWith("HEAD")) {
                String url = line.split(" ")[1];
                request = RequestManagement.headManager(url);
            } else if (line.startsWith("POST")) {
                String url = line.split(" ")[1];
                request = RequestManagement.postManager(url);
            }

            if (line == null || line.isEmpty()) {

                break;
            }
        }
        return request;
    }
}