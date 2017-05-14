package pt.tecnico.myDrive.service;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.InvalidPasswordException;
import pt.tecnico.myDrive.exceptions.UserNotFoundException;


public class LoginUserService  extends MyDriveService{
	
	String _username;
	String _password;
	long _token;
	
	public LoginUserService(String username,String password){
		_username=username;
		_password=password;
	}
	
	@Override
	protected void dispatch() throws InvalidPasswordException, UserNotFoundException{
		
		AppMyDrive app = getMyDrive();
		
		// Check existing sessions
		DateTime current = new DateTime();
		for(Login l : app.getSessionSet()){
			if(l.getActive())
				l.verifyExpiration(current);
			else
				l.remove();
		}
				
		//Create new session
		User u=app.getUserByUsername(_username);
		if(u==null)
			throw new UserNotFoundException(_username);
		
		else if(!u.getPassword().equals(_password))
			throw new InvalidPasswordException(_username);
		Login l = new Login(u);
		_token = l.getToken();
	}
	
	public final long result(){
    	return _token;
    }
}