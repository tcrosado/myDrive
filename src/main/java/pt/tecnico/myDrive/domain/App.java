package pt.tecnico.myDrive.domain;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exceptions.InvalidNameException;

public class App extends App_Base {
    
    public App(Integer id, Directory parent, String name, User owner, String mask, String text) throws InvalidNameException{
        super();
        
        if(!name.contains("/") || !name.contains("\0")){
	        this.setText(text);
	        DateTime date = new DateTime();
	        this.setLastModification(date);
	        this.setDir(parent);
	        parent.addFile(this);
	        this.setName(name);
	        this.setUser(owner);
	        this.setMask(mask);
	        this.setId(id);
        }
        else{ throw new InvalidNameException(name); }
    }
    
    
    public void execute(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException { 
		String name=this.getText();
		Class<?> cls;
        Method meth;
        try { // name is a class: call main()
          cls = Class.forName(name);
          meth = cls.getMethod("main", String[].class);
        } catch (ClassNotFoundException cnfe) { // name is a method
          int pos;
          if ((pos = name.lastIndexOf('.')) < 0) throw cnfe;
          cls = Class.forName(name.substring(0, pos));
          meth = cls.getMethod(name.substring(pos+1), String[].class);
        }
        meth.invoke(null, (Object)args); // static method (ignore return)
	}
    
    
    @Override
    public String read(){
    	return this.getText();
    }
    
    @Override
    public void write(String newText){
	    DateTime date = new DateTime();
	    this.setLastModification(date);
	    this.setText(newText);
    }
    
    @Override
    protected Element xmlExport(){
    	
     	Element newFile = new Element("app");
    	newFile = super.xmlExport(newFile);
    	
    	Element method = new Element("method");
    	method.addContent(this.getText());
    	newFile.addContent(method);
    	
    	return newFile;
    }
    
    @Override
	public String toString() {
		return "App"+" "+this.getMask()+" "+this.getUser().getUsername()+" "+this.getId()+" "+this.getLastModification()+" "+this.getName();
	}
}
