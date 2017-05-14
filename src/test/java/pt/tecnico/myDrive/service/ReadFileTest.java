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
import pt.tecnico.myDrive.exceptions.IsNotPlainFileException;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;

public class ReadFileTest extends AbstractServiceTest{
	
	Login session;
	Login sessionRoot;
	AppMyDrive md;


	@Override
	protected void populate() {
		md = AppMyDrive.getInstance();
		User u = new User(md, "toni", "TONI", "TONIENORME", "rw-dr---", "/home/toni/");
		User u2 = new User(md, "anib", "Anib", "ANIBPEQUENO", "rw-d----", "/home/anib/");
		User u3 = new User(md, "pica", "Pica", "PICAPICA", "rw-dr---", "/home/pica/");

		session = new Login(u);
		new PlainFile(md.nextID(), u.getHomeDir(), "ToniPlainFileTest", u, "rwx-----", "Toni plain file");		
		new PlainFile(md.nextID(), u.getHomeDir(), "ToniPlainFileTestNoPrem", u, "-wx-----", "Toni plain file");		
		new PlainFile(md.nextID(), u2.getHomeDir(), "AnibPlainFileTest", u2, "rwx-----", "Anib plain file");		
		new PlainFile(md.nextID(), u3.getHomeDir(), "PicaPlainFileTest", u3, "rwx-r-x-", "Pica plain file");		

		new Directory("ToniFolder", u.getHomeDir(), md.nextID(), u, "rwxd----");
	    
	    new Link(md.nextID(), u.getHomeDir(), "ToniLinkPlainFile", u, "rwx-----", "/home/toni/ToniPlainFileTest");
	    new Link(md.nextID(), u.getHomeDir(), "ToniLinkPicaFile", u, "rwx-----", "/home/pica/PicaPlainFileTest");
	    new Link(md.nextID(), u3.getHomeDir(), "PicaLinkPicaFile", u3, "rwx-r-x-", "/home/pica/PicaPlainFileTest");
	    
	    new Link(md.nextID(), u.getHomeDir(), "ToniLinkPlainFileNoPrem", u, "rwx-----", "/home/toni/ToniPlainFileTestNoPrem");
	    new Link(md.nextID(), u.getHomeDir(), "ToniLinkNoPremPlainFile", u, "-wx-----", "/home/toni/ToniPlainFileTest");
	    new Link(md.nextID(), u.getHomeDir(), "ToniLinkDirectory", u, "rwx-----", "/home/toni/ToniFolder");
	    new Link(md.nextID(), u.getHomeDir(), "ToniLinkAnibFile", u, "rwx-----", "/home/anib/AnibPlainFileTest");
	    new Link(md.nextID(), u2.getHomeDir(), "AnibLinkAnibFile", u2, "rwx-----", "/home/anib/AnibPlainFileTest");
	    	    
	    new App(md.nextID(), u.getHomeDir(), "ToniApp", u, "rwx-----", "package.class");
	    new App(md.nextID(), u3.getHomeDir(), "PicaApp", u3, "rwx-r-x-", "package.class");

	    new App(md.nextID(), u.getHomeDir(), "ToniAppNoPrem", u, "-wx-----", "package.class");
	    new App(md.nextID(), u2.getHomeDir(), "AnibApp", u2, "rwx-----", "package.class");

	    sessionRoot = new Login(md.getUserByUsername("root"));
	    
	}
	
	//TEST WITH ROOT
	
	@Test
    public void successRootReadOwnFile() {
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(), "ToniPlainFileTest");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/toni/ToniPlainFileTest");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
    }
	
	@Test
    public void successRootReadFileFromOtherUser() {
		ReadFileService service;
		sessionRoot.setPath("/home/pica/");
		service = new ReadFileService(sessionRoot.getToken(), "PicaPlainFileTest");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/pica/PicaPlainFileTest");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
    }
	
	@Test
    public void successRootReadOwnLink() {
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(), "ToniLinkPlainFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkPlainFile");
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
		
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());      
    }
	
	@Test
    public void successRootReadLinkFromOtherUser() {
		ReadFileService service;
		sessionRoot.setPath("/home/pica/");
		service = new ReadFileService(sessionRoot.getToken(), "PicaLinkPicaFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/pica/PicaLinkPicaFile"); 
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());     
    }
	
	@Test
	public void successRootReadLinktoOtherUserFile() {
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(), "ToniLinkPicaFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkPicaFile"); 
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());     
    }
	
	@Test
    public void successRootReadOwnApp() {
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(), "ToniApp");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/toni/ToniApp"); 
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());       
    }
	
	@Test
    public void successRootReadAppFromOtherUser() {
		ReadFileService service;
		sessionRoot.setPath("/home/pica/");
		service = new ReadFileService(sessionRoot.getToken(), "PicaApp");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/pica/PicaApp"); 
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());       
    }
	

	@Test
	public void successRootPermissionDeniedPlainFile(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(),"ToniPlainFileTestNoPrem");
		service.execute();

		PlainFile pf = (PlainFile) md.getFileByPath("/home/toni/ToniPlainFileTestNoPrem");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}
	
	@Test
	public void successRootPermissionDeniedApp_1(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(),"ToniAppNoPrem");
		service.execute();

		PlainFile pf = (PlainFile) md.getFileByPath("/home/toni/ToniAppNoPrem");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}
	
	@Test
	public void successRootPermissionDeniedApp_2(){
		ReadFileService service;
		sessionRoot.setPath("/home/anib/");
		service = new ReadFileService(sessionRoot.getToken(),"AnibApp");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/anib/AnibApp");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}

	@Test
	public void successRootPermissionDeniedLink_1(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(),"ToniLinkPlainFileNoPrem");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkPlainFileNoPrem");
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}
	
	@Test
	public void successRootPermissionDeniedLink_2(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(),"ToniLinkAnibFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkAnibFile");
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}
	
	@Test
	public void successRootPermissionDeniedLink_3(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni/");
		service = new ReadFileService(sessionRoot.getToken(),"ToniLinkNoPremPlainFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkNoPremPlainFile");
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}
	
	@Test
	public void successRootPermissionDeniedLink_4(){
		ReadFileService service;
		sessionRoot.setPath("/home/anib/");
		service = new ReadFileService(sessionRoot.getToken(),"AnibLinkAnibFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/anib/AnibLinkAnibFile");
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void successRootDirectory(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni");
		service = new ReadFileService(sessionRoot.getToken(), "ToniFolder");
		service.execute();
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void successRootLinkDirectory(){
		ReadFileService service;
		sessionRoot.setPath("/home/toni");
		service = new ReadFileService(sessionRoot.getToken(), "ToniLinkDirectory");
		service.execute();
	}
	
	
	//TESTS WITH AN USER
	
	@Test
    public void successReadOwnFile() {
		ReadFileService service;
		session.setPath("/home/toni/");
		service = new ReadFileService(session.getToken(), "ToniPlainFileTest");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/toni/ToniPlainFileTest");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
    }
	
	@Test
    public void successReadFileFromOtherUser() {
		ReadFileService service;
		session.setPath("/home/pica/");
		service = new ReadFileService(session.getToken(), "PicaPlainFileTest");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/pica/PicaPlainFileTest");
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());
    }
	
	@Test
    public void successReadOwnLink() {
		ReadFileService service;
		session.setPath("/home/toni/");
		service = new ReadFileService(session.getToken(), "ToniLinkPlainFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkPlainFile");
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
		
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());      
    }
	
	@Test
    public void successReadLinkFromOtherUser() {
		ReadFileService service;
		session.setPath("/home/pica/");
		service = new ReadFileService(session.getToken(), "PicaLinkPicaFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/pica/PicaLinkPicaFile"); 
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());     
    }
	
	@Test
	public void successReadLinktoOtherUserFile() {
		ReadFileService service;
		session.setPath("/home/toni/");
		service = new ReadFileService(session.getToken(), "ToniLinkPicaFile");
		service.execute();
		
		PlainFile link = (PlainFile) md.getFileByPath("/home/toni/ToniLinkPicaFile"); 
		String file = link.getText();
		PlainFile pf = (PlainFile) md.getFileByPath(file);
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());     
    }
	
	@Test
    public void successReadOwnApp() {
		ReadFileService service;
		session.setPath("/home/toni/");
		service = new ReadFileService(session.getToken(), "ToniApp");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/toni/ToniApp"); 
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());       
    }
	
	@Test
    public void successReadAppFromOtherUser() {
		ReadFileService service;
		session.setPath("/home/pica/");
		service = new ReadFileService(session.getToken(), "PicaApp");
		service.execute();
		
		PlainFile pf = (PlainFile) md.getFileByPath("/home/pica/PicaApp"); 
        assertEquals("Invalid content on PlainText", pf.getText(), service.result());       
    }
	

	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedPlainFile(){
		ReadFileService service;
		session.setPath("/home/toni");
		service = new ReadFileService(session.getToken(),"ToniPlainFileTestNoPrem");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedApp_1(){
		ReadFileService service;
		service = new ReadFileService(session.getToken(),"ToniAppNoPrem");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedApp_2(){
		ReadFileService service;
		session.setPath("/home/anib");
		service = new ReadFileService(session.getToken(),"AnibApp");
		service.execute();
	}

	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedLink_1(){
		ReadFileService service = new ReadFileService(session.getToken(),"ToniLinkPlainFileNoPrem");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedLink_2(){
		ReadFileService service = new ReadFileService(session.getToken(),"ToniLinkAnibFile");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedLink_3(){
		ReadFileService service = new ReadFileService(session.getToken(),"ToniLinkNoPremPlainFile");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedLink_4(){
		ReadFileService service;
		session.setPath("/home/anib");
		service = new ReadFileService(session.getToken(),"AnibLinkAnibFile");
		service.execute();
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void successDirectory(){
		ReadFileService service = new ReadFileService(session.getToken(), "ToniFolder");
		service.execute();
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void successLinkDirectory(){
		ReadFileService service = new ReadFileService(session.getToken(), "ToniLinkDirectory");
		service.execute();
	}
	
	@Test( expected = FileNotFoundException.class)
	public void successUnknownFile(){
		ReadFileService service = new ReadFileService(session.getToken(), "desconhecido");
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void invalidToken(){
		ReadFileService service = new ReadFileService(-1, "ToniPlainFileTest");
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionRootExpired(){
		ReadFileService service = new ReadFileService(sessionRoot.getToken(), "PicaPlainFileTest");
		sessionRoot.setLastAccess(session.getLastAccess().minusMinutes(15));
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionRootInactive(){
		ReadFileService service = new ReadFileService(sessionRoot.getToken(), "PicaPlainFileTest");
		sessionRoot.setActive(false);
		service.execute();
	}

	@Test( expected = InvalidTokenException.class)
	public void sessionExpired(){
		ReadFileService service = new ReadFileService(session.getToken(), "ToniPlainFileTest");
		session.setLastAccess(session.getLastAccess().minusHours(3));
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void sessionInactive(){
		ReadFileService service = new ReadFileService(session.getToken(), "ToniPlainFileTest");
		session.setActive(false);
		service.execute();
	}

}
