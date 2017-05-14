package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.tecnico.myDrive.domain.AppMyDrive;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exceptions.AppMyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDto;

public class AddVariableService extends MyDriveService {
	
	private static final Logger logger = LoggerFactory.getLogger(AddVariableService.class);
	private long _token;
	private String _name;
	private String _value;
	private List<VariableDto> _result;
	
	public AddVariableService(long token,String name,String value){
		_token = token;
		_name = name;
		_value = value;
		_result = new ArrayList<VariableDto>();
	}

	@Override
	protected void dispatch() throws AppMyDriveException {
		AppMyDrive app = AppMyDrive.getInstance();
		Login session = app.getSessionByToken(_token);
		if(!(session.canAccess())){
			logger.warn("Invalid Session");
			return;
		}	
		if(_name != null || _value != null){
			session.addVariable(_name, _value);
		}
		List<VariableDto> varlist = new ArrayList<VariableDto>();
		for(Variable var : session.getVarList()){
			VariableDto dto = new VariableDto(var.getName(),var.getValue());
			varlist.add(dto);
		}
		_result = varlist;
	}
	
	public List<VariableDto> result(){
		return _result;
	}
	
	
}
