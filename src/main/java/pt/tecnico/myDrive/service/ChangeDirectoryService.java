package pt.tecnico.myDrive.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.IsNotDirectoryException;

public class ChangeDirectoryService extends MyDriveService {
	
	private static final Logger logger = LoggerFactory.getLogger(ChangeDirectoryService.class);
	
	AppMyDrive _app;
	Login _session;
	long _token;
	String _path;
	String _result;
	
	public ChangeDirectoryService(long token,String path){
		_token = token;
		if(path.endsWith("/") && !(path.equals("/"))) {
			int index = path.lastIndexOf("/");
			path = path.substring(0, index);
		}
		_path = path;
		_result = "";
	}
	
	@Override
	protected void dispatch() throws IsNotDirectoryException,AccessDeniedException,FileNotFoundException{
		_app = AppMyDrive.getInstance();
		_session = _app.getSessionByToken(_token);
		
		String currentPath = _session.getPath();
		
		if(!(_session.canAccess())){
			logger.warn("Invalid Session");
			throw new InvalidTokenException(_session.getToken());
		}
		
		if(currentPath.equals(_path)||(_path.equals(".")))
			_result = currentPath;
			
		else if(_path.equals(".."))
			changeToFather();
		else if(_path.startsWith("/"))
			changeFromAbsolutePath();
		else
			changeFromRelativePath();
		
	}
	
	public String result(){
		return _result;
	}
	
	private void changeFromAbsolutePath() throws IsNotDirectoryException,FileNotFoundException,AccessDeniedException{
		File f = _app.getFileByPath(_path);
		
		verifyDir(f);
		
		_session.setPath(_path);
		_result = _path;
	}
	
	private void changeFromRelativePath() throws IsNotDirectoryException,AccessDeniedException,FileNotFoundException{
		
		String currentPath = _session.getPath();
		
		if(!(currentPath.equals("/")))
			currentPath+="/";
		
		String objective = currentPath+_path;
		
		File f = _app.getFileByPath(objective);
	
		verifyDir(f);
		
		_session.setPath(objective);
		_result = objective;
	}
	
	private void changeToFather(){
		Directory dir = (Directory) _app.getFileByPath(_session.getPath());
		Directory father = (Directory) dir.getDir();
		
		verifyDir(father);
		
		_session.setPath(father.getPath());
		_result = father.getPath();
		
	}
	
	private void verifyDir(File f) throws IsNotDirectoryException,AccessDeniedException,FileNotFoundException{
		User u = _app.getUserByUsername(_session.getUsername());
		if (!(f.isDir()))
			throw new IsNotDirectoryException(_path);
		else if (!(f.canExecute(u)))
			throw new AccessDeniedException(u.getUsername());
	}
}
