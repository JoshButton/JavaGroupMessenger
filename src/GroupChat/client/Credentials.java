package GroupChat.client;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Credentials {

    public static void register(String username, String password) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            Statement st = conn.createStatement();

            String salt = BCrypt.gensalt();
            String hashedPass = BCrypt.hashpw(password, salt);

            st.executeUpdate("INSERT INTO CREDENTIALS VALUES('" + username + "', '" + hashedPass + "');");

            LoginPrototypeGUI.displayMessage("ACCOUNT CREATED SUCCESSFULLY");

            st.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Credentials.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void login(String username, String password) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            Statement st = conn.createStatement();

            String query = "SELECT * FROM CREDENTIALS;";
            ResultSet rs = st.executeQuery(query);

            String salt = BCrypt.gensalt();

            boolean userexists = false;

            while (rs.next()) {
                String cusername = rs.getString("USERNAME");
                String cpassword = rs.getString("PASSWORD");

                if (cusername.equals(username)) {
                    userexists = true;
                    cpassword = BCrypt.hashpw(password, salt);

                    if (password.trim().equals(cpassword.trim())) {
                        LoginPrototypeGUI.displayMessage("PASSWORD IS CORRECT");
                    } else {
                        LoginPrototypeGUI.displayMessage("PASSWORD IS INCORRECT");
                    }
                }

            }

            rs.close();

            if (!userexists) {
                LoginPrototypeGUI.displayMessage("USER DOES NOT EXIST");
            }

            st.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Credentials.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
