package pt.tecnico.myDrive.exceptions;

public class AccessDeniedException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;
	
	private String username;

	public AccessDeniedException(String name) {
		username = name;
	}
	
	public String getUserName() {
		return username;

	}
	@Override
	public String getMessage() {
		return "The user '" + username + "' dont have premisson to do that operation.";
	}
}