package pt.tecnico.myDrive.presentation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public abstract class Shell {
	private AbstractMap<String,Command> _commands = new HashMap<String,Command>();
	private String _name;
	
	private long _currentToken;
	AbstractMap<String,Long> _recentSessions; 
	
	public Shell(String name){
		_name = name;
		
		_recentSessions = new HashMap<String,Long>();
		
		  new Command(this, "exit") {
		      void execute(String[] args) {
			System.out.println(name+" exit");
		        System.exit(0);
		      }
		    };
	
	}
	
	
	public void print(String s){ System.out.print(s);}
	public void println(String s){ System.out.println(s);}
	public void flush(){ System.out.flush();}
	
	boolean add(Command c){
		Command com = _commands.put(c.getName(), c);
		return (com == null);
	}
	
	public Command get(String name){
		return _commands.get(name);
		
	}
	
	public Collection<String> listCommands(){
		return Collections.unmodifiableCollection(_commands.keySet());
	}
	
	public void execute() throws Exception{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    String str, prompt = null; // System.getenv().get("PS1");

	    if (prompt == null) prompt = "$ ";
	    System.out.println(_name+" shell ('exit' to leave)");
	    System.out.print(prompt);
	    while ((str = in.readLine()) != null) {
	      String[] arg = str.split(" ");
	      Command com = _commands.get(arg[0]);
	      if (com != null) {
		try {
		  com.execute(Arrays.copyOfRange(arg, 1, arg.length));
		} catch (RuntimeException e) {
		  System.err.println(arg[0]+": "+e);
		  e.printStackTrace(); // debug
		}
	      } else
		if (arg[0].length() > 0)
	          System.err.println(arg[0]+": command not found.");
	      System.out.print(prompt);
	    }
	    System.out.println(_name+" end");
	}	
	
	
	public long getCurrentToken() {
		return _currentToken;
	}

	public void setCurrentToken(long currentToken) {
		_currentToken = currentToken;
	}
	

	public Long getRecentToken(String username){
		
		return _recentSessions.get(username);
	}
	
	public void addRecentToken(String username,Long token){
		_recentSessions.put(username, token);
	}
	
	
}
