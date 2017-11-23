package gamiOffice.verticles;
import java.util.LinkedList;
import java.util.List;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.components.helper.MqttHelper;
import gamiOffice.components.activities.Activity;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ChallengeController extends AbstractVerticle{
	Challenge challenge;
	DBHelper dbHelper;
	String challengeId;
	
	public ChallengeController(String challengeId){
		this.challengeId = challengeId;
	}
	
	@Override
	public void start(){
		//get DB connections
		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			//initialize the challenge
			challenge = loadChallenge(challengeId);
			List<Activity> activities = new LinkedList<>(challenge.getActivities().values());
			//deploy activity controller for each of the activity
			vertx.executeBlocking(future -> {
	      // first, retrieve token
				for(Activity activity: activities){
					vertx.deployVerticle(new ActivityController(challenge, activity));
					System.out.println("Deploying Controler for activity [" + activity.getClass().getName() + "]");
				}
				future.complete("All Activity Controller Deployment complete");
				}, res -> {
					System.out.println(res.result().toString());
	    });
		});
	}
	
	@Override
	public void stop(){
		
	}
	
	private Challenge loadChallenge(String Id){
		//retrieve the information based on challengeId
		Challenge c = new Challenge("some challenge", Id);
		c.setWeight();
		c.setEmployeeInvolved();
		return c;
	}

}
