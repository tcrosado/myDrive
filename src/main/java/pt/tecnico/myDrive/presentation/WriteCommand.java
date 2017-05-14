package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.WriteFileService;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;


public class WriteCommand extends MdCommand {

	public WriteCommand(Shell shell) {
		super(shell, "update");

	}

	@Override
	public
	void execute(String[] args) {
		long token = this.getShell().getCurrentToken();
		if ((args.length < 2)){
			println("Too few arguments on Write Command");
			return;
		}
		else{
			try{
				WriteFileService writeFile = new WriteFileService(token,args[0],args[1]);
				writeFile.execute();
			} catch (AppMyDriveException e){
				getShell().println(e.getMessage());
			}
		}
	}
	
}