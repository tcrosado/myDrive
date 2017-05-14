package pt.tecnico.myDrive.presentation;

import java.util.AbstractMap;
import java.util.HashMap;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.service.GetCurrentUserService;
import pt.tecnico.myDrive.service.LoginUserService;

public class MdShell extends Shell {
	
	public static void main(String[] args) throws Exception {
		MdShell shell = new MdShell();
		shell.execute();
	}

	public MdShell(){ //Add available commands here
					 // Check PbShell from PhoneBook v3
		super("myDrive");
		
		new ChangeWorkingDirectory(this);
		new LoginCommand(this);
		new Environment(this);
		new Execute(this);
		new KeyCommand(this);
		new WriteCommand(this);
		new ListCommand(this);

		
		LoginUserService defaultSession = new LoginUserService("nobody", "");
		defaultSession.execute();
		Long guestToken = defaultSession.result();
		this.setCurrentToken(guestToken);
		this.addRecentToken("nobody", defaultSession.result());
		
	}
	
	
}
