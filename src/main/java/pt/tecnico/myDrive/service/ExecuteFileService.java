package pt.tecnico.myDrive.service;

import java.lang.reflect.InvocationTargetException;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.IsNotPlainFileException;

public class ExecuteFileService extends MyDriveService{
	
	long _token;
	boolean _absolute;
	String _path;
	String _fileName;
	private String[] _args;
	
	public ExecuteFileService(long token, String path, String[]args){
		_args =args;
		_token=token;
		_path=path;
		
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
	protected void dispatch() throws AppMyDriveException {
		AppMyDrive app = getMyDrive();
		Login session = app.getSessionByToken(_token);
		
		String originalPath  = session.getPath();
		
		if(_absolute)
			session.setPath(_path);
		else if(!_path.equals(""))
			session.setPath(session.getPath()+"/"+_path);
	
		User currentUser = app.getUserByUsername(session.getUsername());
		if(session.canAccess()){
			File file = app.getFileByPath(session.getPath()+"/"+_fileName);
			if(file == null){
    			throw new FileNotFoundException(_fileName);
    		}
			if(file.isDir()){
    			throw new IsNotPlainFileException(_fileName);
    		}
			else if(file instanceof Link){
				if( ((Link) file).canExecute(currentUser)){
					try {
						((Link) file).execute(_args, currentUser);
					} catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
							| IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}else{ 
	    			throw new AccessDeniedException(session.getUsername()); 
	    		}  
				
    		}
			else if(file instanceof App ){
				if( ((App) file).canExecute(currentUser)){
					try {
						((App) file).execute(_args);
					} catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
							| IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}else{ 
	    			throw new AccessDeniedException(session.getUsername()); 
	    		}  
			}
			else if(file instanceof PlainFile){
				if( ((PlainFile) file).canExecute(currentUser)){
					try {
						((PlainFile) file).execute(currentUser);
					} catch (ClassNotFoundException | SecurityException | NoSuchMethodException | IllegalArgumentException
							| IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}else{ 
	    			throw new AccessDeniedException(session.getUsername()); 
	    		}  
			}
			else{
				
			}
		}
		else{
			throw new InvalidTokenException(_token);
		}
		session.setPath(originalPath);
	}
	
	public String result(){
		return "";
	}
}
