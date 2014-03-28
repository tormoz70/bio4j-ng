package ru.bio4j.service.message;

import java.io.Serializable;

public interface MessageAccumulator {
	
	void storeMessage(int key, Serializable message);
	
	Serializable drain(int key);
	
	int getMessageFetchLimitCount();
}
