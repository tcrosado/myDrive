package pt.tecnico.myDrive.domain;

public class Variable extends Variable_Base {
    
    public Variable(Login session, String name, String value) {
        super();
        super.setName(name);
        super.setValue(value);
        super.setSession(session);
    }
    
    
}
