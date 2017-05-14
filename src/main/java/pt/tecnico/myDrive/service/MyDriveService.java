package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exceptions.*;
import pt.tecnico.myDrive.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

public abstract class MyDriveService{
	private static final Logger logger = LoggerFactory.getLogger(AppMyDrive.class);

	    @Atomic
	    public final void execute() throws AppMyDriveException {
	        dispatch();
	    }

	    static AppMyDrive getMyDrive() {
	        return AppMyDrive.getInstance();
	    }

	    protected abstract void dispatch() throws AppMyDriveException;
	}