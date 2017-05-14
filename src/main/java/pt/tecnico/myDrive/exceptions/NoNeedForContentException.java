package pt.tecnico.myDrive.exceptions;

public class NoNeedForContentException extends AppMyDriveException {
	
	private static final long serialVersionUID = 201408261552L;
	private String _type;
	
	public NoNeedForContentException(String type){
		_type=type;
	}
	
	public String getType(){
		return _type;
	}
	
	@Override
	public String getMessage() {
		return "The File type '" + _type + "' does not need content.";
	}
}