package dao;

public class ServerUserFunctions {
    public static ServerUserDao serverUserDao = new ServerUserDao();

    public static void addNewUser(String username, String password) {
        serverUserDao.addNewUser(username, password);
    }
}
