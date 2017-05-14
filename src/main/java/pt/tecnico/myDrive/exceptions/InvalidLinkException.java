package pt.tecnico.myDrive.exceptions;

public class InvalidLinkException  extends AppMyDriveException {
	
	private static final long serialVersionUID = -7134769130149788113L;
	private String name;

	public InvalidLinkException(String link) {
		name = link;
	}
	
	public String getLinkName() {
		return name;
	}

	@Override
	public String getMessage() {
		return "The link '" + name + "' is invalid.";
	}
}
