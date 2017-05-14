package pt.tecnico.myDrive.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import pt.tecnico.myDrive.service.*;
import pt.tecnico.myDrive.service.dto.*;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IntegrationTest extends AbstractServiceTest{
	
	private static final List<VariableDto> list = new ArrayList<VariableDto>();

	protected void populate(){
		VariableDto var = new VariableDto("$HOME", "/home/root");
		list.add(var);
	}
	
	@Test
	public void success() throws Exception{
		
		LoginUserService l = new LoginUserService("root", "***");
		l.execute();
		long token = l.result();
		
		ChangeDirectoryService cwd = new ChangeDirectoryService(token, ".");
		cwd.execute();
		System.out.println(cwd.result());
		assertEquals(cwd.result(), "/home/root");
		
		new CreateFileService(token, "test_plain", "PlainFile", "").execute();
		
		new WriteFileService(token, "test_plain", "Writed document").execute();
		
		ReadFileService read = new ReadFileService(token, "test_plain");
		read.execute();
		assertEquals(read.result(), "Writed document");
		
		ListDirectoryService ls = new ListDirectoryService(token, ".");
		ls.execute();
		assertEquals(ls.result().size(), 3);
		for( FileDto f: ls.result()){
			System.out.println(f.getType()+" "+f.getMask()+" "+f.getSize()+" "+f.getUser()+" "+f.getId()+" "+f.getDate()+" "+f.getName());
		}
		
		new DeleteFileService(token, "test_plain").execute();
		
		ListDirectoryService ls_1 = new ListDirectoryService(token, ".");
		ls_1.execute();
		assertEquals(ls_1.result().size(), 2);
		for( FileDto f: ls_1.result()){
			System.out.println(f.getType()+" "+f.getMask()+" "+f.getSize()+" "+f.getUser()+" "+f.getId()+" "+f.getDate()+" "+f.getName());
		}
		new CreateFileService(token, "App", "App", "print").execute();
		new CreateFileService(token, "Hello_World.txt", "PlainFile", "").execute();
		new WriteFileService(token, "Hello_World.txt", "/home/root/App").execute();
		new MockUp<ExecuteFileService>(){
			@Mock
			String result(){
				return "Hello World!";
			}
		};
		ExecuteFileService exe = new ExecuteFileService(token, "Hello_World.txt", null);
		exe.execute();
		assertEquals(exe.result(), "Hello World!");		
		
		new	MockUp<AddVariableService>() {
			@Mock
			List<VariableDto> result(){
				return list;				
			}
		};
		
		AddVariableService variable = new AddVariableService(token,null,null);
		List<VariableDto> varList = variable.result();
		for(VariableDto var : varList){
			if(var.getName().equals("$HOME")){
				System.out.println(var.getValue());
			}
		}		
		
	}
}
