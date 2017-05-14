package pt.tecnico.myDrive.presentation;

import java.util.List;

import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.service.AddVariableService;
import pt.tecnico.myDrive.service.dto.VariableDto;

public class Environment extends MdCommand {

	
	
	public Environment(Shell shell) {
		super(shell, "env");
	} 

	@Override
	public
	void execute(String[] args) {
		long token = this.getShell().getCurrentToken(); 
		AddVariableService service;
		try{
			if (args.length == 0)
			{
				service = new AddVariableService(token,null,null);
				service.execute();
				List<VariableDto> varList = service.result();			
				for(VariableDto var : varList){
					System.out.println(var.getName()+" = "+var.getValue());
				}
			}
			else if (args.length == 1){
				service = new AddVariableService(token,null,null);
				service.execute();
				List<VariableDto> varList = service.result();
				for(VariableDto var : varList){
					if(var.getName().equals(args[0])){
						System.out.println(var.getValue());
					}
				}
			}
			else{
				service = new AddVariableService(token,args[0],args[1]);
				service.execute();
			}
		}
		catch (AppMyDriveException e){
			getShell().println(e.getMessage());
		}
	}
}
