// is the message. contains message data as well. needs to be serialisable,
// because we need to be able to pass these over the network!
// abstract? class

package com.thebrenny.jumg.entities.messaging;

import java.util.Objects;

import com.thebrenny.jumg.util.StringUtil;
import com.thebrenny.jumg.util.TimeUtil;

public abstract class Message {
	protected MessageListener<?> sender;
	protected MessageListener<?> receiver;
	protected int id;
	protected Object[] data;
	protected long timestamp;
	protected long delay;
	
	public Message(MessageListener<?> sender, MessageListener<?> receiver, int id, Object ... data) {
		this(sender, receiver, id, 0, data);
	}
	public Message(MessageListener<?> sender, MessageListener<?> receiver, int id, long delay, Object ... data) {
		this.sender = sender;
		this.receiver = receiver;
		this.id = id;
		this.timestamp = TimeUtil.getEpoch();
		this.delay = delay;
		this.data = data;
	}
	
	public MessageListener<?> getSender() {
		return sender;
	}
	public MessageListener<?> getReceiver() {
		return receiver;
	}
	public int getID() {
		return id;
	}
	public Object[] getData() {
		return data;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public long getSendingTimestamp() {
		return timestamp + delay;
	}
	
	public String getUniqueString() {
		return "Message:" + id + "@" + hashCode();
	}
	public int hashCode() {
		return Objects.hash(sender, receiver, id, data, timestamp, delay);
	}
	public String toString() {
		return "Message{sender:" + sender + ", receiver:" + receiver + ", id:" + id + ", timestamp:" + timestamp + ", delay:" + delay + ", data:[" + StringUtil.getStringList(data, ", ") + "]}";
	}
}
