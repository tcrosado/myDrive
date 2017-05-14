package pt.tecnico.myDrive.presentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.service.ExecuteFileService;

public class Execute extends MdCommand {
	
	public Execute(Shell shell) {
		super(shell, "do");
	}

	@Override
	public
	void execute(String[] args) {
		long token = this.getShell().getCurrentToken();
		if (args.length==0){
			System.out.println("Nothing to Execute!");
		}
		try{
			ExecuteFileService service = new ExecuteFileService(token, args[0], Arrays.copyOfRange(args, 1, args.length));
			service.execute();
		} catch (AppMyDriveException e){
			getShell().println(e.getMessage());
		}
	}
}