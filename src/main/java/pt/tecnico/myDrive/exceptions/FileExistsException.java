package pt.tecnico.myDrive.exceptions;

public class FileExistsException extends AppMyDriveException {

	private static final long serialVersionUID = 1L;
	
	private String fileName;

	
	public FileExistsException(String file) {
		fileName = file;
	}
	
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMessage() {
		return "The '" + fileName + "' already exist.";
	}
}
