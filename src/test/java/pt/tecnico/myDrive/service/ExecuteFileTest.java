package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.FileNotFoundException;
import pt.tecnico.myDrive.exceptions.InvalidOperationException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.IsNotAppException;
import pt.tecnico.myDrive.exceptions.IsNotPlainFileException;

public class ExecuteFileTest extends AbstractServiceTest {
	
	AppMyDrive _md;
	Directory _homeDir;
	Directory _childDir;
	Login _sessao;
	PlainFile _plainFile;
	
	@Override
	protected void populate() {
		
		_md = AppMyDrive.getInstance();
		
		new User(_md, "Pedro", "Pedro", "00000000", "rwxdr-x-", "/home/Pedro/");
		
		User user = _md.getUserByUsername("Pedro");
		
		_homeDir = user.getHomeDir();
		
		_sessao = new Login(user);
		
		_childDir = _homeDir.createDir("diretorioFilho", _md.nextID(), user, user.getMask());
		
		_childDir.createApp(_md.nextID(), "AppText", user, user.getMask(), "ola");
		
		_childDir.createApp(_md.nextID(), "AppText2", user, user.getMask(), "olala");
		
		_childDir.createApp(_md.nextID(), "AppTextInvalid", user, "--------", "olaa");
		
		_childDir.createLink(_md.nextID(), "LinkApp", user, user.getMask(), "/home/Pedro/diretorioFilho/AppText");
		
		_plainFile = _childDir.createPlainFile(_md.nextID(), "PlainFileText", user, user.getMask(), "AppText");
		
		_childDir.createLink(_md.nextID(), "LinkDir", user, user.getMask(), "/home/Pedro/diretorioFilho");
		
		_childDir.createLink(_md.nextID(), "LinkInvalidApp", user, user.getMask(), "/home/Pedro/diretorioFilho/AppTextInvalid");
	}
	
	public void succes(){
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "home/diretorio/Filho/PlainFileText", args);
		service.execute();
	}
	
	@Test( expected = InvalidTokenException.class)
	public void invalidToken(){
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(-1, "/home/Pedro/diretorioFilho/AppText", args);
		service.execute();
	}
	
	@Test( expected = FileNotFoundException.class)
	public void executeInexistentFile(){
		_plainFile.setText("/home/Pedro/diretorioFilho/ES");
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "/home/Pedro/diretorioFilho/PlainFileText", args);
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void LinktoForbbidenApp(){
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "/home/Pedro/diretorioFilho/LinkInvalidApp", args);
		service.execute();
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void executeDirectory(){
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "/home/Pedro/diretorioFilho", args);
		service.execute();
	}
	
	@Test( expected = IsNotPlainFileException.class)
	public void executeLinktoDir(){
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "/home/Pedro/diretorioFilho/LinkDir", args);
		service.execute();
	}
	
	@Test( expected = IsNotAppException.class)
	public void executeAppWrongArguments(){
		_plainFile.setText("/home/Pedro/diretorioFilho/LinkDir");
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "/home/Pedro/diretorioFilho/PlainFileText", args);
		service.execute();
	}
	
	@Test( expected = AccessDeniedException.class)
	public void InvalidExecution(){
		String[] args = {"1", "2", "3"};
		ExecuteFileService service = new ExecuteFileService(_sessao.getToken(), "/home/Pedro/diretorioFilho/AppTextInvalid", args);
		service.execute();
	}
}
