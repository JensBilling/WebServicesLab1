import com.google.gson.Gson;
import dao.ServerUserFunctions;
import serverusers.ServerUser;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
                } else if (request.getUrl().contains("/getuser")) {
                    retrieveUserFromDatabase(request, outputToClient);
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

    private static void retrieveUserFromDatabase(RequestObject request, OutputStream outputToClient) throws IOException {
        String stringUserId = request.getUrlParameters().get("id");
        int userId = Integer.parseInt(stringUserId);
        ServerUser su = ServerUserFunctions.retrieveUserFromDatabase(userId);

        Gson gson = new Gson();
        String userInformationResponse = gson.toJson(su);
        byte[] responseBody = userInformationResponse.getBytes(StandardCharsets.UTF_8);

        String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-length: " + responseBody.length + "\r\n\r\n";
        byte[] headerData = header.getBytes(StandardCharsets.UTF_8);

        outputToClient.write(headerData);
        outputToClient.write(responseBody);
        outputToClient.flush();
    }

    private static void saveUserToDatabaseFromUrl(RequestObject request, OutputStream clientOutput) throws IOException {
        String username = request.getUrlParameters().get("username");
        String password = request.getUrlParameters().get("password");

        ServerUser userObject = new ServerUser(username, password);
        addNewUser(clientOutput, userObject);
    }

    private static void addNewUser(OutputStream clientOutput, ServerUser userObject) throws IOException {
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
        addNewUser(clientOutput, userObject);
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
                // Vad gör fileInputStream?
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

    private static RequestObject readRequest(BufferedReader clientInput) throws IOException {
        RequestObject request = new RequestObject("", "");

        int contentLength = 0;
        // contentLengthString is useless
        String contentLengthString = "";
        String contentType = "";
        String line1 = "";

        while (true) {
            String line = clientInput.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            if (line.contains("HTTP/1.1")) {
                line1 = line;
            }
            if (line.contains("Content-Length:")) {
                String[] splitLine = line.split(" ");
                contentLengthString = splitLine[1];
                contentLength = Integer.parseInt(splitLine[1]);
            }
            if (line.contains("Content-Type:")) {
                String[] splitLine = line.split(" ");
                contentType = splitLine[1];
            }
        }

        String responseBody = "";
        for (int i = 0; i < contentLength; i++) {
            int asciiInt = clientInput.read();
            responseBody += Character.toString(asciiInt);
        }

        if (line1.startsWith("GET")) {
            String url = line1.split(" ")[1];
            request = RequestManagement.getManager(url);
        } else if (line1.startsWith("HEAD")) {
            String url = line1.split(" ")[1];
            request = RequestManagement.headManager(url);
        } else if (line1.startsWith("POST")) {
            String url = line1.split(" ")[1];
            request = RequestManagement.postManager(url, contentType, contentLengthString, responseBody);
        }
        return request;
    }
}
