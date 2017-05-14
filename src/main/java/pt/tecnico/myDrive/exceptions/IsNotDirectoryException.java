package pt.tecnico.myDrive.exceptions;

public class IsNotDirectoryException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;

	private String fileName;

	public IsNotDirectoryException(String file) {
		fileName = file;
	}
	
	public String getFileName(){
		return fileName;
	}

	@Override
	public String getMessage() {
		return "The file '" + fileName + "' is not a directory.";
	}
}