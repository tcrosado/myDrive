package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;

public class GetCurrentUserService extends MyDriveService {
	
	long _currenToken;
	String _currentUsername;
	
	public GetCurrentUserService(long token){
		_currenToken = token;
	}

	@Override
	protected void dispatch() throws AppMyDriveException {
		
		AppMyDrive app = getMyDrive();
		String currentUser = app.getSessionByToken(_currenToken).getUsername();
		Login sessao = app.getSessionByToken(_currenToken);
		
		if(sessao.canAccess()){
			
			_currentUsername = currentUser;
		}
		else{
			throw new InvalidTokenException(_currenToken);
		}
	}
	
	public final String result(){
    	return _currentUsername;
    }
}
