package pt.tecnico.myDrive.exceptions;


public class InvalidOperationException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;
	
	private String _message;
	
	public InvalidOperationException(String msg){ _message = msg;}
	
	@Override
	public String getMessage() {
		return "Invalid operation in AppMyDrive: " + _message + ".";
	}


}
