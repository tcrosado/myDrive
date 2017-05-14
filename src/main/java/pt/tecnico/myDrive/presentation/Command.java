package pt.tecnico.myDrive.presentation;

public abstract class Command {

	private String _name;
	private Shell _shell;
	
	public Command(Shell shell, String name){
		_name = name;
		_shell = shell;
		_shell.add(this);
	}

	public String getName() {
		return _name;
	}

	public Shell getShell() {
		return _shell;
	}
	
	abstract void execute(String[] args);
	
	public void print(String s){
		_shell.print(s);
	}
	
	public void println(String s){
		_shell.println(s);
	}
	
	public void flush(){
		_shell.flush();
	}
}
