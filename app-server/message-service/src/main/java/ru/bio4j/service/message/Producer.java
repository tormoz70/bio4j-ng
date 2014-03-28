package ru.bio4j.service.message;


public interface Producer {

	<T> void publish(T messageObject);

}
