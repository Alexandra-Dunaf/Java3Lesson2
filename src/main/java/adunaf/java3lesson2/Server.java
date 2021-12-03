package adunaf.java3lesson2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;
    private DBAuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;
        try {
            authService = new DBAuthService();
            System.out.println("Соединение с базой данных установлено");
            try {
                server = new ServerSocket(8081);
                System.out.println("Сервер запущен");

                while (true){
                    socket = server.accept();
                    System.out.println("Клиент подключился");
                    new ClientHandler(this,socket);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    server.close();
                    authService.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String nick, String msg){
        for (ClientHandler c:clients ) {
            c.sendMsg( nick +" : "+  msg);
        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg){
        String message = String.format("[ %s ] private [ %s ] : %s",
                sender.getNick(), receiver, msg);

        for (ClientHandler c:clients ) {
            if(c.getNick().equals(receiver)){
                c.sendMsg(message);
                sender.sendMsg(message);
                return;
            }
        }
        sender.sendMsg("Пользователь с ником: "+ receiver +" не найден");
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientlist();
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientlist();
    }

    public boolean isLoginAuthorized(String login){
        for (ClientHandler c:clients ) {
            if (c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public void broadcastClientlist(){
        StringBuilder sb = new StringBuilder("/clientlist ");
        for (ClientHandler c:clients ) {
            sb.append(c.getNick()+" ");
        }

        String msg = sb.toString();
        for (ClientHandler c:clients ) {
            c.sendMsg(msg);
        }
    }

    public void setNewNick(ClientHandler clientHandler, String newNick) {
        try {
            if (!authService.isNickExists(newNick)){
                authService.updateNick(clientHandler.getNick(),newNick);
                clientHandler.setNick(newNick);
                broadcastClientlist();
                clientHandler.sendMsg("Ник изменен");
                clientHandler.sendMsg("/newNick " + newNick);
            } else {
                clientHandler.sendMsg("Такой ник уже существует:" + newNick);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}