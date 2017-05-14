package pt.tecnico.myDrive.domain;


import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.tecnico.myDrive.exceptions.InvalidNameException;
import pt.tecnico.myDrive.exceptions.InvalidOperationException;
import pt.tecnico.myDrive.exceptions.InvalidPasswordException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class User extends User_Base {
	
	//private static final Logger logger = LoggerFactory.getLogger(AppMyDrive.class);
	

	public User(){/* throw new InvalidOperationException("Invalid Constructor of User");*/}
	
	public User(AppMyDrive app, String username, String name, String password, String mask, String homeDir){	
			
		this.setApp(app);
		this.setUsername(username);
		this.setName(name);
		this.setPassword(password);
		this.setMask(mask);
		Directory dir = app.createDirByPath(homeDir);
		this.setHomeDir(dir);
		dir.setHomeUser(this);	
	}
	
	@Override
	public void setUsername(String username){
		if(username.length() >= 3 && username!=null){
			super.setUsername(username);
		}
		else{ throw new InvalidNameException(username);}
	}
	
	@Override
	public void setPassword(String pass) throws InvalidPasswordException{
		
		if(this.getUsername().equals("root") || this.getUsername().equals("nobody"))
			super.setPassword(pass);
		else if(pass.length()<8) throw new InvalidPasswordException(this.getUsername());
		super.setPassword(pass);
	}
	
	@Override
	public void setApp(AppMyDrive app){
		if(app == null){
			remove();
		}else{
			app.addUser(this);
		}
	}
	
	public boolean isRoot(){
		return false;
	}

	public boolean isGuest(){
		return false;
	}
	
	public void xmlImport(Element userElement){
    	List<Element> info = userElement.getChildren();
    	
    	for(Element tag : info ){
    		if(tag.getName().equals("name")){
    			this.setName(new String(tag.getText()));
    		}
    		else if(this.getPassword()==null){
    			this.setPassword(this.getUsername());
    		}
    		if(tag.getName().equals("password")){
    			this.setPassword(new String(tag.getText()));
    		}
    		else if(this.getName()==null){
    			this.setName(this.getUsername());
    		}
    		if(tag.getName().equals("mask")){
    			this.setMask(new String(tag.getText()));
    		}
    		else if(this.getMask()==null){
    			this.setMask("rwxd----");
    		}    		
    	}
    }
    
    protected Element xmlExport(){
    	
    	if(this.getUsername().equals("root")){
    		return null;
    	}
    	Element newUser = new Element("user");
    	Attribute username = new Attribute("username",this.getUsername());
    	newUser.setAttribute(username);
    	
    	Element password = new Element("password");
    	password.addContent(this.getPassword());
    	newUser.addContent(password);
    	
    	Element name = new Element("name");
    	name.addContent(this.getName());
    	newUser.addContent(name);
    	
    	Element home = new Element("home");
    	home.addContent(this.getHomeDir().getPath());
    	newUser.addContent(home);
    	
    	Element mask = new Element("mask");
    	mask.addContent(this.getMask());
    	newUser.addContent(mask);  	  	
    	
    	return newUser;
    }

	public void remove() {
		setHomeDir(null);
		for(File file: getFileSet()){
			file.remove();
		}
		super.setApp(null);
		deleteDomainObject();
	}
	
	public void addFile(File f) {
		getFileSet().add(f);
	}
    
}
