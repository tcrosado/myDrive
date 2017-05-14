package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mockit.Mock;
import mockit.MockUp;
import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;

public class ExecAssociantionTest extends AbstractServiceTest{
	
	Login session;
	
	@Override
	protected void populate() {
		
		AppMyDrive md = AppMyDrive.getInstance();
		User u = new User(md, "toni", "TONI", "TONIENORME", "rw-dr---", "/home/toni/");
		session = new Login(u);
		new PlainFile(md.nextID(), u.getHomeDir(), "ToniPlainFileTest.txt", u, "rwx-----", "Toni plain file");
		
	}
	
	@Test
    public void successRootReadOwnFile() {
		new	MockUp<ExecAssociationService>() {
			@Mock
			String result(){
				return "FAKE";
			}
		};
		ExecAssociationService exe = new ExecAssociationService(session.getToken(), "/home/toni/ToniPlainFileTest.txt", null);
		exe.execute();
		assertEquals(exe.result(), "FAKE");		
	}
	
	

}
