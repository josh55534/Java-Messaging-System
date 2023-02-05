import java.sql.*;
import java.util.ArrayList;

public class Database {
    String connStr;
    Connection connection;
    PreparedStatement statement;
    ResultSet result;

    public Database(String dbPathname) {
        connStr = "jdbc:sqlite:" + dbPathname;
    }

    public void conOpen() {
		try {
			connection = DriverManager.getConnection(connStr);
		}
		catch (SQLException e) {
			System.out.println("It broke Connecting");
		}
	}
	private void conClose() {
		try {
			connection.close();
		}
		catch (SQLException e) {
			System.out.println("It broke Disconnecting");
		}
	}
}
