package org.dukeofxor.jtalker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

	private static DB intance = null;
	
	private Connection connection;
	
	protected DB(){
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.home" + "/.JTalker/DB.db"));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Initialized DB successfully");
	}
	
	public static DB getInstance(){
		if(intance == null){
			intance = new DB();
		}
		return intance;
	}
	
	public Connection getConnection(){
		return connection;
	}
}
