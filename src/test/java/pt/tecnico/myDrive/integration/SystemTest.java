package pt.tecnico.myDrive.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.junit.Test;

import pt.tecnico.myDrive.domain.AbstractServiceTest ;
import pt.tecnico.myDrive.presentation.*;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.XMLImportService;

public class SystemTest extends AbstractServiceTest {

    private MdShell sh;

    protected void populate() {
        sh = new MdShell();
        XMLImportService service;
		try {
			service = new XMLImportService("populate.xml");
			service.execute();
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
    }

    @Test
    public void success() {
        new LoginCommand(sh).execute(new String[] { "root" , "***" });
        new ChangeWorkingDirectory(sh).execute(new String[] { "/home" } );
        
        //FIXME
        //new ListCommand(sh).execute(new String[] {  } );
        
        //FIXME
        //new Execute(sh).execute(new String[] { "/home/app" } );

        new WriteCommand(sh).execute(new String[] { "/home/textfile", "ficheiro alterado" } );
        
        new Environment(sh).execute(new String[] { "var1", "valor1" } );
        
        new KeyCommand(sh).execute(new String[] { } );
    }
}
