package pt.tecnico.myDrive.exceptions;

public class InvalidNameException extends AppMyDriveException {


	private static final long serialVersionUID = -6013436582336162610L;
	
	private String name;

	public InvalidNameException(String file) {
		name = file;
	}
	
	public String getFileName() {
		return name;
	}

	@Override
	public String getMessage() {
		return "The Name '" + name + "' is invalid.";
	}
}
