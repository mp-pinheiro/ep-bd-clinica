package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

import database.ConnectionFactory;

public class DAO {
	protected Connection connection;
	
	public DAO() {
		connection = ConnectionFactory.getConnection();
	}
	
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
