package com.sample;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;


public class TempWatcher {

	private KieSession kSession = null;
	private FactHandle fireHandle = null;
	private Map<String, Room> name2room = null;
	
	public final static int THRESHOLD = 60;
	public final static String QUEUE_NAME = "ruleengine";
	
	
	public TempWatcher(Map<String, Room> name2room) {
        // Loads up the knowledge base.
		initRuleEngine();
		
		this.name2room = name2room;
	}
	

	public void start() throws IOException, 
						ShutdownSignalException, 
						ConsumerCancelledException,
						InterruptedException {
		// Sets up rabbitmq queue.
    	initMQ();
	}
	
	
	private void initMQ() throws IOException, 
							ShutdownSignalException, 
							ConsumerCancelledException,
							InterruptedException {
		
    	ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages.");
        
        // Since it will push us messages asynchronously, we provide a callback in the form of 
        // an object that will buffer the messages until we're ready to use them. That is what 
        // QueueingConsumer does.
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);

        while (true) {
        	// nextDelivery() blocks until another message has been delivered from the server.
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			//System.out.println(" [*] Received '" + message + "'");
			
			evaluate(message);
        }		
	}
	
	
	private void initRuleEngine() {
        KieServices ks = KieServices.Factory.get();
	    KieContainer kContainer = ks.getKieClasspathContainer();
    	kSession = kContainer.newKieSession("ksession-rules");		
	}
	
	
	private void evaluate(String msg) {
		int temp;
		String roomName;
		
		try {
			// Get temp and room name from rabbitmq message.			
			JSONObject payload = new JSONObject(msg);
			temp = payload.getInt("temp");
			roomName = payload.getString("room");

			System.out.println(" [*] room: "+ roomName +", temp: "+ temp);
		}
		catch (JSONException ex) {
			System.out.println(" [x] Error while parsing the JSON message.");
			return;
		}

		if (temp > THRESHOLD) {
			//System.out.println(" [*] FIRE!!!");
			
			// A Fact Handle is an internal engine reference to the inserted instance and 
			// allows instances to be retracted or modified at a later point in time. With 
			// the fires now in the engine, once fireAllRules() is called, the alarm is 
			// raised and the respective sprinklers are turned on. 
						
			Room room = name2room.get(roomName);
			Fire fire = new Fire(room);
			fireHandle = kSession.insert(fire);
		}
		else {
			// This results in the sprinklers being turned off, the alarm being cancelled, 
			// and eventually the health message is printed again.
			if (fireHandle != null) {
				kSession.delete(fireHandle);
				fireHandle = null;
				//System.out.println(" [*] Fire extinguished !!!");
			}
		}

		kSession.fireAllRules();
	}
}
