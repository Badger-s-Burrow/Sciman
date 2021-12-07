package de.badgersburrow.sciman.objects;

import com.google.gson.Gson;

import java.io.Serializable;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class User implements Serializable{

	private String email;
	private String passwordHash;
	private String name;
	private String userId = null;


	public User(String Email, String PasswordHash, String Name){
		this.email = Email;
		this.passwordHash = PasswordHash;
		this.name = Name;

	}
	
	//getter Methods
    public String getEmail(){
        return email;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
    public String getName(){
        return name;
    }
    public String getUserId(){
        return userId;
    }

    //setter Methods
    public void setId(String Id){
        this.userId =Id;
    }

    public String getGson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
