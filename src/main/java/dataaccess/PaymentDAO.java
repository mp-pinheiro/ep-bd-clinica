package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import system.Patient;
import system.Payment;

public class PaymentDAO extends DAO {
	public Payment getPayment(int code) {
		String query = "SELECT * FROM consulta "
				+ "WHERE cod_consulta = ?";
		PreparedStatement preparedStatement;
		Payment payment = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, code);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				payment = new Payment();
				payment.setCode(rs.getInt("cod_consulta"));
				payment.setValue(rs.getInt("valor_pagamento"));
				payment.setType(rs.getInt("tipo_pagamento"));
				payment.setStatus(rs.getBoolean("status_pagamento"));
				
				return payment;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void insertPayment(Payment payment) {
		try {
			//Manual commits
			connection.setAutoCommit(false);
			
			//Client insertion SQL
			String statement = "INSERT INTO pagamento (cod_consulta, valor_pagamento, tipo_pagamento, status_pagamento) "
					+ "VALUES (?, ?, ?, ?)";
			
			//Creates statement
			PreparedStatement preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, payment.getCode());
			preparedStatement.setInt(2, payment.getValue());
			preparedStatement.setInt(3, payment.getType());
			preparedStatement.setBoolean(4, payment.getStatus());
			
			//Inserts payment
			preparedStatement.execute();
			
			//Commits and returns
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
