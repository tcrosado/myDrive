package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.exceptions.InvalidOperationException;

public class Login extends Login_Base {
    
    public Login(User user) {
        super();
        super.setApp(user.getApp());
        this.getApp().addSession(this);
        super.setToken(newToken());
        super.setActive(true);
        super.setUsername(user.getUsername());
        String path = user.getHomeDir().getPath();
        super.setPath(path);
        DateTime date = new DateTime();
        super.setLastAccess(date);
    }

    private long newToken(){
    	long token;
    	boolean notUnique;
    	do{
    		notUnique = true;
    		token =  new BigInteger(64,new Random()).longValue();
    		try{
    			this.getApp().getSessionByToken(token);
    		}catch (AppMyDriveException e){
    			notUnique = false;
    		}
    		
    		
    	}while(notUnique);
		return token;
	}
    
    public boolean canAccess(){
    	if(super.getActive()){
    		DateTime access = new DateTime();
    		DateTime limitAccess;
    		if(this.getUsername().equals("root")){
    			limitAccess = super.getLastAccess().plusMinutes(10);
    		}
    		else if(this.getUsername().equals("nobody")){
    			limitAccess = access.plusDays(1);
    		}
    		else{
    			limitAccess = super.getLastAccess().plusHours(2);
    		}
    		if(access.isAfter(limitAccess)){
    			super.setActive(false);
    		}else{
    			super.setLastAccess(access);
    		}
    	}
    	return super.getActive();
    }
    
    public void verifyExpiration(DateTime current){
    	DateTime limitAccess;
    	if(this.getUsername().equals("root")){
			limitAccess = super.getLastAccess().plusMinutes(10);
		}
		else{
			limitAccess = super.getLastAccess().plusHours(2);
		}
    	if(this.getUsername().equals("nobody")){
			super.setActive(true);
			return;
		}
		if(current.isAfter(limitAccess)){
			super.setActive(false);
		}
    }

    
    @Override
	public void setApp(AppMyDrive app){
    	throw new InvalidOperationException("Cannot change AppMyDrive in Login entity");
    }
    
    @Override
	public void setToken(long token){
    	throw new InvalidOperationException("Cannot change token in Login entity");
    }
    
    @Override
	public void setActive(boolean active){
    	if(active)
    		throw new InvalidOperationException("Cannot change active status in Login entity");
    	super.setActive(active);
    }
    
    @Override
	public void setUsername(String username){
    	throw new InvalidOperationException("Cannot change username in Login entity");
    }
    
    @Override
	public void setPath(String path){
    	File file = this.getApp().getFileByPath(path);
    	if(file == null){ throw new InvalidOperationException("Invalid path");}
    	else if(!file.isDir()){ throw new InvalidOperationException("Path isn't a directory");}
    	else{ super.setPath(path);}
    }
    
    @Override
	public void setLastAccess(DateTime date){
    	if(date.isAfter(this.getLastAccess()))
    		throw new InvalidOperationException("Cannot change last access in Login entity");
    	super.setLastAccess(date);
    }

	public void remove() {
		super.getApp().removeSession(this);
		super.setApp(null);
		deleteDomainObject();
	}

	public void addVariable(String name, String value) {
		Set<Variable> varList = this.getVarSet();
		Variable newVar;
		for(Variable var : varList){
			if(var.getName().equals(name)){
				var.setValue(value);
				return;
			}
		}
		newVar =  new Variable(this,name,value);
		varList.add(newVar);
	}
	
	public boolean removeVariable(String name){
		Set<Variable> varList = this.getVarSet();
		for(Variable var : varList)
			if(var.getName().equals(name))
				return varList.remove(var);
		
		return false;
	}
	
	public Set<Variable> getVarList(){
		return this.getVarSet();
	}
	
	public Variable getVariable(String name){
		Set<Variable> varList = this.getVarSet();
		for(Variable var : varList)
			if(var.getName().equals(name))
				return var;
		
		return null;
	}
}
