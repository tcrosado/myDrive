package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.IsNotDirectoryException;
import pt.tecnico.myDrive.service.dto.FileDto;


public class ListDirectoryService  extends MyDriveService{
	
	private static final Logger _logger = LoggerFactory.getLogger(AppMyDrive.class);
	
	long _token;
	String _path;
    private List<FileDto> listed;

	
	public ListDirectoryService(long token, String path){
		_token = token;
		_path=path;
		listed = new ArrayList<FileDto>();
	}
	
	@Override
	protected void dispatch() throws IsNotDirectoryException, AccessDeniedException{
		
		AppMyDrive app = getMyDrive();
		Login session = app.getSessionByToken(_token);
		User currentUser = app.getUserByUsername(session.getUsername());
		Directory currentDir;
		if(_path.equals(".")){
			String curDir = session.getPath();
			currentDir = (Directory)app.getFileByPath(curDir);
		}
		else{
			currentDir = (Directory)app.getFileByPath(_path);
		}
			
		String fileName = currentDir.getName();
		
		if(session.canAccess()){
			if(!currentDir.isDir()){
    			throw new IsNotDirectoryException(fileName);
    		}
			
			if (!(currentDir.canRead(currentUser)))
				throw new AccessDeniedException(currentUser.getUsername());
			
			listed.add(new FileDto( "Dir", currentDir.getMask() , ""+currentDir.getFileSet().size(), currentDir.getUser().getName(), currentDir.getId().toString(), currentDir.getLastModification().toString(), "."));
			listed.add(new FileDto( "Dir", currentDir.getDir().getMask(), ""+currentDir.getDir().getFileSet().size(), currentDir.getDir().getUser().getName(), currentDir.getDir().getId().toString(), currentDir.getDir().getLastModification().toString(), ".."));
			
			for (File f : currentDir.getFileSet()) {
				if(f instanceof Directory){
					listed.add(new FileDto( "Dir", f.getMask(), ""+((Directory) f).getFileSet().size(), f.getUser().getName(), f.getId().toString(), f.getLastModification().toString(), f.getName()));
				}
				else if(f instanceof Link){ 
					listed.add(new FileDto( "Link", f.getMask(),"0", f.getUser().getName(), f.getId().toString(), f.getLastModification().toString(), f.getName()));
				}
				else if(f instanceof App){
					listed.add(new FileDto( "App", f.getMask(), "0", f.getUser().getName(), f.getId().toString(), f.getLastModification().toString(), f.getName()));
				}
				else if(f instanceof PlainFile){
					listed.add(new FileDto( "Plain", f.getMask(), "0", f.getUser().getName(), f.getId().toString(), f.getLastModification().toString(), f.getName()));
				}
			}
		}			   
		
		else{	
			throw new InvalidTokenException(_token);
		}
	}
	
	public final List<FileDto> result(){
    	return listed;
    }

}
