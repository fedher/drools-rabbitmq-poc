package com.mq;

import java.io.IOException;

import org.kie.api.runtime.KieSession;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;


public class MQHandler {
	
	private String addr = "localhost";
	private String username = "";
	private String password = "";
	
	private KieSession kSession;	
	
	
	private static class Factory {
		private static MQHandler INSTANCE = new MQHandler();
	}
	
	public static MQHandler getInstance() {
		return Factory.INSTANCE;
	}
	
	public void setkSession(KieSession kSession) {
		this.kSession = kSession;
	}
	
	public void setConn(String addr) {
		this.addr = addr;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void addQueueListener(String queueName, Listener callback) 
			throws IOException, 
			ShutdownSignalException, 
			ConsumerCancelledException,
			InterruptedException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(addr);
//		factory.setPassword(password);
//		factory.setUsername(username);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(queueName, false, false, false, null);
		System.out.println(" [*] Waiting for messages.");
		
		// Since it will push us messages asynchronously, we provide a callback in the form of 
		// an object that will buffer the messages until we're ready to use them. That is what
		// QueueingConsumer does.
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);
		
		while (true) {
			// nextDelivery() blocks until another message has been delivered from the server.
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			//System.out.println(" [*] Received '" + message + "'");
			
			callback.process(kSession, message);
		}
	}
	
}
