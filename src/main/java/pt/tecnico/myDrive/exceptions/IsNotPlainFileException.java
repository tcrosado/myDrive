package pt.tecnico.myDrive.exceptions;

public class IsNotPlainFileException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;

	private String plainFileName;

	public IsNotPlainFileException(String file) {
		plainFileName = file;
	}
	
	public String getPlainFile() {
		return plainFileName;
	}

	@Override
	@SuppressWarnings("nls")
	public String getMessage() {
		return "The file '" + plainFileName + "' is not a PlainFile.";
	}
}