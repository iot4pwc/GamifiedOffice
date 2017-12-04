package gamiOffice.components.activities;

import java.util.ArrayList;
import java.util.List;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.general.User;
import io.vertx.core.json.JsonObject;

public abstract class Activity {
	//store general info of current activity
	String ActivityName;
	double GainPerUnit;
	List<String> topicSet = new ArrayList<String>(); 

	int UnitOfMeasure;

	public String getName(){
		return this.ActivityName;
	}
	
	public List<String> getTopicSet(){
		return this.topicSet;
	}

	public abstract void updateScore(Challenge challenge, JsonObject payload);
	
	
}
