package pt.tecnico.myDrive.exceptions;

public class PathTooLongException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;
	
	private String fileName;
	
	public PathTooLongException() {
		fileName = "";
	}
	
	public PathTooLongException(String name) {
		fileName = name;
	}
	
	public String getFileName() {
		return fileName;

	}
	@Override
	public String getMessage() {
		return "The file path '" + fileName + "' is over 1024 characters.";
	}
}