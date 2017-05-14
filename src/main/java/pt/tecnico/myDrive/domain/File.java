package pt.tecnico.myDrive.domain;


import java.lang.Comparable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.tecnico.myDrive.exceptions.InvalidNameException;
import pt.tecnico.myDrive.exceptions.PathTooLongException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.joda.time.DateTime;

public abstract class File extends File_Base implements Comparable<Object>{
	

   	static final Logger logger = LoggerFactory.getLogger(File.class);
	
    public File(Integer id, Directory parent, String name, User owner, String mask)  throws InvalidNameException{
    	super();
	    	
        DateTime date = new DateTime();
        this.setLastModification(date);
        this.setDir(parent);
        parent.addFile(this);
        this.setName(name);
        this.setUser(owner);
        this.setMask(mask);
        this.setId(id);
    }
    
    public File() {
    		super();
    }
    
    public boolean isDir(){
    		return false;
    }
    

    public boolean canRead(User u){
    	String perm = this.getMask();
    	boolean owner = false;
    	boolean root = false;
    	
    	if(this.equals(u.getHomeDir())){
    		owner = true;
    	}
    	
    	if(u.isRoot()){		root = true;}
    	else if(u.equals(this.getUser())){		owner = true;}
    	
    	if((owner&&perm.startsWith("r"))
    		||(!owner&&perm.startsWith("r",4))
    		||root){
    		
    		return true;
    	}    	
    	
    	return false;
    }
    
    public boolean canWrite(User u){
	String perm = this.getMask();
	boolean owner = false;
	boolean root = false;
	
	if(this.equals(u.getHomeDir())){
		owner = true;
	}
	
	if(u.isGuest()){	return false;}
	
	if(u.isRoot()){		root = true;}
	else if(u.equals(this.getUser())){		owner = true;}
	
	if((owner&&perm.startsWith("w",1))
		||(!owner&&perm.startsWith("w",5))
		||root){
		
		return true;
	}    	
	
	return false;
    }
    
    public boolean canExecute(User u){
    	String perm = this.getMask();
    	boolean owner = false;
    	boolean root = false;
    	
    	if(this.equals(u.getHomeDir())){
    		owner = true;
    	}
    	
    	if(u.isRoot()){		root = true;}
    	else if(u.equals(this.getUser())){		owner = true;}
    	
    	if((owner&&perm.startsWith("x",2))
    		||(!owner&&perm.startsWith("x",6))
    		||root){
    		
    		return true;
    	}    	
    	
    	return false;
    }
    
    public boolean canDelete(User u){
    	String perm = this.getMask();
    	boolean owner = false;
    	boolean root = false;
    	
    	if(this.equals(u.getHomeDir())){
    		owner = true;
    	}
    	
    	if(u.isGuest()){	return false;}
    	
    	if(u.isRoot()){		root = true;}
    	else if(u.equals(this.getUser())){		owner = true;}
    	
    	if((owner&&perm.startsWith("d",3))
    		||(!owner&&perm.startsWith("d",7))
    		||root){
    		
    		return true;
    	}    	
    	
    	return false;
    }
    

    
    protected Element xmlExport(Element newFile){
    	Attribute id = new Attribute("id",""+this.getId());
    	newFile.setAttribute(id);
    	
    	Element path = new Element("path");
    	path.addContent(this.getDir().getPath());
    	newFile.addContent(path);
    	
    	Element name = new Element("name");
    	name.addContent(this.getName());
    	newFile.addContent(name);
    	
    	Element owner = new Element("owner");
    	owner.addContent(this.getUser().getUsername());
    	newFile.addContent(owner);
    	
    	Element permissions = new Element("perm");
    	permissions.addContent(this.getMask());
    	newFile.addContent(permissions);
    	
    	return newFile;
    }
	
	protected abstract Element xmlExport();
    
	public String getPath(){
		
		if(getName().equals("/"))
			return getName();
		
		File file= this.getDir();
		String path = this.getName();

		while(!file.getName().equals("/")){
			path=file.getName()+"/"+path;
			file=file.getDir();
			
		}
		return path=file.getName()+path;			
	}
	
	
	/*Used on TreeSet comparison when exporting XML*/
	public int compareTo(Object o) throws ClassCastException{
		if(!(o instanceof File)){
			throw new ClassCastException("Expected File object");
		}
		int otherFileId = ((File) o).getId();
		
		return (this.getId() - otherFileId); 
	}
	
	public void remove() {
		this.setDir(null);
		this.setUser(null);
		deleteDomainObject();
	}	
	
}
