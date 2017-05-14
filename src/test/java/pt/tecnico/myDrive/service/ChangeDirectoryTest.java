package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidOperationException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.IsNotDirectoryException;

public class ChangeDirectoryTest extends AbstractServiceTest {
	
	Directory _homeDir1;
	String _actualDir1;
	Directory _childDir1;
	Login _sessao1;
	Login _sessao2; 
	AppMyDrive _md;
	Directory _childDir2;
	Directory _homeDir2;
	
	
	protected void populate(){
		
		_md = AppMyDrive.getInstance();
		
		new User(_md, "Pedro", "Pedro", "00000000", "rwxdr-x-", "/home/Pedro/");
		
		User user1 = _md.getUserByUsername("Pedro");
		
		_homeDir1 = user1.getHomeDir();
		
		_sessao1 = new Login(user1);
		
		_actualDir1 = _md.getFileByPath(_sessao1.getPath()).getPath();
		
		_childDir1 = _homeDir1.createDir("diretorioFilho", _md.nextID(), user1, user1.getMask());

		new User(_md, "Miguel", "Miguel", "11111111", "rwxd----", "/home/Miguel/");
		
		User user2 = _md.getUserByUsername("Miguel");
		
		_homeDir2 = user2.getHomeDir();
		
		_sessao2 = new Login(user2);

		_childDir2 = _homeDir2.createDir("filhoMiguel", _md.nextID(), user2, "--------");
		
		_childDir1.createApp(_md.nextID(), "AppText", user1, user1.getMask(), "ola");
		
		_childDir1.createLink(_md.nextID(), "Link", user1, user1.getMask(), "/home/Pedro/diretorioFilho/AppText");
		
		_childDir1.createPlainFile(_md.nextID(), "PlainFileText", user1, user1.getMask(), "AppText");
	}
	
	
	@Test
	public void sucessChangeToChildbyAbsolutePath(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro");
		service = new ChangeDirectoryService(_sessao1.getToken(), "/home/Pedro/diretorioFilho");
		service.execute();
		
		
		String dir = service.result();
		assertEquals("Invalid Actual Dir", dir, _childDir1.getPath());
	}
	
	@Test
	public void sucessChangeToChildbyRelativePath(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro");
		service = new ChangeDirectoryService(_sessao1.getToken(), "diretorioFilho");
		service.execute();
		
		
		String dir = service.result();
		assertEquals("Invalid Actual Dir", dir, _childDir1.getPath());
	}
	
	@Test
	public void sucessChangeToFather(){
		
		ChangeDirectoryService service;
		service = new ChangeDirectoryService(_sessao1.getToken(), "..");
		_sessao1.setPath("/home/Pedro/diretorioFilho");
		service.execute();
		
		
		String dir = service.result();
		assertEquals("Invalid Actual Dir", dir, _childDir1.getDir().getPath()); 
	}
	
	@Test
	public void sucessChangeToDot(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro");
		service = new ChangeDirectoryService(_sessao1.getToken(), ".");
		service.execute();
		
		
		String dir = service.result();
		assertEquals("Invalid Actual Dir", dir, _actualDir1);
	}
	
	@Test( expected = FileNotFoundException.class)
	public void changeToInexistentDir(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro");
		service = new ChangeDirectoryService(_sessao1.getToken(), "/home/Pedro/none");
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void changeToDirWithoutDirPermission(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro");
		service = new ChangeDirectoryService(_sessao2.getToken(), "/home/Miguel/filhoMiguel");
		service.execute();
	}
	
	@Test( expected = IsNotDirectoryException.class)
	public void invalidChangetoPlainFile(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro/diretorioFilho");
		service = new ChangeDirectoryService(_sessao1.getToken(), "/home/Pedro/diretorioFilho/PlainFileText");
		service.execute();
	}
	
	@Test( expected = IsNotDirectoryException.class)
	public void invalidChangetoApp(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro/diretorioFilho");
		service = new ChangeDirectoryService(_sessao1.getToken(), "/home/Pedro/diretorioFilho/AppText");
		service.execute();
	}
	
	@Test( expected = IsNotDirectoryException.class)
	public void invalidChangetoLink(){
		
		ChangeDirectoryService service;
		_sessao1.setPath("/home/Pedro/diretorioFilho");
		service = new ChangeDirectoryService(_sessao1.getToken(), "/home/Pedro/diretorioFilho/Link");
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void invalidToken(){
		ChangeDirectoryService service = new ChangeDirectoryService(-1, "/home/Pedro/diretorioFilho");
		service.execute();
	}
}
