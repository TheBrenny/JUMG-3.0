// passes all the messages to and from the MessageListeners
// Telegraphs need to register with the Postman (its a global/static instance).

package com.thebrenny.jumg.entities.messaging;

import java.util.ArrayList;
import java.util.LinkedList;

import com.thebrenny.jumg.util.Logger;

public class Postman {
	private static Postman POSTMAN_PAT;
	private ArrayList<MessageListener<?>> listeners;
	private LinkedList<Message> messageQueue;
	
	public Postman() {
		if(Postman.POSTMAN_PAT == null) Postman.POSTMAN_PAT = this;
		listeners = new ArrayList<MessageListener<?>>();
		messageQueue = new LinkedList<Message>();
	}
	
	public void queueMessage(Message msg) {
		synchronized(messageQueue) {
			if(this.messageQueue.add(msg)) Logger.log("Message added successfully: " + msg.getUniqueString());
			else Logger.log("Couldn't add message: " + msg.getUniqueString());
		}
	}
	/**
	 * Sends all queued messages to all listeners. It is up to the individual
	 * listeners to identify if they care about the message.
	 * <em>#TotalSecurityLol</em>
	 */
	public void update() {
		synchronized(messageQueue) {
			synchronized(listeners) {
				Message msg;
				while(!messageQueue.isEmpty()) {
					msg = messageQueue.pop();
					for(MessageListener<?> listener : listeners) listener.handleMessage(msg);
				}
			}
		}
	}
	
	public boolean registerListener(MessageListener<?> listener) {
		synchronized(listeners) {
			Logger.log("Registering listener: " + listener.toString());
			return listeners.add(listener);
		}
	}
	
	public static Postman getInstance() {
		if(Postman.POSTMAN_PAT == null) new Postman();
		return Postman.POSTMAN_PAT;
	}
}
