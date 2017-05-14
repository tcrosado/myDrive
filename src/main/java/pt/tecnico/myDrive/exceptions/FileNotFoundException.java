package pt.tecnico.myDrive.exceptions;

public class FileNotFoundException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;

	private String fileName;

	public FileNotFoundException(String file) {
		fileName = file;
	}
	
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMessage() {
		return "The File '" + fileName + "' does not exists.";
	}
}
