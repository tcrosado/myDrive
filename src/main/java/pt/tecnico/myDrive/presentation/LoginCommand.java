package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.service.LoginUserService;

public class LoginCommand extends MdCommand {
	
	
	
	public LoginCommand(Shell shell) {
		super(shell, "login");
	}

	@Override
	public
	void execute(String[] args) {
		String password = "";
		
		if ((args.length < 2) && !(args[0].equals("nobody"))){
			println("Too few arguments on Login Command");
			return;
		}
		if(!(args[0].equals("nobody")))
			password = args[1];
		
		LoginUserService service = new LoginUserService(args[0], password);
		try{
			service.execute();
			getShell().setCurrentToken(service.result());
			getShell().addRecentToken(args[0], service.result());
		}catch(AppMyDriveException e){
			println("Errors ocurred during Login process:");
			println(e.getMessage());
		}
	}

}
