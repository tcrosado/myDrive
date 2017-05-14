package pt.tecnico.myDrive.service;


import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;

public class XMLImportService extends MyDriveService {
	Element _rootElement;
	
	public XMLImportService(String path) throws JDOMException, IOException{
		File f = new File(path);
		SAXBuilder sax = new SAXBuilder();
		Document doc = sax.build(f);
		_rootElement = doc.getRootElement();
	}
	
	@Override
	protected void dispatch() throws AppMyDriveException {
		AppMyDrive app = AppMyDrive.getInstance();
		app.xmlImport(_rootElement);
	}

}
