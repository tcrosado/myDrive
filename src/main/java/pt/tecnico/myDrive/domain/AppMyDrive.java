package pt.tecnico.myDrive.domain;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;



import org.jdom2.Element;
import org.jdom2.Attribute;

import pt.ist.fenixframework.*;
import pt.tecnico.myDrive.exceptions.*;

public class AppMyDrive extends AppMyDrive_Base {
	
	private static final Logger logger = LoggerFactory.getLogger(AppMyDrive.class);
   
    
    private AppMyDrive() {
        setRoot(FenixFramework.getDomainRoot());
        this.setIdFile(0);
        
        Directory rootDir = new Directory(this, nextID(),"rwxdr-x-", "/");
        rootDir.setDir(rootDir);
        this.setDir(rootDir);
        
        Directory home = new Directory(null,nextID(),"rwxdr-x-", "home");
        home.setDir(rootDir);
        rootDir.addFile(home);
        
        RootUser rootUser = new RootUser(this);
        this.addUser(rootUser);
        
        rootDir.setUser(rootUser);
	    home.setUser(rootUser);
        
        rootUser.addFile(rootDir);
        rootUser.addFile(home);
        
        new GuestUser(this);

    }
    
	public Directory createDirByPath(String path) throws PathTooLongException{
		if(path.length()>1024)
    		throw new PathTooLongException();
		Directory currentDir = this.getDir();
		Directory aux;
		User root = getUserByUsername("root");

		String prefix = "/";
		String noPrefixStr = path.substring(path.indexOf(prefix) + prefix.length());
		String[] tokens = noPrefixStr.split("/");
	
		for (String t : tokens){
			aux = (Directory) currentDir.getChildbyName(t);
			if(aux==null){
				aux = currentDir.createDir(t, nextID(),root,"rwxdr-x-");
			}
			currentDir = aux;
		}
    		return currentDir;
	}
    
    public File getFileByPath(String path) throws FileNotFoundException,PathTooLongException{
    	if(path.length()>1024)
    		throw new PathTooLongException();
		Directory currentDir = this.getDir();
		File aux = currentDir;	
		
		String prefix = "/";
		String noPrefixStr = path.substring(path.indexOf(prefix) + prefix.length());
		String[] tokens = noPrefixStr.split("/");
	

		if(path.equals("/"))
			return currentDir;
	
		
		for (String t : tokens){
			if(!(aux instanceof Directory) && !(aux instanceof PlainFile) && !(aux instanceof App) && !(aux instanceof Link) && !(aux instanceof File)){
				throw new FileNotFoundException(t);
			}
			aux = currentDir.getChildbyName(t);
			if (aux == null){
				throw new FileNotFoundException(t);
			}
			else if(aux instanceof Directory){
				currentDir = (Directory) aux;
			}
		}
		return (File) aux;
    }

    public int nextID(){
    		int id = this.getIdFile();
    		id += 1;
    		this.setIdFile(id);
    		return id;
    }
    
	public User getUserByUsername(String username){
		for( User u : getUserSet()){
			if(u.getUsername().equals(username)){
				return u;
			}
		}
		return null;
	}
    
    public static AppMyDrive getInstance(){
        AppMyDrive app = FenixFramework.getDomainRoot().getMyDrive();
        if (app != null)
        	return app;

        logger.trace("new AppMyDrive");
        return new AppMyDrive();
    }
    
    

    public void xmlImport(Element element){
    	for(Element node : element.getChildren("user")){
    		String username = new String(node.getAttributeValue("username"));
    		String home = node.getChildText("home");
    		if(home == null){
    			home = "/home/"+username;
    		}
    		try{
    			User newUser = new User(this, username, null, null, null, home);
    			newUser.xmlImport(node);
    		}catch (PathTooLongException e){
    			logger.trace("Path is too long");
    		}catch (UserExistsException e){
    			logger.trace("User already exists");
    		}catch (FileExistsException e){
    			logger.trace("File already exists");
    		}
    	}
    	for(Element node : element.getChildren()){
    		if(node.getName().equals("dir")||node.getName().equals("plain")||
    				node.getName().equals("app")||node.getName().equals("link"))
    		{
	    		//int id = Integer.parseInt(node.getAttributeValue("id"));
	    		String path = new String(node.getChildText("path"));
	    		String name = new String(node.getChildText("name"));
	    		String owner = new String(node.getChildText("owner"));
	    		String perm = new String(node.getChildText("perm"));
	    		Directory dirToFile;
				try{
					dirToFile = (Directory) this.getFileByPath(path);
				}catch (FileNotFoundException e){
					dirToFile = (Directory) this.createDirByPath(path);
				}
				
				try{
		    		if(node.getName().equals("dir")){
		    			dirToFile.createDir(name, nextID(), getUserByUsername(owner), perm);    			
		    		}
		    		else if(node.getName().equals("plain")){
		    			dirToFile.createPlainFile(nextID(), name, getUserByUsername(owner), perm, node.getChildText("contents"));
		    		}
		    		else if(node.getName().equals("link")){
		    			dirToFile.createLink(nextID(), name, getUserByUsername(owner), perm, node.getChildText("value"));
		    		}
		    		else if(node.getName().equals("app")){
		    			dirToFile.createApp(nextID(), name, getUserByUsername(owner), perm, node.getChildText("method"));
		    		}
				}catch (AppMyDriveException e){
					throw new ImportDocumentException();
				}
	    	}
    	}
    }

	public Document xmlExport(){
		
		logger.info("Initializing XML Export");
    	TreeSet<File> fileList = new TreeSet<File>();
    	Document newDocument = new Document();
    
    	Element rootTag = new Element("myDrive");
    	newDocument.setRootElement(rootTag);
    	Element userExport;
    	for(User u: this.getUserSet()){
    		userExport = u.xmlExport();
    		if(userExport!=null)
    			rootTag.addContent(userExport);
    		
    		if(u.getFileSet()==null)
    			continue;
    		
    		fileList.addAll(u.getFileSet());
    	}
    	logger.info("Users exported to XML");
    	logger.info("Exporting Files to XML");
    	for(File f: fileList){
    		Element xmlElement = f.xmlExport();
    		if(xmlElement == null)
    			continue;
    		rootTag.addContent(xmlElement);
    	}
    	logger.info("XML Export finished");
    	return newDocument;
    }

	public void cleanup() {
		for(User u: getUserSet()){
			u.remove();
		}		
	}

	public Login getSessionByToken(long token) throws InvalidTokenException {
		for(Login s: this.getSessionSet()){
			if(s.getToken()==token){
				return s;
			}
		}
		throw new InvalidTokenException(token);		
	}

       
}
