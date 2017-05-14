package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // FenixFramework will try automatic initialization when first accessed
    public static void main(String [] args) {
    	try {
    	    init();
    	    for (String s: args) xmlScan(new File(s));
    	    setup();
    	    
    	
    	} finally { FenixFramework.shutdown(); }
    }
    
    @Atomic
    public static void init() { // empty AppMyDrive
        //log.trace("Init: " + FenixFramework.getDomainRoot());
        AppMyDrive.getInstance().cleanup();
    }
    
    @Atomic
    public static void setup(){
    	User root = AppMyDrive.getInstance().getUserByUsername("root");
    	Directory rootDir = AppMyDrive.getInstance().getDir();
	    Directory homeDir = (Directory) rootDir.getChildbyName("home");
	    try{
		    homeDir.createPlainFile(AppMyDrive.getInstance().nextID(),"README", root, null, "Lista de  Utilizadores");
		    Directory bin = AppMyDrive.getInstance().createDirByPath("/usr/local/bin");
		    
		    PlainFile f  = (PlainFile) homeDir.getChildbyName("README");
		    logger.info(f.getPath());
		    logger.info(f.read());
		    bin.remove();
		    xmlPrint();
		    f.remove();
		    for(String s : homeDir.list())
		    	logger.info(s+"\n");
		    
	    }catch (AppMyDriveException e){
	    	logger.info(e.getMessage());
	    }	  
    }
    
    @Atomic
    public static void xmlPrint() {
        //log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
	Document doc = AppMyDrive.getInstance().xmlExport();
	XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	xmlOutput.setFormat(xmlOutput.getFormat().setExpandEmptyElements(true));
	try { xmlOutput.output(doc, new PrintStream(System.out));
	} catch (IOException e) { System.out.println(e); }
    }
    
    @Atomic
    public static void xmlScan(File file) {
        //log.trace("xmlScan: " + FenixFramework.getDomainRoot());
	AppMyDrive app = AppMyDrive.getInstance();
	SAXBuilder builder = new SAXBuilder();
	try {
	    Document document = (Document)builder.build(file);
	    app.xmlImport(document.getRootElement());
	} catch (JDOMException | IOException e) {
	    e.printStackTrace();
	}
    }
}
