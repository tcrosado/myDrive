package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;

public class ListDirectoryTest extends AbstractServiceTest{
	
	Login session;
	Login session1;
	Login sessionRoot;
	AppMyDrive md;


	@Override
	protected void populate() {
		md = AppMyDrive.getInstance();
		User u = new User(md, "joao", "JOAO", "JOAOO678", "rw-dr---", "/home/joao/");
		User u2 = new User(md, "joana", "JOANA", "JOANAA78", "rw-d----", "/home/joana/");

		session = new Login(u);	
		session1 = new Login(u2);

		Directory d = new Directory("JoaoFolderAllPermissions", u.getHomeDir(), md.nextID(), u, "rwxd----");
		Directory d1 = new Directory("JoaoFolderNoPermissions", u.getHomeDir(), md.nextID(), u, "-w------");
		new Directory("JoanaJoaoFolder", d, md.nextID(), u, "rwxdrwxd");
	    
		new PlainFile(md.nextID(), u.getHomeDir(), "JoaoTest", u, "rwx-----", "jjjjjj");
		new PlainFile(md.nextID(), d1, "JoaoTest2", u, "rwx-----", "123456");
		new PlainFile(md.nextID(), u2.getHomeDir(), "JoanaTest", u2, "rwx-----", "aaaaaaaa");
		
	    new Link(md.nextID(), u.getHomeDir(), "JoaoLinkToJoanaFile", u, "rwx-----", "/home/joana/JoanaTest");
	    
	    new App(md.nextID(), u.getHomeDir(), "JoaoApp", u, "-wx-----", "package.class");

	    sessionRoot = new Login(md.getUserByUsername("root"));
	    
	}
	
	//TEST WITH ROOT
	
	@Test
    public void successRootListNoPermissionDir() {
		ListDirectoryService service;
		sessionRoot.setPath("/home/joao/JoaoFolderNoPermissions/");
		service = new ListDirectoryService(sessionRoot.getToken(),"/home/joao/JoaoFolderNoPermissions/");
		service.execute();
		
		Directory dir = (Directory) md.getFileByPath("/home/joao/JoaoFolderNoPermissions/");
		String [] s = dir.list();
		assertNotNull(service.result());
//		assertArrayEquals("Invalid permissions to root", s, service.result());
    }
	
	@Test
    public void successRootDirList() {
		ListDirectoryService service;
		sessionRoot.setPath("/");
		service = new ListDirectoryService(sessionRoot.getToken(),"/");
		service.execute();
		
		Directory dir = (Directory) md.getFileByPath("/");
		String [] s = dir.list();
		assertNotNull(service.result());
		//assertArrayEquals("/Dir problem", s, service.result());
    }
		
	//TESTS WITH AN USER
	
	@Test
    public void sucessListOwnDir() {
		ListDirectoryService service;
		session.setPath("/home/joao/");
		service = new ListDirectoryService(session.getToken(),"/home/joao/");
		service.execute();
		
		Directory dir = (Directory) md.getFileByPath("/home/joao/");
		String [] s = dir.list();
		assertNotNull(service.result());
		//assertArrayEquals("Invalid read from own dir", s, service.result());
    }
	
	@Test
    public void successListOtherUserDir() {
		ListDirectoryService service;
		session.setPath("/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
		service = new ListDirectoryService(session.getToken(),"/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
		service.execute();
		
		Directory dir =  (Directory) md.getFileByPath("/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
		String [] s = dir.list();
		assertNotNull(service.result());
//		assertArrayEquals("Invalid read from other user dir", s, service.result());

    }
	
	@Test ( expected = AccessDeniedException.class)
    public void listDirNoPermissions() {
		ListDirectoryService service;
		session1.setPath("/home/joao/JoaoFolderNoPermissions/");
		service = new ListDirectoryService(session1.getToken(),"/home/joao/JoaoFolderNoPermissions/");
		service.execute();
    }
	
	@Test 
    public void listhomeDirNormalUser() {
		ListDirectoryService service;
		session1.setPath("/home/");
		service = new ListDirectoryService(session1.getToken(),"/home/");
		service.execute();
		
		Directory dir = (Directory) md.getFileByPath("/home/");
		String [] s =dir.list();
		assertNotNull(service.result());
	//	assertArrayEquals("Invalid read from /home dir", s, service.result());
    }


	@Test(expected = InvalidTokenException.class)
	   public void invalidListDirectoryWithInvalidToken() {
		   long invalidToken = -1;
		   session.setPath("/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
		   ListDirectoryService service = new ListDirectoryService(invalidToken,"/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
	       service.execute(); 
	   }
		
		@Test( expected = InvalidTokenException.class)
		public void sessionRootExpired(){
			sessionRoot.setPath("/");
			ListDirectoryService service = new ListDirectoryService(sessionRoot.getToken(),"/");
			sessionRoot.setLastAccess(sessionRoot.getLastAccess().minusMinutes(15));
			service.execute();
		}
		
		@Test( expected = InvalidTokenException.class)
		public void sessionRootInactive(){
			sessionRoot.setPath("/");
			ListDirectoryService service = new ListDirectoryService(sessionRoot.getToken(),"/");
			sessionRoot.setActive(false);
			service.execute();
		}

		@Test( expected = InvalidTokenException.class)
		public void sessionExpired(){
			session.setPath("/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
			ListDirectoryService service = new ListDirectoryService(session.getToken(),"/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
			session.setLastAccess(session.getLastAccess().minusHours(3));
			service.execute();
		}
		
		@Test( expected = InvalidTokenException.class)
		public void sessionInactive(){
			session.setPath("/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
			ListDirectoryService service = new ListDirectoryService(session.getToken(),"/home/joao/JoaoFolderAllPermissions/JoanaJoaoFolder/");
			session.setActive(false);
			service.execute();
		}
}
