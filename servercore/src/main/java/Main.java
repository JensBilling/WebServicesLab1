import com.google.gson.Gson;
import dao.ServerUserFunctions;
import serverusers.ServerUser;

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

            if (request.getType().equals("GET") || request.getType().equals("HEAD")) {

                if (request.getUrl().contains("/create")) {

                    saveUserToDatabaseFromUrl(request, outputToClient);


                } else {
                    sendFileToClient(request, outputToClient);

                }


            } else if (request.getType().equals("POST")) {
                saveUserToDatabase(request, outputToClient);
            }

            clientInput.close();
            outputToClient.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUserToDatabaseFromUrl(RequestObject request, OutputStream clientOutput) throws IOException {
        String username = request.getUrlParameters().get("username");
        String password = request.getUrlParameters().get("password");

        ServerUser userObject = new ServerUser(username, password);
        ServerUserFunctions.addNewUser(userObject.getUsername(), userObject.getPassword());
        String userCreationResponse = userObject.getUsername() + " account was successfully created";
        byte[] responseBody = userCreationResponse.getBytes(StandardCharsets.UTF_8);

        String header = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: " + responseBody.length + "\r\n\r\n";
        byte[] headerData = header.getBytes();

        clientOutput.write(headerData);
        clientOutput.write(responseBody);
        clientOutput.flush();


    }

    private static void saveUserToDatabase(RequestObject request, OutputStream clientOutput) throws IOException {

        Gson gson = new Gson();
        ServerUser userObject = gson.fromJson(request.getBody(), ServerUser.class);
        ServerUserFunctions.addNewUser(userObject.getUsername(), userObject.getPassword());

        String userCreationResponse = userObject.getUsername() + " account was successfully created";
        byte[] responseBody = userCreationResponse.getBytes(StandardCharsets.UTF_8);

        String header = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-length: " + responseBody.length + "\r\n\r\n";
        byte[] headerData = header.getBytes();

        clientOutput.write(headerData);
        clientOutput.write(responseBody);
        clientOutput.flush();
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
        if (request.getType().equals("HEAD")) {
            clientOutput.write(header.getBytes());
            clientOutput.flush();
        } else if (request.getType().equals("GET")) {
            clientOutput.write(header.getBytes());
            clientOutput.write(data);
            clientOutput.flush();

        }


    }


    private static void sendf1JsonResponse(OutputStream clientOutput) throws IOException {

        List<Formula1Driver> listOfDrivers = new ArrayList<>();
        listOfDrivers.add(new Formula1Driver("Lewis Hamiltodn", 35, true));
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
        RequestObject request = new RequestObject("", "");
        List<Integer> rawRequestAsciiData = new ArrayList<>();

        boolean loop = true;
        while (loop) {
            int x = clientInput.read();
            rawRequestAsciiData.add(x);
            if (x == 125) {
                loop = false;
            }
        }

        String requestDataString = "";
        for (int i = 0; i < rawRequestAsciiData.size(); i++) {
            requestDataString += Character.toString(rawRequestAsciiData.get(i));
        }

        String[] requestArray = requestDataString.split("\r\n");

        String line1 = requestArray[0];
        String contentType = "";
        String contentLength = "";
        String[] requestBodyArray = requestDataString.split("\r\n\r\n");
        String requestBody = requestBodyArray[1];

        for (int i = 0; i < requestArray.length; i++) {

            if (requestArray[i].contains("Content-Type:")) {
                String[] stringSplit = requestArray[i].split(" ");
                contentType = stringSplit[1];
            }
            if (requestArray[i].contains("Content-Length:")) {
                String[] stringSplit = requestArray[i].split(" ");
                contentLength = stringSplit[1];
            }
        }

        if (line1.startsWith("GET")) {
            String url = line1.split(" ")[1];
            request = RequestManagement.getManager(url);
        } else if (line1.startsWith("HEAD")) {
            String url = line1.split(" ")[1];
            request = RequestManagement.headManager(url);
        } else if (line1.startsWith("POST")) {
            String url = line1.split(" ")[1];
            request = RequestManagement.postManager(url, contentType, contentLength, requestBody);
        }

        return request;
    }

}
