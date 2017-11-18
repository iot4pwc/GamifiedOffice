package gamiOffice.components.activities;

import java.util.List;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.general.User;
import io.vertx.core.json.JsonObject;

public abstract class Activity {
	//store general info of current activity
	String ActivityName;
	double GainPerUnit;
	List<String> topicSet; 

	int UnitOfMeasure;

	public String getName(){
		return this.ActivityName;
	}

	public abstract void updateScore(Challenge challenge, JsonObject payload);
}
