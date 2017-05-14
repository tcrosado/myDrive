package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exceptions.InvalidOperationException;

public class Association extends Association_Base {
    
    public Association(String extension,App app){
    	super();
    	super.setExtension(extension);
    	super.setApp(app);
    }
    
    @Override
    public void setExtension(String extension){
    	throw new InvalidOperationException("Cannot change Extension in Association entity");
    }
}
