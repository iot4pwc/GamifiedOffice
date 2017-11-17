package gamiOffice.verticles;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import gamiOffice.components.activities.Activity;
import gamiOffice.components.general.Challenge;
import gamiOffice.components.helper.DBHelper;
import gamiOffice.components.helper.MqttHelper;
import gamiOffice.constants.ConstLib;
import io.vertx.core.AbstractVerticle;

public class ActivityController extends AbstractVerticle implements MqttCallback{
	Challenge challenge;
	DBHelper dbHelper;
	MqttHelper MqttClient;
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
			Set<String> topics = new HashSet<>();
			topics.add(ConstLib.TOPIC_WATERINTAKE);
			MqttClient = new MqttHelper(false);
			MqttClient.subscribe(topics);
		});
	}

	@Override
	public void stop(){

	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message){
		System.out.println(message.getPayload().toString());
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
