package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.service.dto.VariableDto;

public class AddVariableTest extends AbstractServiceTest{
	
	private Login session1;
	private Login session2;
	
	@Override
	protected void populate() {
		AppMyDrive md = AppMyDrive.getInstance();
		
		User u = new User(md, "toni", "TONI", "TONIENORME", "rw-dr---", "/home/toni/");
		session1 = new Login(u);
		User u2 = new User(md, "anib", "Anib", "ANIBPEQUENO", "rw-d----", "/home/anib/");
		session2 = new Login(u2);
		
		session1.addVariable("var1-u", "u.1.1");
		session1.addVariable("var2-u", "u.1.2");
		session2.addVariable("var1-u2", "u.2.1");
		session2.addVariable("var2-u2", "u.2.2");
	}
	
	private String conteudo(List<VariableDto> list, String name){
		for(VariableDto v: list){
			if(v.getName().equals(name)){
				return v.getValue();
			}
		}return null;
	}
	
	@Test
	public void successAddVar(){
		
		AddVariableService service = new AddVariableService(session1.getToken(), "var3_u", "u.1.3");
		service.execute();
		
		assertEquals("Wrong Value", conteudo(service.result(), "var3_u"), "u.1.3");
		assertEquals("Wrong Number of Vars", 3, service.result().size());
	}

	@Test
	public void successChangeVariable(){
		String var_value = null;
		for(Variable v : session1.getVarList()){
			if(v.getName().equals("var1-u")){
				var_value = v.getValue();
			}
		}
			
		AddVariableService service = new AddVariableService(session1.getToken(), "var1-u", "u.1.3");
		service.execute();

		assertEquals("Wrong Value", conteudo(service.result(), "var1-u"), "u.1.3");
		assertFalse(var_value.equals("u.1.3"));
		assertEquals("Wrong Number of Vars", 2 , service.result().size());
	}
	
	@Test
	public void successChangeVariableNullValue(){
		String var_value = null;
		for(Variable v : session1.getVarList()){
			if(v.getName().equals("var1-u")){
				var_value = v.getValue();
			}
		}
			
		AddVariableService service = new AddVariableService(session1.getToken(), "var1-u", null);
		service.execute();

		assertEquals("Wrong Value", conteudo(service.result(), "var1-u"), null);
		assertFalse(var_value.equals(null));
		assertEquals("Wrong Number of Vars", 2 , service.result().size());
	}
}
