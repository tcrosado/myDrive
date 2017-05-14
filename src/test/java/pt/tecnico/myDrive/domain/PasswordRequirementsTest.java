package pt.tecnico.myDrive.domain;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.tecnico.myDrive.exceptions.InvalidPasswordException;

public class PasswordRequirementsTest extends AbstractServiceTest{

	AppMyDrive _app;
	String _invalidPassword;
	String _validPassword;

	
	protected void populate(){
		_app = AppMyDrive.getInstance();
		_invalidPassword = "test";
		_validPassword = "validPassword";
		
	}

	@Test( expected = InvalidPasswordException.class)
	public void ShortInvalidPasswordTest() {
		User usr = new User(_app, "TestUser", "Test", _invalidPassword, "rw-dr---", "/home/TestUser");
	}
	
	@Test
	public void ValidPassword(){
		User usr = new User(_app,"ValidUser","ValidUser",_validPassword,"rw-dr---","/home/ValidUser");
		assertEquals("Password invalida",_validPassword,usr.getPassword());
	}

}
