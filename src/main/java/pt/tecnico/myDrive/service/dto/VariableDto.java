package pt.tecnico.myDrive.service.dto;

public class VariableDto{
	
	private String _name;
	private String _value;
	
	public VariableDto(String name,String value){
		_name = name;
		_value = value;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

}
