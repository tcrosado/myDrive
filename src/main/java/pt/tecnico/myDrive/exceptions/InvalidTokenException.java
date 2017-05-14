package pt.tecnico.myDrive.exceptions;

public class InvalidTokenException extends AppMyDriveException {
	
	
	private static final long serialVersionUID = -1717929906014357896L;
	
	private long _token;

	public InvalidTokenException(Long token) {
		_token=token;
	}
	
	public Long getToken() {
		return _token;
	}

	@Override
	public String getMessage() {
		return "The token '" + _token + "' is invalid.";
	}
}
