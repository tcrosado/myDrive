package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;

import static org.junit.Assert.assertNotEquals;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.*;
import pt.tecnico.myDrive.service.*;

import org.junit.Test;

public class WriteFileTest extends AbstractServiceTest {
	/* TODO
	 * */
	
	AppMyDrive md;
	Directory homeDir;
	Directory rootHome;
	Login session;
	Login sessionRoot;
	Login sessionGuest;
	DateTime lastModificationAllPermissions;
	DateTime lastModificationPlainFile;
	DateTime lastModificationLink;
	DateTime lastModificationLinkNoPermission;
	DateTime lastModificationApp;
	DateTime lastModificationRootPlain;
	DateTime lastModificationRootApp;
	DateTime lastModificationRootLink;
	
	protected void populate() {
		md = AppMyDrive.getInstance();
		
		new User(md, "joao", "Joao", "12345678", "rw-dr---", "/home/joao/");
		
		User u = md.getUserByUsername("joao");
		User root = md.getUserByUsername("root");
		User guest = md.getUserByUsername("nobody");
		sessionGuest = new Login(guest);
		sessionGuest.setPath("/home/joao");
		session = new Login(u);
		sessionRoot = new Login(root);
		sessionRoot = new Login(root);
		
		homeDir = u.getHomeDir();
		rootHome = root.getHomeDir();
		
		/*Files from User*/
		homeDir.createPlainFile( md.nextID(), "examplePlain", u, "rwx-----", "File is testing");
		homeDir.getChildbyName("examplePlain").setLastModification(homeDir.getChildbyName("examplePlain").getLastModification().minusMinutes(1));
		lastModificationPlainFile = homeDir.getChildbyName("examplePlain").getLastModification();
		
		/*Nobody*/
		homeDir.createPlainFile( md.nextID(), "examplePlainAllPerm", u, "rwxdrwxd", "File testing");
		homeDir.getChildbyName("examplePlainAllPerm").setLastModification(homeDir.getChildbyName("examplePlainAllPerm").getLastModification().minusMinutes(1));
		lastModificationAllPermissions = homeDir.getChildbyName("examplePlainAllPerm").getLastModification();
		/******/
		
		homeDir.createLink(md.nextID(), "UserLinkFile", u, "rwxd----","/home/joao/examplePlain");
		homeDir.getChildbyName("UserLinkFile").setLastModification(homeDir.getChildbyName("UserLinkFile").getLastModification().minusMinutes(1));
		lastModificationLink = homeDir.getChildbyName("UserLinkFile").getLastModification();
		
		homeDir.createLink(md.nextID(), "LinkWithNoPermission", u, "r-xd----", "/home/joao/examplePlain");
		homeDir.getChildbyName("LinkWithNoPermission").setLastModification(homeDir.getChildbyName("LinkWithNoPermission").getLastModification().minusMinutes(1));
		lastModificationLinkNoPermission = homeDir.getChildbyName("LinkWithNoPermission").getLastModification();
		
		homeDir.createApp(md.nextID(), "AppUser", u, "rwxd----", "test.app");
		homeDir.getChildbyName("AppUser").setLastModification(homeDir.getChildbyName("AppUser").getLastModification().minusMinutes(1));
		lastModificationApp = homeDir.getChildbyName("AppUser").getLastModification();
		
		homeDir.createDir("folder", md.nextID(),u, "rwxd----");
		homeDir.createLink(md.nextID(), "linkFolder", u, "rwxd----", "/home/joao/folder");
		
		/*File from Root*/
		rootHome.createPlainFile(md.nextID(), "rootFile",root,"rwx-r-x-","This belongs to Root");
		rootHome.getChildbyName("rootFile").setLastModification(rootHome.getChildbyName("rootFile").getLastModification().minusMinutes(1));
		lastModificationRootPlain = rootHome.getChildbyName("rootFile").getLastModification();
		
		rootHome.createApp(md.nextID(), "AppRoot", root, "rwxd----", "test.app");
		rootHome.getChildbyName("AppRoot").setLastModification(rootHome.getChildbyName("AppRoot").getLastModification().minusMinutes(1));
		lastModificationRootApp = rootHome.getChildbyName("AppRoot").getLastModification();
		
		rootHome.createLink(md.nextID(), "LinkRoot", root, "rwxd----", "/home/joao/examplePlain");
		rootHome.getChildbyName("LinkRoot").setLastModification(rootHome.getChildbyName("LinkRoot").getLastModification().minusMinutes(1));
		lastModificationRootLink = rootHome.getChildbyName("LinkRoot").getLastModification();
		
		rootHome.createLink(md.nextID(), "linkFolder", u, "rwxd----", "/home/joao/folder");
		
				
		
		
		
    }

	@Test
    public void successUserPlainFile(){
		WriteFileService service;
        service = new WriteFileService(session.getToken(),"examplePlain", "Changed file");
		service.execute();
		
        // check files were well created
        PlainFile pf = (PlainFile) homeDir.getChildbyName("examplePlain");
        assertNotNull("PlainText was not created", pf);
        assertEquals("Invalid content on PlainText", "Changed file", pf.read());
        assertNotEquals(lastModificationPlainFile, pf.getLastModification());
        
    }
	
	@Test
    public void successRootPlainFile() {
		WriteFileService service;
        service = new WriteFileService(sessionRoot.getToken(),"rootFile", "Changed file");
		service.execute();
		
        // check files were well created
        PlainFile pf = (PlainFile) rootHome.getChildbyName("rootFile");
        assertNotNull("PlainText was not created", pf);
        assertEquals("Invalid content on PlainText", "Changed file", pf.read());
        assertNotEquals(lastModificationRootPlain, pf.getLastModification());
        
    }	
	
	@Test
	public void successUserLinkFile(){
		WriteFileService service;
		service = new WriteFileService(session.getToken(), "UserLinkFile", "Changed file");
		service.execute();
		
		//check files were well created
		Link l = (Link) homeDir.getChildbyName("UserLinkFile");
		assertNotNull("Link was not created", l);
		assertEquals("Invalid content on Link", "/home/joao/examplePlain", l.getText());
		assertEquals(lastModificationLink, l.getLastModification());
	
		PlainFile pf = (PlainFile) md.getFileByPath(l.getText());
		assertNotNull("PlainText was not created", pf);
		assertEquals("Invalid content on PlainText", "Changed file", pf.read());
		assertNotEquals(lastModificationLink, pf.getLastModification());
	}
	
	@Test
	public void successRootLinkFile(){
		WriteFileService service;
		service = new WriteFileService(sessionRoot.getToken(), "UserLinkFile", "Changed file");
		sessionRoot.setPath("/home/joao");
		service.execute();
		
		//check files were well created
		Link l = (Link) homeDir.getChildbyName("UserLinkFile");
		assertNotNull("Link was not created", l);
		assertEquals("Invalid content on Link", "/home/joao/examplePlain", l.getText());
		assertEquals(lastModificationLink, l.getLastModification());
	
		PlainFile pf = (PlainFile) md.getFileByPath(l.getText());
		assertNotNull("PlainText was not created", pf);
		assertEquals("Invalid content on PlainText", "Changed file", pf.read());
		assertNotEquals(lastModificationLink, pf.getLastModification());
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successUserLinkNoPermission(){
		WriteFileService service;
		service = new WriteFileService(session.getToken(), "LinkWithNoPermission", "Changed file");
		service.execute();
	}
	
	@Test
	public void successRootLinkNoPermission(){
		WriteFileService service;
		service = new WriteFileService(sessionRoot.getToken(), "LinkWithNoPermission", "Changed file");
		sessionRoot.setPath("/home/joao");
		service.execute();
		
		//check files were well created
		Link l = (Link) homeDir.getChildbyName("LinkWithNoPermission");
		assertNotNull("Link was not created", l);
		assertEquals("Invalid content on Link", "/home/joao/examplePlain", l.getText());
		assertEquals(lastModificationLinkNoPermission, l.getLastModification());
	
		PlainFile pf = (PlainFile) md.getFileByPath(l.getText());
		assertNotNull("PlainText was not created", pf);
		assertEquals("Invalid content on PlainText", "Changed file", pf.read());
		assertNotEquals(lastModificationLinkNoPermission, pf.getLastModification());
	}
	
	@Test
	public void successUserAppFile(){
		WriteFileService service;
		service = new WriteFileService(session.getToken(), "AppUser", "app.app");
		service.execute();
		
		//check files were well created
		App app = (App) homeDir.getChildbyName("AppUser");
		assertNotNull("App was not created", app);
		assertEquals("Invalid content on App", "app.app", app.read());
		assertNotEquals(lastModificationApp, app.getLastModification());
	}
	
	@Test (expected = AccessDeniedException.class)
	public void unsuccessUserAppFile(){
		WriteFileService service;
		service = new WriteFileService(session.getToken(), "AppRoot", "app.app");
		session.setPath("/home/root");
		service.execute();
	}
	
	@Test
	public void successRootAppFile(){
		WriteFileService service;
		service = new WriteFileService(sessionRoot.getToken(), "AppRoot", "app.app");
		service.execute();
		
		//check files were well created
		App app = (App) rootHome.getChildbyName("AppRoot");
		assertNotNull("App was not created", app);
		assertEquals("Invalid content on App", "app.app", app.read());
		assertNotEquals(lastModificationRootApp, app.getLastModification());
	}
	
	@Test(expected = IsNotPlainFileException.class)
	public void invalidWriteLinkToDirectory(){
		WriteFileService service = new WriteFileService(session.getToken(), "linkFolder", "write in folder");
		service.execute();		
	}
	
	@Test(expected = IsNotPlainFileException.class)
	public void invalidRootWriteLinkToDirectory(){
		WriteFileService service = new WriteFileService(sessionRoot.getToken(), "linkFolder", "write in folder");
		service.execute();		
	}
	
	@Test( expected = AccessDeniedException.class)
	public void invalidAccess(){
		WriteFileService service = new WriteFileService(session.getToken(), "rootFile", "joao changed root file");
		session.setPath("/home/root");
		service.execute();
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void invalidWriting(){
		WriteFileService service = new WriteFileService(session.getToken(), "folder", "folder with content");
		service.execute();
	}
	@Test( expected = FileNotFoundException.class)
	public void invalidFile(){
		WriteFileService service = new WriteFileService(session.getToken(), "unnamed", "file test");
		service.execute();
	}

	@Test( expected = InvalidTokenException.class)
	public void invalidToken(){
		WriteFileService service = new WriteFileService(-1, "AppUser", "app.app");
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionRootExpired(){
		WriteFileService service = new WriteFileService(sessionRoot.getToken(), "rootFile", "Changed file");
		sessionRoot.setLastAccess(session.getLastAccess().minusMinutes(15));
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionRootInactive(){
		WriteFileService service = new WriteFileService(sessionRoot.getToken(), "rootFile", "Changed file");
		sessionRoot.setActive(false);
		service.execute();
	}

	@Test( expected = InvalidTokenException.class)
	public void sessionExpired(){
		WriteFileService service = new WriteFileService(session.getToken(), "examplePlain", "Changed file");
		session.setLastAccess(session.getLastAccess().minusHours(3));
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionInactive(){
		WriteFileService service = new WriteFileService(session.getToken(), "examplePlain", "Changed file");
		session.setActive(false);
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void invalidAccessNobody(){
		WriteFileService service = new WriteFileService(sessionGuest.getToken(), "examplePlainAllPerm", "nobody permissions");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void sessionExpiredNobody(){
		WriteFileService service = new WriteFileService(sessionGuest.getToken(),"examplePlainAllPerm", "nobody permissions");
		sessionGuest.setLastAccess(sessionGuest.getLastAccess().minusHours(3));
		service.execute();
	}
}

