package org.dukeofxor.jtalker.db.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.dukeofxor.jtalker.db.DB;


public class DBController {
	private Connection dbConnection = null;
	private Statement stmt = null;
	
	public DBController() {
		dbConnection = DB.getInstance().getConnection();
		try {
			stmt = dbConnection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createTable();
	}

	private void createTable() {
		String sqlCreate = "Create Table If not exists bannedPlayer(ip vatchar(10));";
		try {
			stmt.execute(sqlCreate);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Fehler bei CreateTable");
		}
	}
	
	public void addBannedPlayer(String ip){
		String sql = "INSERT INTO bannedPlayer (ip) VALUES ('" + ip + "');";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Fehler bei addBannedPlayer");
		}
	}
	
	public void removeBannedPlayer(String ip){
		String sqlRemove = "Delete from bannedPlayer where ip = '" + ip + "';";
		try {
			stmt.executeUpdate(sqlRemove);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Fehler bei RemoveBannedPlayer");
		}
	}
	
	public ArrayList<String> getBannedPlayers(){
		String sqlSelect = "Select * from bannedPlayer;";
		ArrayList<String> bannedPlayers = new ArrayList<String>();
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sqlSelect);
			for (int i=1; rs.next(); i++)
			{
				bannedPlayers.add(rs.getString(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bannedPlayers;
	}
}
