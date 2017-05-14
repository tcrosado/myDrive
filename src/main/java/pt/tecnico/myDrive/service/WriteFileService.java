package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.IsNotPlainFileException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;


public class WriteFileService extends MyDriveService {

	long _token;
	String _fileName;
	boolean _absolute;
	String _path;
	String _content;
	
    public WriteFileService(long token, String path, String content) {
    		this._token = token;    		
    		this._content = content;
    		
    		//Resolve path
    		if(path.startsWith("/")){
    			_absolute = true;
    			int last = path.lastIndexOf("/");
    			_path = path.substring(0, last);
    			_fileName = path.substring(last+1);
    		}
    		else if(path.contains("/")){
    			_absolute = false;
    			int last = path.lastIndexOf("/");
    			_path = path.substring(0, last);
    			_fileName = path.substring(last+1);
    		}
    		else{
    			_absolute = false;
    			_path = "";
    			_fileName = path;
    		}
    }

    @Override
    public final void dispatch() throws FileNotFoundException, IsNotPlainFileException, AccessDeniedException {
    	AppMyDrive app = getMyDrive();
		Login session = app.getSessionByToken(_token);

		String originalPath  = session.getPath();
		
		if(_absolute)
			session.setPath(_path);
		else if(!_path.equals(""))
			session.setPath(session.getPath()+"/"+_path);

		User currentUser = app.getUserByUsername(session.getUsername());
		boolean link = false;
		File fileLinked = null;
		
		if(session.canAccess()){
	    		String path= session.getPath();
	    		Directory currentDir = (Directory) app.getFileByPath(path);
	    		File file = currentDir.getChildbyName(_fileName);
	    		
	    		if(file == null){
	    			throw new FileNotFoundException(_fileName);
	    		}
	    		if(file.isDir()){
	    			throw new IsNotPlainFileException(_fileName);
	    		}
	    		else if(file instanceof Link){
	    			fileLinked = ((Link) file).getFinalFile(((Link) file).getText());
	    			if(fileLinked.isDir()){
	    				throw new IsNotPlainFileException(_fileName);
	    			}else{
	    				link = true;
	    			}
	    		}
    		
	    		if(file.canWrite(currentUser)){
	    			if(link){ 
	    				if(fileLinked.canRead(currentUser)){ file = fileLinked;}
	    				else{ throw new AccessDeniedException(session.getUsername());}
	    			}
	    			PlainFile pf = (PlainFile) file;
	    			pf.write(_content);
	    		}
	    		else{
	    			throw new AccessDeniedException(session.getUsername());
	    		}
		}
		else{
			throw new InvalidTokenException(_token);
		}
		session.setPath(originalPath);
    }
}
