package pt.tecnico.myDrive.exceptions;

public class ExportDocumentException extends AppMyDriveException {

    private static final long serialVersionUID = 1L;

    public ExportDocumentException() {
        super("Error in importing person from XML");
    }
}