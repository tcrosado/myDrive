package pt.tecnico.myDrive.domain;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.InvalidNameException;
import pt.tecnico.myDrive.exceptions.IsNotAppException;
import pt.tecnico.myDrive.exceptions.PathTooLongException;

import org.jdom2.Element;

public class PlainFile extends PlainFile_Base {
    
    public PlainFile(Integer id, Directory parent, String name, User owner, String mask, String text) throws InvalidNameException{
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
    
    public PlainFile(String text){
    	super();
    	this.setText(text);
    }
    
    public PlainFile(){
    	super();
    }
    
    @Override
    public void setName(String name){
 	   
 	   if(name.contains("/") || name.contains("\0")) throw new InvalidNameException(name);
 		   
 	   if((this.getDir().getPath().length()+name.length())<1024)
 			   super.setName(name);
 	   else{ throw new PathTooLongException(name);}
 	   
    }
    
    public void write(String newText){
	    DateTime date = new DateTime();
	    this.setLastModification(date);
	    this.setText(newText);
    }
        
    public String read(){
    	return this.getText();    	
    }
    
	public void execute(User u) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{ 
    	String[] lines = this.getText().split("\n");
    	String[] app;
    	File f;
    	for(String s: lines){
    		app=s.split(" ");
    		if(app.length > 0){
        		f=AppMyDrive.getInstance().getFileByPath(app[0]);
	    		if (f instanceof App){
	    			if(((App) f).canExecute(u)){
	    				((App) f).execute(Arrays.copyOfRange(app, 1, app.length));
	    			}else{ 
		    			throw new AccessDeniedException(u.getUsername());
					}
				}
	    		else if (f instanceof Link){
	    			if(((Link) f).canExecute(u)){
	    			
						File fapp = ((Link) f).getFinalFile(((Link) f).getText());
						if (fapp instanceof App){
							if (((App) fapp).canExecute(u)){
								((App) fapp).execute(Arrays.copyOfRange(app, 1, app.length));
							}else{ 
				    			throw new AccessDeniedException(u.getUsername());
							}
						}
						else{ 
			    			throw new IsNotAppException(u.getUsername());
						}
	    			}else{ 
		    			throw new AccessDeniedException(u.getUsername()); 
		    		}
	    		}
	    		else{ 
	    			throw new IsNotAppException(u.getUsername());
	    		}
    		}
    	}
    } 
    
    @Override
	public String toString() {
		return "PlainFile"+" "+this.getMask()+" "+this.getUser().getUsername()+" "+this.getId()+" "+this.getLastModification()+" "+this.getName();
	}
    
    @Override
    protected Element xmlExport(){
    	
    	Element newFile = new Element("plain");
    	newFile = super.xmlExport(newFile);
    	
    	Element contents = new Element("contents");
    	contents.addContent(this.getText());
    	newFile.addContent(contents);
    	
    	return newFile;
    }
}
