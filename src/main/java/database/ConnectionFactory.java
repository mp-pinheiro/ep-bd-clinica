package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
	public static Connection getConnection(){
        try {
            String opt = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=" + TimeZone.getDefault().getID();;
            Connection con =  DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica_ep" + opt, "root", "1234");
            return con;
        } catch (SQLException e) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, "Cannot connect.", e);
            System.exit(0);
        }
        return null;
    }
}
