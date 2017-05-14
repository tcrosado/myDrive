package pt.tecnico.myDrive.exceptions;

public class ImportDocumentException extends AppMyDriveException {

    private static final long serialVersionUID = 1L;

    public ImportDocumentException() {
        super("Error in importing person from XML");
    }
}
