package model;

public class LoggedInUser {
	
	public static String USER_NAME;
	
	public String getUser_name() {
		return USER_NAME;
	}
	public void setUser_name(String user_name) {
		LoggedInUser.USER_NAME = user_name;
	}

}