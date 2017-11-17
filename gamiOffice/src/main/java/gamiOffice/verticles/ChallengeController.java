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
	//MqttHelper MqttClient;
	
	@Override
	public void start(){
		//get DB connections
		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			//initialize the challenge
			challenge = loadChallenge("");
			List<Activity> activities = new LinkedList<>(challenge.getWeight().keySet());
			//deploy activity controller for each of the activity
			vertx.executeBlocking(future -> {
	      // first, retrieve token
				for(Activity activity: activities){
					vertx.deployVerticle(new ActivityController(challenge, activity));
					System.out.println("Deploying Controler for activity [" + activity.getName() + "]");
				}
				future.complete("All Activity Controller Deployment complete");
				}, res -> {
					
	    });
		});
	}
	
	@Override
	public void stop(){
		
	}
	
	public Challenge loadChallenge(String Id){
		//retrieve the information based on challengeId
		String queryChallenge = "";
		List<JsonObject> challengeRes = dbHelper.select(queryChallenge);
		JsonArray arr = new JsonArray(challengeRes);
		JsonObject challenge = arr.getJsonObject(0);
		String challengeId = challenge.getString("");
		String challengeName = challenge.getString("");
		Challenge chall = new Challenge(challengeName, challengeId);
		
		String queryWeight = "";
		List<JsonObject> weightRes = dbHelper.select(queryWeight);
		String queryScore = "";
		List<JsonObject> UserRes = dbHelper.select(queryScore);
		
		chall.setWeight(weightRes);
		chall.setEmployeeInvolved(UserRes);
		
		return chall;
	}

}
