package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.FileExistsException;
import pt.tecnico.myDrive.exceptions.InvalidNameException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.IsNotFileException;
import pt.tecnico.myDrive.exceptions.NeedForContentException;
import pt.tecnico.myDrive.exceptions.NoNeedForContentException;
import pt.tecnico.myDrive.exceptions.PathTooLongException;


public class CreateFileService  extends MyDriveService {
	
	long _token;
	String _name;
	String _type;
	String _content = null;
	
	public CreateFileService(long token,String name,String type,String content){
		_token=token;
		_name=name;
		_type=type;
		_content=content;
	}
	public CreateFileService(long token,String name,String type){
		_token=token;
		_name=name;
		_type=type;
	}
	
	@Override
	protected void dispatch() throws PathTooLongException,NoNeedForContentException,NeedForContentException,FileExistsException,AccessDeniedException, InvalidNameException{
		AppMyDrive app = getMyDrive();
		Login session = app.getSessionByToken(_token);
		

		User currentUser = app.getUserByUsername(session.getUsername());
		int id;
		
		if(session.canAccess()){
			String path= session.getPath();	
    		Directory currentDir = (Directory) app.getFileByPath(path);
    		if(!currentDir.canWrite(currentUser))
    			throw new AccessDeniedException(session.getUsername());	
    		if(_type.equals("Directory")){
    			if(_content  != null)
    				throw new NoNeedForContentException(_type);
    			id=app.nextID();
    			currentDir.createDir(_name, id, currentUser, currentUser.getMask());
    		}
    		else if(_type.equals("App")){
    			if(_content == null)
    				throw new NeedForContentException(_type);
    			id=app.nextID();
    			currentDir.createApp(id, _name, currentUser, currentUser.getMask(),_content);
    		}
    		else if(_type.equals("Link")){
    			if(_content == null)
    				throw new NeedForContentException(_type);
    			id=app.nextID();
    			currentDir.createLink(id,_name,currentUser,currentUser.getMask(),_content);    			
    		}
    		else if(_type.equals("PlainFile")){
    			if(_content == null)
    				throw new NeedForContentException(_type);
    			id=app.nextID();
    			currentDir.createPlainFile(id,_name,currentUser,currentUser.getMask(),_content);
    		}
    		else
    			throw new IsNotFileException(_type);
		}
		else{
			throw new InvalidTokenException(_token);
		}
	}
}