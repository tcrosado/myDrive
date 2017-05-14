package pt.tecnico.myDrive.exceptions;

public class UserNotFoundException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;

	
	private final String username;

	public UserNotFoundException(String user) {
		username = user;
	}
	
	public String getUserName(){
		return username;
	}
	
	@Override
	public String getMessage() {
		return "The user '" + username + "' not found.";
	}
}