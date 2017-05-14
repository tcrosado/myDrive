package pt.tecnico.myDrive.domain;

public class GuestUser extends GuestUser_Base {
    
    public GuestUser(AppMyDrive app) {
        this.setApp(app);
        this.setName("Guest");
        this.setUsername("nobody");
        this.setPassword("");
        this.setMask("rwxdr-x-");
        Directory home = (Directory) app.getFileByPath("/home/");
        Directory homeDir= home.createDir("nobody",app.nextID(),this,"rwxdr-x-");
        this.addFile(homeDir);
        this.setHomeDir(homeDir);
        homeDir.setHomeUser(this);
    }    

	@Override
	public void remove(){
		//Guest User cannot be removed
		for(File f : this.getFileSet()){
			if(!(f.getPath().equals("/home/nobody")))
				f.remove();
		}
	}
	
	@Override
	public boolean isGuest(){
		return true;
	}
}