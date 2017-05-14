package pt.tecnico.myDrive.domain;

import java.util.Set;

import org.jdom2.Element;
import org.joda.time.DateTime;

import com.mysql.fabric.xmlrpc.base.Array;
import pt.tecnico.myDrive.exceptions.*;

public class Directory extends Directory_Base {
    
    public Directory(String name, Directory parent, Integer id, User user, String mask) throws InvalidNameException{
        super();

        this.setDir(parent);
        parent.addFile(this);
        this.setName(name);
        this.setId(id);
        this.setUser(user);
        user.addFile(this);
        this.setMask(mask);
        DateTime date = new DateTime();
        this.setLastModification(date);
    }
     
    public Directory(AppMyDrive app, Integer id, String mask, String name) {
        super();
        super.setName(name);
        this.setApp(app);
        this.setId(id);
        this.setMask(mask);
        DateTime date = new DateTime();
        this.setLastModification(date);
    }
    
    @Override
    public void setName(String name){
 	   
 	   if(name.contains("/") || name.contains("\0")) throw new InvalidNameException(name);
 		   
 	   if((this.getDir().getPath().length()+name.length())<1024)
 			   super.setName(name);
 	   else{ throw new PathTooLongException(name);}
 	   
    }
        
    
    @Override
    public boolean isDir(){
    	return true;
    }
    
    
    public File getChildbyName(String name){
    	
    	for(File f: this.getFileSet()){
    		if(f.getName().equals(name)){
    			return f;
    		}
    	}
 
    	return null;
    }
    
    
   public Directory createDir(String name, Integer id, User user, String mask) throws FileExistsException{
	   
	   if(this.getChildbyName(name)==null){
		 
		   if((this.getPath()+"/"+name).length()>1024)
			   throw new PathTooLongException(name);
		   
		   Directory dir = new Directory(name, this, id, user, mask);
		   
		   return dir;
	   }
	   else{ throw new FileExistsException(name);}
   }
   
   public Link createLink(Integer id, String name, User owner, String mask, String text) throws FileExistsException{
	   
	   if(this.getChildbyName(name)==null){
		   if((this.getPath()+"/"+name).length()>1024)
			   throw new PathTooLongException(name);
		   Link l = new Link(id, this, name, owner, mask, text);
		   
		   return l;
	   }
	   else{ throw new FileExistsException(name);}
   }
   
   public App createApp(Integer id, String name, User owner, String mask, String text) throws FileExistsException{
	   
	   if(this.getChildbyName(name)==null){
		   if((this.getPath()+"/"+name).length()>1024)
			   throw new PathTooLongException(name);
		   App a = new App(id, this, name, owner, mask, text);
		   
		   return a;
	   }
	   else{ throw new FileExistsException(name);}
   }
   
   public PlainFile createPlainFile(Integer id, String name, User owner, String mask, String text) throws FileExistsException{
	   
	   if(this.getChildbyName(name)==null){
		   if((this.getPath()+"/"+name).length()>1024)
			   throw new PathTooLongException(name);
		   PlainFile p = new PlainFile(id, this, name, owner, mask, text);
		   
		   return p;
	   }
	   else{ throw new FileExistsException(name);}
   }
   
   public void removeChild(File file) throws FileNotFoundException{ 
		
		if(!(this.getChildbyName(file.getName())==null)){
			
			if(file.isDir()){
				
				Directory dir = (Directory) file;
				
				dir.remove();
				
			}else{
				this.getFileSet().remove(file);
			}
			
			
		}else{ throw new FileNotFoundException(file.getName()); }
	}
   
   @Override
   public void remove(){
	   this.setDir(null);
	   this.setUser(null);
	   if(this.getHomeUser() != null){
			this.setHomeUser(null);
		}
	   for(File f : this.getFileSet()){
		   f.remove();
	   }
	   deleteDomainObject();
   }
    
    
    public File[] getChildren(){
    	
    	File[] f = this.getFileSet().toArray(new File[getFileSet().size()]);
    	
    	return f;
    }
    
    public int getNumberOffChildren(){
    	return this.getFileSet().size();
    }
    
    public String[] list(){
    	int size = this.getFileSet().size(); 
    	String[] list = new String[size+2];
    	int iter = 2;
    	File parent = this.getDir();
    	list[0] = "Directory"+" "+this.getMask()+" "+(this.getDir().getNumberOffChildren()+2)+" "+this.getUser().getUsername()+" "+this.getDir().getId()+" "+this.getLastModification()+" "+".";
    	list[1] = "Directory"+" "+parent.getMask()+" "+(parent.getDir().getNumberOffChildren()+2)+" "+parent.getUser().getUsername()+" "+parent.getDir().getId()+" "+parent.getLastModification()+" "+"..";
    	for( File f : this.getFileSet()){
    		list[iter++]=f.toString();
    	}
    	
		return list;
    }
    
    @Override
	public String toString() {
		return "Directory"+" "+this.getMask()+" "+(this.getNumberOffChildren()+2)+" "+this.getUser().getUsername()+" "+this.getId()+" "+this.getLastModification()+" "+this.getName();
	}
    
    @Override
    protected Element xmlExport(){
    	try{
	    	if(this.getId() < 4){
	    		return null;
	    	}
    	}catch (FileNotFoundException e){
    		
    	}
    	Element newFile = new Element("dir");
    	newFile = super.xmlExport(newFile);

    	return newFile;  
    }
	
}
