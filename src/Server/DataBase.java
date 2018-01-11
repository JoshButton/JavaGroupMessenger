package Server;


import java.sql.*;

import java.util.ArrayList;

import java.awt.List;

import java.net.*;

public class DataBase {

	Statement st;

	String reason,screenName;

	boolean response;

	ArrayList<String> OnlineUsers = new ArrayList<String>();

	ArrayList<String> OfflineUsers = new ArrayList<String>();

	public DataBase() {
		try{
			Class.forName("org.h2.Driver");
			Connection con = DriverManager.getConnection("jdbc:h2:~/test","sa","sa");
			st = con.createStatement();
			st.executeUpdate("DROP TABLE IF EXISTS CREDENTIALS;");
			st.executeUpdate("CREATE TABLE CREDENTIALS(username VARCHAR(15) PRIMARY KEY, password VARCHAR(100), screenname VARCHAR(15), loggedon VARCHAR(5));");
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized  boolean registerUser(String userName, String Password, String screenName) throws SQLException{
		try {
			String name = userName;
			String password = Password;
			String sn = screenName;
			String query = "SELECT * FROM CREDENTIALS;";
			ResultSet rs = st.executeQuery(query);
			boolean userexists = false;
			boolean screenNameExist = false;
			while (rs.next()){
				String username = rs.getString("USERNAME");
				String screen = rs.getString("screenname");
				if(username.equals(name)){
					userexists = true;
				}
				if(sn.equals(screen)){
					screenNameExist = true;
				}
			}
			if(userexists){
				response=false;
				reason="Username Already Exists!";
			}
			else if(screenNameExist) {
				response=false;
				reason="Screen Name Already Exists!";
			}
			else{
				st.executeUpdate("INSERT INTO CREDENTIALS VALUES ('"+name+"','"+password+"','"+screenName+"','"+false+"');");
				response = true;
				reason ="";
			}

			rs.close();
			return response;

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return response;

	}

	public boolean checkLogin(String userName, String password){

		try { 
			String name = userName;
			String Password = password;
			String query = "SELECT * FROM CREDENTIALS;";
			ResultSet rs = st.executeQuery(query);
			boolean success = false;
			boolean userExists=false;
			boolean loggedOn=false;
			while (rs.next()){
				String username = rs.getString("USERNAME");
				String userpassword = rs.getString("PASSWORD");
				if(username.equals(name)) {
					userExists=true;
					if(username.equals(name)&& userpassword.equals(Password))
						if(rs.getString("LOGGEDON").equals("true")) {
							success=false;
							loggedOn=true;
						}else {
							success = true;
							screenName=rs.getString("SCREENNAME");
						}

				}
			}
			if(!success){
				response = false;
				if(userExists) {
					if(loggedOn) {
						reason="User currently logged in! Please logout and try again";
					}
					else {
						reason="Password is Incorrect!";
					}
				}
				else {
					reason="Username Does Not Exist!";
				}
			}
			else{
				response = true;
				reason = "";
			}

			rs.close();
			return response;

		} catch (SQLException e1) {

			// TODO Auto-generated catch block

			e1.printStackTrace();

		}
		return response;

	}

	public synchronized void changeScreenName(String oldScreenName, String newScreenName){
		try {
			st.executeUpdate("UPDATE CREDENTIALS SET screenname = '"+newScreenName+"' WHERE screenname='"+oldScreenName+"';");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void changePassword(String userName, String password){
		try {
			st.executeUpdate("UPDATE CREDENTIALS SET password = '"+password+"' WHERE username='"+userName+"';");
		}catch(Exception e){
			e.printStackTrace();
		}

	}


	public synchronized void setState(String userName,boolean state){
		try {
			st.executeUpdate("UPDATE CREDENTIALS SET LOGGEDON = '"+state+"' WHERE username='"+userName+"';");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public ArrayList<String> getOnlineUsers(){
		ArrayList<String> online = new ArrayList<String>();
		try{

			ResultSet rs = st.executeQuery("SELECT SCREENNAME FROM CREDENTIALS WHERE LOGGEDON='true';");
			if(rs!=null) {
				while (rs.next()){
					online.add(rs.getString("screenname"));
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return online;
	}

	public synchronized ArrayList<String> getOfflineUsers(){
		ArrayList<String> offline = new ArrayList<String>();
		try{
			ResultSet rs = st.executeQuery("SELECT SCREENNAME FROM CREDENTIALS WHERE LOGGEDON='false';");
			if(rs!=null) {
				while (rs.next()){
					offline.add(rs.getString("screenname"));
				}
			}
		}catch(Exception e){

			e.printStackTrace();

		}

		return offline;
	}

	public String getReason() {
		return reason;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getScreenName(String username) {
		try{
			ResultSet rs = st.executeQuery("SELECT SCREENNAME FROM CREDENTIALS WHERE username='"+username+"';");
			if(rs!=null) {
				while (rs.next()){
					screenName=(rs.getString("screenname"));
				}
			}
		}catch(Exception e){

			e.printStackTrace();

		}

		return screenName;
	}
}