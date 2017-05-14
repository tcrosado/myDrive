package pt.tecnico.myDrive.exceptions;

public class UserExistsException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;

	private String username;

	public UserExistsException(String user) {
		username = user;
	}
	
	public String getUserName(){
		return username;
	}

	@Override
	public String getMessage() {
		return "The user '" + username + "' already exist.";
	}
}
