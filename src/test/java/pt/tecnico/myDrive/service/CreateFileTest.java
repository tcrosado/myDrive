package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.RootUser;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.*;
import pt.tecnico.myDrive.service.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateFileTest extends AbstractServiceTest {
	/* TODO
	 * Test Create File (Plain File/App/Link/Directory)
	 * Test Create File with same name
	 * */
	
	Login sessionUser, sessionRoot;
	Directory targetDir,userHomeDir;
	AppMyDrive app;
	User user,root;
	
	protected void populate() {
		app = AppMyDrive.getInstance();
		
		//Superuser
		root = app.getUserByUsername("root");
		sessionRoot = new Login(root);
			

		//ExampleUser
		user = new User(app, "example", "example", "example8", "rwxd----", "/home/example");
		sessionUser = new Login(user);
		userHomeDir = (Directory) app.getFileByPath(sessionUser.getPath());  
		userHomeDir.createPlainFile( app.nextID(), "examplePlain", user, "rwxd----", "File is testing");
	
    }

	@Test
    public void successCreateDirectory() {
		CreateFileService service;
	
		//User (session) -> Directory
		targetDir = (Directory) app.getFileByPath(sessionUser.getPath());
        service = new CreateFileService(sessionUser.getToken(),"_userDir","Directory");
        service.execute();
        
        		// check user dir was well created
        Directory dir = (Directory) targetDir.getChildbyName("_userDir");
        assertNotNull("Directory was created",dir); 
        assertEquals("Invalid mask",user.getMask(),dir.getMask());
        assertEquals("Invalid path","/home/example/_userDir",dir.getPath());
        assertEquals("Invalid owner",user.getUsername(),dir.getUser().getUsername());   
    }
	
	@Test
    public void successCreateLink() {
		CreateFileService service;
	
		//User (session) -> Link
		targetDir = (Directory) app.getFileByPath(sessionUser.getPath());
        service = new CreateFileService(sessionUser.getToken(),"_userLink","Link","/home");
        service.execute();
        
        		// check user dir was well created
        Link l = (Link) targetDir.getChildbyName("_userLink");
        assertNotNull("Link was created", l); 
        assertEquals("Invalid mask",user.getMask(),l.getMask());
        assertEquals("Invalid path","/home/example/_userLink",l.getPath());
        assertEquals("Invalid owner",user.getUsername(),l.getUser().getUsername());
        assertEquals("Invalid link path","/home",l.getText());
    }
	
	@Test
    public void successCreatePlainFile() {
		CreateFileService service;
	
		//User (session) -> Plainfile
		targetDir = (Directory) app.getFileByPath(sessionUser.getPath());
        service = new CreateFileService(sessionUser.getToken(),"_userPlainFile","PlainFile","rui was here");
        service.execute();
        
        		// check user plainfile was well created
        PlainFile pf = (PlainFile) targetDir.getChildbyName("_userPlainFile");
        assertNotNull("PlainFile was created", pf); 
        assertEquals("Invalid mask",user.getMask(),pf.getMask());
        assertEquals("Invalid path","/home/example/_userPlainFile",pf.getPath());
        assertEquals("Invalid owner",user.getUsername(),pf.getUser().getUsername());  
        assertEquals("Invalid content","rui was here",pf.read());
    }
	
   @Test(expected = PathTooLongException.class)
   public void invalidFileCreationWithPathTooLong() {
       String name = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	   CreateFileService service = new CreateFileService(sessionRoot.getToken(),name,"Directory");
       service.execute(); 
   }
	
   @Test(expected = InvalidNameException.class)
   public void invalidFileCreationWithInvalidName() {
       
       CreateFileService service = new CreateFileService(sessionRoot.getToken(),"ola\0","Directory");
       service.execute(); 
   }
   
   @Test(expected = NoNeedForContentException.class)
   public void invalidFileCreationWithContent() {
       
       CreateFileService service = new CreateFileService(sessionRoot.getToken(),"testDir","Directory","testing");
       service.execute(); 
   }

   @Test(expected = NeedForContentException.class)
   public void invalidFileCreationWithNoContent() {
       
       CreateFileService service = new CreateFileService(sessionRoot.getToken(),"testPlainFile","PlainFile");
       service.execute(); 
   } 

   @Test(expected = FileExistsException.class)
   public void invalidFileCreationWithDuplicateName() {
       CreateFileService service = new CreateFileService(sessionUser.getToken(),"examplePlain","PlainFile","File is Testing");
       service.execute(); 
   }
   
   @Test(expected = AccessDeniedException.class)
   public void invalidFileAccess() {
       sessionUser.setPath("/home/root");
       CreateFileService service = new CreateFileService(sessionUser.getToken(),"accessInvalidDir","Directory");
       service.execute();
   }

   @Test(expected = InvalidTokenException.class)
   public void invalidFileCreationWithInvalidToken() {
	   long invalidToken = -1;
       CreateFileService service = new CreateFileService(invalidToken,"examplePlain","PlainFile","File is Testing");
       service.execute(); 
   }
	
	@Test( expected = InvalidTokenException.class)
	public void sessionRootExpired(){
		CreateFileService service = new CreateFileService(sessionRoot.getToken(), "examplePlain","PlainFile","File is Testing");
		sessionRoot.setLastAccess(sessionRoot.getLastAccess().minusMinutes(15));
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionRootInactive(){
		CreateFileService service = new CreateFileService(sessionRoot.getToken(),"examplePlain","PlainFile","File is Testing");
		sessionRoot.setActive(false);
		service.execute();
	}

	@Test( expected = InvalidTokenException.class)
	public void sessionExpired(){
		CreateFileService service = new CreateFileService(sessionUser.getToken(),"examplePlain","PlainFile","File is Testing");
		sessionUser.setLastAccess(sessionUser.getLastAccess().minusHours(3));
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionInactive(){
		CreateFileService service = new CreateFileService(sessionUser.getToken(),"examplePlain","PlainFile","File is Testing");
		sessionUser.setActive(false);
		service.execute();
	}
}

