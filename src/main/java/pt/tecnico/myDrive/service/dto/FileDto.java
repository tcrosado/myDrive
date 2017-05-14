package pt.tecnico.myDrive.service.dto;

public class FileDto {
	private String name;
	private String owner;
	private String mask;
	private String type;
	private String size;
	private String id;
	private String date;
	private String content;
	
	
	public FileDto(String t, String p, String s, String o, String i, String d, String n) {
        this.type = t;
        this.mask = p;
        this.size = s;
        this.owner = o;
        this.id = i;
        this.date = d;
        this.name = n;
	}
	
	public FileDto(String n, String c) {
		this.name=n;
		this.content=c;
	}
    
	public String getName() {
		return name;
	}

	public String getUser() {
		return owner;
	}
	
	public String getMask() {
		return mask;
	}
	
	public String getType() {
		return type;
	}
	
	public String getSize() {
		return size;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getContent(){
		return content;
	}
}
