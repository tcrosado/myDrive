package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.service.ChangeDirectoryService;

public class ChangeWorkingDirectory extends MdCommand {

	
	
	public ChangeWorkingDirectory(Shell shell) {
		super(shell, "cwd");
	}

	@Override
	public
	void execute(String[] args) {
		long token = this.getShell().getCurrentToken();
		if (args.length == 0){
			ChangeDirectoryService cwd = new ChangeDirectoryService(token, ".");
			cwd.execute();
			System.out.println(cwd.result());
		}else{
			try{
				ChangeDirectoryService cwd = new ChangeDirectoryService(token, args[0]);
				cwd.execute();
				System.out.println(cwd.result());
			} catch (AppMyDriveException e){
				getShell().println(e.getMessage());
			}
		}
	}

}
