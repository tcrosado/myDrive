package pt.tecnico.myDrive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.log.Log;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;

public class DeleteFileService extends MyDriveService{
	
	private static final Logger _logger = LoggerFactory.getLogger(AppMyDrive.class);
	
	long _token;
	String _fileName;
	
	public DeleteFileService(long token, String fileName){
		_token = token;
		_fileName = fileName;
	}
	
	@Override
	public final void dispatch() throws FileNotFoundException, AccessDeniedException{
		
		AppMyDrive app = getMyDrive();
		Login session = app.getSessionByToken(_token);

		User currentUser = app.getUserByUsername(session.getUsername());
		    		
		
		if(session.canAccess()){
    		
			String path= session.getPath();
    		Directory currentDir = (Directory) app.getFileByPath(path);
    		File file = currentDir.getChildbyName(_fileName);
    		
    		if(file == null){
    			throw new FileNotFoundException(_fileName);
    		}
    		
    		else if(file.isDir()){
    			
    			if(file.canDelete(currentUser))
    				currentDir.removeChild(file);
    			else{
    				throw new AccessDeniedException(session.getUsername());
    			}
    		}
    		
    		else{
    			
    			if(file.canDelete(currentUser)){
    				file.remove();
    			}
    			
    			else{
    				throw new AccessDeniedException(session.getUsername());
    			}
    		}
    	}
		
		else{
			throw new InvalidTokenException(_token);
		}
	}
	
}
