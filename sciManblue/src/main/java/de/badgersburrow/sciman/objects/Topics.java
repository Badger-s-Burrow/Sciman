package de.badgersburrow.sciman.objects;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class Topics implements Serializable{
	
	//
	private ArrayList<String> topics = new ArrayList<String>();
	private int numberOfTopics;
	
	public Topics()
	{
		this.numberOfTopics=0;
	}
		
	//Methoden
    public ArrayList<String> getTopics(){
        return this.topics;
    }
    
    public void addTopic(String newTopic){
        this.topics.add(newTopic);
        this.numberOfTopics++;
    }
    
    public void deleteTopic(int index){
        this.topics.remove(index);
        this.numberOfTopics--;
    }
    
    public String getTopic(int index){
    	return this.topics.get(index);
    }
    
    public void sortTopics(){
        
    }
    
    public int getNumberOfTopics(){
    	return this.numberOfTopics;
    }
    
    public boolean isItem(String topic){
        return this.topics.indexOf(topic) > -1;
    	
    }
    
 }
