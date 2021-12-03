package adunaf.java3lesson2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private String nick;
    private String login;

    public String getLogin() {
        return login;
    }

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;



            System.out.println("RemoteSocketAddress: " + socket.getRemoteSocketAddress());

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {

                    socket.setSoTimeout(120000);
                    while (true) {
                        String str = in.readUTF();


                        if (str.equals("/end")) {
                            sendMsg("/end");
                            throw new RuntimeException("отключаемся");
                        }


                        if (str.startsWith("/reg ")) {
                            String[] token = str.split(" ");
                            boolean b = server.getAuthService()
                                    .registration(token[1], token[2], token[3]);
                            if (!b) {
                                sendMsg("Ошибка: с этим логином уже Зарегистированы.");
                            } else {
                                sendMsg("Регистрация прошла успешно.");
                            }
                        }

                        if (str.startsWith("/auth ")) {
                            String[] token = str.split(" ");
                            String newNick = null;
                            try {
                                newNick = server.getAuthService()
                                        .getNicknameByLoginAndPassword(token[1], token[2]);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (newNick != null) {
                                if (!server.isLoginAuthorized(token[1])) {
                                    login = token[1];
                                    sendMsg("/authok " + newNick);
                                    nick = newNick;
                                    server.subscribe(this);
                                    System.out.println("Клиент " + nick + " подключился");
                                    socket.setSoTimeout(0);
                                    break;
                                } else {
                                    sendMsg("С этим логином уже авторизовались");
                                }
                            } else {
                                sendMsg("Неверный логин / пароль");
                            }
                        }

                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                break;
                            }
                            if (str.startsWith("/w ")) {
                                String[] token = str.split(" ", 3);
                                server.privateMsg(this, token[1], token[2]);
                            } else
                            if (str.startsWith("/newNick ")) {
                                String[] token = str.split(" ", 2);
                                server.setNewNick(this, token[1]);
                            }

                        } else {
                            server.broadcastMsg(nick, str);
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println("by");
                } catch (SocketTimeoutException e) {
                    sendMsg("/end");
                    System.out.println("by time out");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
                    System.out.println("Клиент отключился");
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNick(String newNick) {
        nick = newNick;
    }
}
