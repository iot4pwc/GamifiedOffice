package gamiOffice.verticles;
import java.util.List;

import gamiOffice.components.general.Challenge;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.components.helper.MqttHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ChallengeController extends AbstractVerticle{
	Challenge challenge;
	DBHelper dbHelper;
	MqttHelper MqttClient;
	
	@Override
	public void start(){
		//get DB connections
		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			//initialize the challenge
			challenge = loadChallenge("");
			//initialize MQTT listeners
			MqttClient.subscribe(topics);
			
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
