package gamiOffice.verticles;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import gamiOffice.components.activities.Activity;
import gamiOffice.components.general.Challenge;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.components.helper.MqttHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class ActivityController extends AbstractVerticle implements MqttCallback{
	Challenge challenge;
	DBHelper dbHelper;
	MqttHelper MqttHelper;
	MqttClient client;
	Activity activity;

	public ActivityController(Challenge challenge, Activity activity){
		this.challenge = challenge;
		this.activity = activity;
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
				client.subscribe(ConstLib.TOPIC_LIGHT);
				client.subscribe(ConstLib.TOPIC_WATERINTAKE);
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("MQTT listener deployed");
		});
	}

	@Override
	public void stop(){

	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message){
		System.out.println(topic + new String(message.getPayload()));
		JsonObject data = new JsonObject(new String(message.getPayload()));
		if(topic.equals(ConstLib.TOPIC_WATERINTAKE) && data.getString("value_key").equals("Tag") && (data.getString("value_content").equals("19414433") || data.getString("value_content").equals("9025298"))){
			System.out.println("[before] user: " + challenge.getRank().get(0).getKey() + "score: " + challenge.getRank().get(0).getValue());
			System.out.println("Begin updating score...");
			activity.updateScore(challenge, data);
			System.out.println("Score updated finished!");
			System.out.println("[after] user: " + challenge.getRank().get(0).getKey() + "score: " + challenge.getRank().get(0).getValue());
		}
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

}
