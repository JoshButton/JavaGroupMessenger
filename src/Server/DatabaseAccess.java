package Server;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseAccess {
	static DataBase d;
	public DatabaseAccess() {
		d = new DataBase();
	}

	public static ArrayList<String> getOnlineUsers(){
		return d.getOnlineUsers();
	}

	public static ArrayList<String> getOfflineUsers(){
		return d.getOfflineUsers();
	}

	public static synchronized  boolean registerUser(String userName, String Password, String screenName) throws SQLException  {
		return d.registerUser(userName, Password, screenName);
	}

	public static synchronized boolean checkLogin(String userName, String password){
		return d.checkLogin(userName, password);
	}

	public static synchronized void setState(String userName,boolean state){
		d.setState(userName, state);
	}


	public static String getReason() {
		return d.getReason();
	}

	public static String getScreenName() {
		return d.getScreenName();
	}

	public static String getScreenName(String username) {
		return d.getScreenName(username);
	}

	public synchronized static void changePassword(String userName, String password) {
		d.changePassword(userName, password);
	}

	public synchronized static void changeScreenName(String oldScreenName, String newScreenName){
		d.changeScreenName(oldScreenName, newScreenName);
	}
}
