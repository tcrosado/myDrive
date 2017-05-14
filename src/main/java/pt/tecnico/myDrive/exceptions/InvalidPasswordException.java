package pt.tecnico.myDrive.exceptions;

public class InvalidPasswordException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;

	private String username;

	public InvalidPasswordException(String user) {
		username = user;
	}
	
	public String getUserName(){
		return username;
	}

	@Override
	public String getMessage() {
		return "The '" + username + "'s password does not match.";
	}
}
