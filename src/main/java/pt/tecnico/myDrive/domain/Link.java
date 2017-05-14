package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.InvalidLinkException;
import pt.tecnico.myDrive.exceptions.InvalidNameException;
import pt.tecnico.myDrive.exceptions.IsNotPlainFileException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

public class Link extends Link_Base {
    
    public Link(Integer id, Directory parent, String name, User owner, String mask, String text) throws InvalidNameException{
        super();
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
    
    @Override
    public void setText(String text){
    	
    	if(getText()==null){
	    	if(text == null){
	        	throw new InvalidLinkException(text);
	    	}
	    	else if(!haveLoop(text)){
	    		super.setText(text);
	    	}
	    	else{
	        	throw new InvalidLinkException(text);
	        }
    	}
    	else{
        	throw new InvalidLinkException(text);
        }
    }
    
    public boolean haveLoop(String filePath){
    	AppMyDrive app = AppMyDrive.getInstance();
    	File file = app.getFileByPath(filePath);
    	if (file instanceof Link){
    		List<File>links = new ArrayList<File>();
    		links.add(file);
    		return haveLoopAux(((Link) file).getText(), links);
    	}
    	return false;
    }
    
    public boolean haveLoopAux(String filePath, List<File> links){
    	AppMyDrive app = AppMyDrive.getInstance();
    	File file = app.getFileByPath(filePath);
    	if (file instanceof Link){
    		if(links.contains(file))
    			return true;
    		links.add(file);
    		return haveLoopAux(((Link) file).getText(), links);
    	}
    	return false;
    }
    
    public File getFinalFile(String filePath){
    	AppMyDrive app = AppMyDrive.getInstance();
    	File file = app.getFileByPath(filePath);
    	if (file instanceof Link){
    		return getFinalFile(((Link) file).getText());
    	}
    	return file;
    }
    
    @Override
    public String read(){
    	File f = this.getFinalFile(this.getText());
    	if(f instanceof Directory){
    		throw new IsNotPlainFileException(f.getName());
    	}
		else if(f instanceof App){
			return ((App) f).read();
		}
		else if(f instanceof PlainFile){
			return ((PlainFile)f).read();
		}
    	return null;
    	
    }
    
    @Override
    public void write(String text){
    	File f = this.getFinalFile(this.getText());
    	if(f instanceof Directory){
    		throw new IsNotPlainFileException(f.getName());
    	}
		else if(f instanceof App){
			((App) f).write(text);
			return;
		}
		else if(f instanceof PlainFile){
			((PlainFile)f).write(text);
			return;
		}
    }
    
    
    public void execute(String[]args, User u) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{ 
    	File f = this.getFinalFile(this.getText());
    	if(f instanceof Directory){
    		throw new IsNotPlainFileException(f.getName());
    	}
		else if(f instanceof App){
			if(((App) f).canExecute(u)){
				((App) f).execute(args);
				return;
			}else{ 
				throw new AccessDeniedException(u.getUsername()); 
			}
		}
		else if(f instanceof PlainFile){
			if(((PlainFile) f).canExecute(u)){
				((PlainFile) f).execute(u);
				return;
			}else{ 
				throw new AccessDeniedException(u.getUsername()); 
			}
		}
		else{ 
			throw new IsNotPlainFileException(f.getName());
		}
    }
    
    @Override
    protected Element xmlExport(){
    	
    	Element newFile = new Element("link");
    	newFile = super.xmlExport(newFile);
    	
    	Element value = new Element("value");
    	value.addContent(this.getText());
    	newFile.addContent(value);
    	
    	return newFile;    	
    }
    
    @Override
    public String toString(){
    	String s=this.getName()+" -> " +this.getText();
    	return s;
    }
    
    
}
