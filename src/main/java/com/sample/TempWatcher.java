package com.sample;

import java.io.IOException;
import java.util.Map;

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
	private FactHandle officeFireHandle = null;
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
		// Sets up rabbitmq queue
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
			//System.out.println(" [x] Received '" + message + "'");
			
			evaluate(message);
        }		
	}
	
	
	private void initRuleEngine() {
        KieServices ks = KieServices.Factory.get();
	    KieContainer kContainer = ks.getKieClasspathContainer();
    	kSession = kContainer.newKieSession("ksession-rules");		
	}
	
	
	private void evaluate(String msg) {
		int temp = 0;
		
		try {
			
			// Get temp from rabbitmq message.
			temp = Integer.parseInt(msg);
		}
		catch (NumberFormatException ex) {
			// Ignore
		}
		
		System.out.println(" [x] temp: " + temp);
		
		if (temp > THRESHOLD) {
			//System.out.println(" [x] FIRE!!!");
			
			// A Fact Handle is an internal engine reference to the inserted instance and 
			// allows instances to be retracted or modified at a later point in time. With 
			// the fires now in the engine, once fireAllRules() is called, the alarm is 
			// raised and the respective sprinklers are turned on. 
			
			// @@ TODO: Get room name from message
			
			String roomName = "office";
			Room room = name2room.get(roomName);
			Fire officeFire = new Fire(room);
			officeFireHandle = kSession.insert(officeFire);
		}
		else {
			// This results in the sprinklers being turned off, the alarm being cancelled, 
			// and eventually the health message is printed again.
			if (officeFireHandle != null) {
				kSession.delete(officeFireHandle);
				officeFireHandle = null;
				//System.out.println(" [x] Fire extinguished !!!");
			}
		}

		kSession.fireAllRules();
	}
}
