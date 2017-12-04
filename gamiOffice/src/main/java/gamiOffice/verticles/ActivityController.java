package gamiOffice.verticles;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import gamiOffice.components.activities.Activity;
import gamiOffice.components.activities.SittingDuration;
import gamiOffice.components.activities.WaterIntake;
import gamiOffice.components.general.Challenge;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.components.helper.MqttHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActivityController extends AbstractVerticle implements MqttCallback{
	Challenge challenge;
	DBHelper dbHelper;
	MqttHelper MqttHelper;
	MqttClient client;
	Activity activity;
	Logger logger = LogManager.getLogger(ActivityController.class);

	public ActivityController(Challenge challenge, String activity){
		this.challenge = challenge;
		//switch to get singleton activity instance
		switch (activity) {
		case WaterIntake.COMPONENT_CODE:
			this.activity = WaterIntake.getInstance();
			break;
		case SittingDuration.COMPONENT_CODE:
			this.activity = SittingDuration.getInstance();
			break;
		default:
			System.out.println("No matching activity found.");
			break;
		}
	}

	@Override
	public void start(){
		//get DB connections
		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.GAMIFIED_OFFICE);
			future.complete();
		}, response -> {
			//initialize the MQTT topic subscription
			MqttHelper = new MqttHelper(false);
			client = MqttHelper.getAliveClient();
			client.setCallback(this);
			try {
				for(String topic : activity.getTopicSet()){
					client.subscribe(topic);
					logger.info(activity.getName() + " now listening to "+ topic);
				}
			} catch (MqttException e) {
				logger.error("Something went wrong. " + e);
			}
			logger.info("MQTT listener deployed");
		});
	}

	@Override
	public void stop(){

	}

	@Override
	public void messageArrived(String topic, MqttMessage message){
		logger.info(topic + new String(message.getPayload()));
		JsonObject data = new JsonObject(new String(message.getPayload()));
		logger.info("Begin updating score...");
		activity.updateScore(challenge, data);
		logger.info("Score updated finished!");
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		logger.error("Lost connection with MQTT " + arg0.getMessage());

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

}
