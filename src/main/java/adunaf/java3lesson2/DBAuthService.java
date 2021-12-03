package adunaf.java3lesson2;

import java.sql.*;

public class DBAuthService implements AuthService {
    private static Connection connection;
    private static Statement stmt;


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) throws SQLException {

        PreparedStatement psSelect = connection.prepareStatement
                ("SELECT nick FROM users WHERE  login = ? AND password = ?;");
        psSelect.setString(1, login);
        psSelect.setString(2, password);
        ResultSet rs = psSelect.executeQuery();
        if (rs.next()){
            return rs.getString("nick");
        } else {
            return null;
        }
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return false;
    }

    public boolean isNickExists(String nick) throws SQLException {
        PreparedStatement psSelect = connection.prepareStatement
                ("SELECT id FROM users WHERE  nick = ? ;");
        psSelect.setString(1, nick);
        ResultSet rs = psSelect.executeQuery();
        if (rs.next()){
            return true;
        } else {
            return false;
        }
    }


    public int updateNick(String oldNick, String newNick) throws SQLException {
        PreparedStatement psSelect = connection.prepareStatement
                ("UPDATE users set nick = ? where nick=? ;");
        psSelect.setString(1, newNick);
        psSelect.setString(2, oldNick);

        return(psSelect.executeUpdate());
    }

    public DBAuthService() throws ClassNotFoundException, SQLException {
        connect();
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        stmt = connection.createStatement();
    }

    public void disconnect(){
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

}
