// listens to and handles messages
// interface

package com.thebrenny.jumg.entities.messaging;

public interface MessageListener<T> {
	public void handleMessage(Message message);
	public T getOwner();
}