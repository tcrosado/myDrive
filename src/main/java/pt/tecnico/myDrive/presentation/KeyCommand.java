package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.service.GetCurrentUserService;


public class KeyCommand extends MdCommand{

	public KeyCommand(Shell shell) {
		super(shell, "token");
	}
	
	@Override
	public
	void execute(String[] args) {
		
		long token = this.getShell().getCurrentToken();
		
		if(args.length == 0){
			
			GetCurrentUserService service = new GetCurrentUserService(token);
			service.execute();
			String currentUsername = service.result();
			System.out.println(token);
			System.out.println(currentUsername);
			
		}else{
			try{
				String username = args[0];
				Shell md = getShell();
				Long currentToken = md.getRecentToken(username);
				if(currentToken == null){ return; }
				GetCurrentUserService service = new GetCurrentUserService(currentToken);
				String currentUsername = service.result();
				
				md.setCurrentToken(currentToken);
				md.addRecentToken(username, currentToken);	
				System.out.println(currentToken);
			}
			catch(AppMyDriveException e){
				getShell().println(e.getMessage());
			}
		}
	}
}
