package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.AccessDeniedException;
import pt.tecnico.myDrive.exceptions.InvalidPasswordException;
import pt.tecnico.myDrive.exceptions.UserNotFoundException;

public class LoginUserTest extends AbstractServiceTest{
	
	@Override
	protected void populate() {
		AppMyDrive md = AppMyDrive.getInstance();
		new User(md, "toni", "TONI", "TONIENORME", "rw-dr---", "/home/toni/");
	}
	
	@Test
    public void successLoginAllRigth(){
		LoginUserService service;
		service = new LoginUserService("toni", "TONIENORME");
		service.execute();
		
        assertNotNull("User failed log in", service.result());

	}
	
	@Test ( expected = InvalidPasswordException.class)
	public void successLoginWrongPass(){
		LoginUserService service;
		service = new LoginUserService("toni", "errado");
		service.execute();	
	}
	
	@Test ( expected = UserNotFoundException.class)
	public void SuccessLoginUnknow(){
		LoginUserService service;
		service = new LoginUserService("foretsfgo", "errado");
		service.execute();
	}

}
