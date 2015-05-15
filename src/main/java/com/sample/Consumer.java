package com.sample;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import com.model.Fire;
import com.model.Room;
import com.model.Temperature;
import com.mq.Listener;
import com.mq.MQHandler;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;


public class Consumer extends Thread {
	
	private MQHandler mq;
	private String queueName;
	private Map<String, Room> name2room; 
	
	public Consumer(KieSession kSession, String queueName, Map<String, Room>name2room) {
		mq = MQHandler.getInstance();
		mq.setkSession(kSession);
		this.queueName = queueName;
		this.name2room = name2room;
	}
	
	@Override
	public void run() {
		try {
			mq.addQueueListener(queueName, new Listener() {

				@Override
				public void process(KieSession kSession, String message) {
					try {
						JSONObject payload = new JSONObject(message);

						String roomName = payload.getString("room");
						Room room = name2room.get(roomName);
						Temperature temp = new Temperature(room, payload.getInt("temp"));
						
						System.out.println(" [*] room: "+ roomName +", temp: "+ temp.getValue());

						// Insert a Fact into the working memory.
						
//						Fire fire = new Fire(room);
//						FactHandle fireHandle = kSession.insert(fire);						
						FactHandle tempHandle = kSession.insert(temp);
						kSession.fireAllRules();

					}
					catch (JSONException ex) {
						System.out.println(" [x] Error while parsing the JSON message.");
						return;
					}
				}
			});
		}
		catch (ShutdownSignalException | ConsumerCancelledException | IOException | InterruptedException e) {
			e.printStackTrace();
		}		
	}
}
