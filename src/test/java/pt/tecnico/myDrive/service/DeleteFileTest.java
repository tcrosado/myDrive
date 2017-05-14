package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.service.*;

public class DeleteFileTest extends AbstractServiceTest {
	
	private Directory _rootDir;
	private Login _session;
	private Login _deniedSession;
	private Login _guestSession;
	private AppMyDrive _amd;
	public void populate(){
		_amd = AppMyDrive.getInstance();		
		User root = _amd.getUserByUsername("root");
		String defaultMask = root.getMask();
		Directory contentDir;
		Directory bin;
		User deniedUser = new User(_amd, "denied", "User Denied", "denied78", "rwxdr-x-", "/home/denied");
		
		_deniedSession = new Login(deniedUser);
		_deniedSession.setPath("/");
		
		User guest = _amd.getUserByUsername("nobody");
		Directory guestDir = guest.getHomeDir();
		_guestSession = new Login(guest);
		_guestSession.setPath("/home/nobody");
		
		_session = new Login(root);
		_session.setPath("/");
				
		_rootDir = _amd.getDir();
		
		bin = _rootDir.createDir("bin", _amd.nextID(), root, defaultMask);
		bin.createApp(_amd.nextID(), "ls", root, defaultMask, "list");
		
		contentDir = _rootDir.createDir("havingStuff",_amd.nextID(),root,defaultMask);
				
		contentDir.createPlainFile(_amd.nextID(), "text",root, defaultMask, "text");
				
		contentDir.createApp(_amd.nextID(),"ls", root, defaultMask, "ls");
				
		contentDir.createLink(_amd.nextID(), "link", root, defaultMask, "/bin/ls");
		
		contentDir.createDir("emptyDir",_amd.nextID(),root,defaultMask);
		
		_rootDir.createDir("empty", _amd.nextID(),root, defaultMask);
		
		_rootDir.createPlainFile(_amd.nextID(), "text",root, defaultMask, "text");
		
		_rootDir.createApp(_amd.nextID(),"app", root, defaultMask, "ls");
		
		_rootDir.createLink(_amd.nextID(), "link", root, defaultMask, "/bin/ls");
		
		guestDir.createApp(_amd.nextID(),"guestapp",deniedUser, "rwxdrwxd","lst");
		
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveEmptyDir() {
		
		DeleteFileService service = new DeleteFileService( _session.getToken(),"empty");
		service.execute();
		
		_amd.getFileByPath("/empty");
	
	}

	@Test( expected = FileNotFoundException.class)
	public void successRemoveContentDir(){
		
		DeleteFileService service = new DeleteFileService( _session.getToken(),"havingStuff");
		service.execute();
		
		_amd.getFileByPath("/havingStuff/ls");
		_amd.getFileByPath("/havingStuff");
		
	}

	@Test( expected = FileNotFoundException.class) 
	public void successRemovePlainFile(){
		long token = _session.getToken();
		DeleteFileService service = new DeleteFileService( token,"text");
		service.execute();
		
		_amd.getFileByPath("/text");
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveApp(){
		DeleteFileService service = new DeleteFileService(_session.getToken(),"app");
		service.execute();
		
		_amd.getFileByPath("/app");
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveLink(){
		DeleteFileService service = new DeleteFileService( _session.getToken(),"link");
		service.execute();
		
		_amd.getFileByPath("/link");		
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveNonExistentLink(){
		
		DeleteFileService service = new DeleteFileService(_session.getToken(),"NonExistingLink");
		service.execute();
		
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveNonExistentApp(){
		
		DeleteFileService service = new DeleteFileService(_session.getToken(),"NonExistingApp");
		service.execute();
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveNonExistentPlainFile(){
		
		DeleteFileService service = new DeleteFileService(_session.getToken(),"NonExistingText");
		service.execute();
	}
	
	@Test( expected = FileNotFoundException.class) 
	public void successRemoveNonExistentDirectory(){
		
		DeleteFileService service = new DeleteFileService(_session.getToken(),"NonExistingDir");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedEmptyDirectory(){
		DeleteFileService service = new DeleteFileService(_deniedSession.getToken(),"empty");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedContentDirectory(){
		DeleteFileService service = new DeleteFileService(_deniedSession.getToken(),"havingStuff");
		service.execute();
	}

	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedPlainFile(){
		DeleteFileService service = new DeleteFileService(_deniedSession.getToken(),"text");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedApp(){
		DeleteFileService service = new DeleteFileService(_deniedSession.getToken(),"app");
		service.execute();
	}

	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedLink(){
		DeleteFileService service = new DeleteFileService(_deniedSession.getToken(),"link");
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void invalidSessionDeleteFile(){
		long invalidToken = -20;
		DeleteFileService service = new DeleteFileService(invalidToken,"link");
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void expiredSessionDeleteFile(){
		DateTime lastAccess =_session.getLastAccess();
		_session.setLastAccess(lastAccess.minusHours(4));
		DeleteFileService service = new DeleteFileService(_session.getToken(),"link");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void successPermissionDeniedNobody(){
		DeleteFileService service = new DeleteFileService(_guestSession.getToken(),"guestapp");
		service.execute();
	}
}
