package ru.bio4j.service.message;

import java.io.Serializable;
import java.util.Map;

public interface PriorityResolver {
	
	Map<String, Integer> getPriorityMap();

	int getDefPriority();

	int getPriority(Serializable object);

	int[] getPrioritiesInDesc();
	
	int[] getPrioritiesInDescLessThen(int priority);
	
}
