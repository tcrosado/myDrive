package pt.tecnico.myDrive.exceptions;

public abstract class AppMyDriveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AppMyDriveException() {
    }

    public AppMyDriveException(String msg) {
        super(msg);
    }
}