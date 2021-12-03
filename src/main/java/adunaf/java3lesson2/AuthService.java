package adunaf.java3lesson2;

import java.sql.SQLException;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password) throws SQLException;
    boolean registration(String login, String password, String nickname);

}