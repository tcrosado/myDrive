package pt.tecnico.myDrive.presentation;


import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.dto.FileDto;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;

public class ListCommand extends MdCommand {

	public ListCommand(Shell shell) {
		super(shell, "ls");
	}

	@Override
	public
	void execute(String[] args) {
		long token = this.getShell().getCurrentToken();
		ListDirectoryService list;
		try{
			if (args.length == 0){
				list = new ListDirectoryService(token, ".");
				}	
			else{
				list = new ListDirectoryService(token, args[0]);
			}
			list.execute();
			for (FileDto f: list.result()){
				if(!f.getType().equals("Link")){
					System.out.println(f.getType()+" "+f.getMask()+" "+f.getSize()+" "+f.getUser()+" "+f.getId()+" "+f.getDate()+" "+f.getName());
				}
				else{
					System.out.println(f.getType()+" "+f.getMask()+" "+f.getSize()+" "+f.getUser()+" "+f.getId()+" "+f.getDate()+" "+f.getName()+" -> "+f.getContent());
				}
			}
		}catch (AppMyDriveException e){
			getShell().println(e.getMessage());
		}
	}
}