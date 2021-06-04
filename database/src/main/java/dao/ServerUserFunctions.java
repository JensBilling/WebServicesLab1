package dao;

import serverusers.ServerUser;

import java.io.OutputStream;

public class ServerUserFunctions {
    public static ServerUserDao serverUserDao = new ServerUserDao();

    public static void addNewUser(String username, String password) {
        serverUserDao.addNewUser(username, password);
    }

    public static ServerUser retrieveUserFromDatabase(int userId) {
        ServerUser su =serverUserDao.retrieveUserFromDatabase(userId);
        return su;
    }
}
