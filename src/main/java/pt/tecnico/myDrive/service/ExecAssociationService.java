package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exceptions.AppMyDriveException;

public class ExecAssociationService  extends MyDriveService{ 
	long _token;
	String _path;
	private String[] _args;


	public ExecAssociationService(long token, String path, String[]args){
		_args =args;
		_token=token;
		_path=path;	
	}
	
	
	@Override
	protected void dispatch() throws AppMyDriveException {
		return;
		
	}


	public String result(){
		return "";
	}
}