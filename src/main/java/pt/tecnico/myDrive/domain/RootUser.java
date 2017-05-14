package pt.tecnico.myDrive.domain;

public class RootUser extends RootUser_Base {
    
	
	protected RootUser(AppMyDrive app) {
        this.setApp(app);
        this.setName("Super User");
        this.setUsername("root");
        this.setPassword("***");
        this.setMask("rwxdr-x-");
        Directory home = (Directory) app.getFileByPath("/home/");
        Directory homeDir= home.createDir("root",app.nextID(),this,"rwxdr-x-");
        this.addFile(homeDir);
        this.setHomeDir(homeDir);
        homeDir.setHomeUser(this);
    }
    
    @Override
    public void remove(){
    	//Root User cannot be removed
    	for(File f : this.getFileSet()){
    		if(!(f.getPath().equals("/")||f.getPath().equals("/home")||f.getPath().equals("/home/root")))
    			f.remove();
    	}
    }
    
    @Override
    public boolean isRoot(){
		return true;
	}
    
}
