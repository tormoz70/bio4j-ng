package ru.bio4j.service.message;
import static ru.bio4j.util.Utils.toPrimitiveInt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ru.bio4j.service.message.jms.JmsServiceConfig;

public class PriorityResolverImpl implements PriorityResolver {
	
	private final Map<String, Integer> priorityMap;
	private final int defPriority;
	private final int[] priorities;
	
	private final static String EMPTY = "";
	private final static int TECH_PRIORITY = 0;
	private final static int BOTTOM_BOUND = 0;
	private final static int TOP_BOUND = 9;
	
	public PriorityResolverImpl(JmsServiceConfig serviceConfig) {
		Map<String, Integer> sourceMap = serviceConfig.getPriority();
		priorityMap = Collections.unmodifiableMap(sourceMap);
		defPriority = serviceConfig.getDefPriority();
		priorities = fillPrioritiesArray();
	}
	
	private int[] fillPrioritiesArray() {
		Set<Integer> sortedset = new TreeSet<>(priorityMap.values());
		return toPrimitiveInt(sortedset);
	}

	private int checkBounds(Integer calculatePriority) {
		if (calculatePriority != null && calculatePriority >= BOTTOM_BOUND && calculatePriority <= TOP_BOUND) {
			return calculatePriority;
		}
		return TECH_PRIORITY;
	}

	private Integer calculatePriority(Object object) {
		String className = getSimpleName(object);
		Integer priority = priorityMap.get(className);
		if (priority != null) {
			return priority;
		} else {
			return defPriority;
		}
	}
	
	public int[] getPrioritiesInDescLessThen(int priority) {
		int position = Arrays.binarySearch(priorities, priority);
		return Arrays.copyOfRange(priorities, 0, position);
	}

	private String getSimpleName(Object object) {
		if (object != null) { return object.getClass().getSimpleName(); }
		return EMPTY;
	}
	
	public int getPriority(Serializable object) {
		return checkBounds(calculatePriority(object));
	}
	
	public int[] getPrioritiesInDesc() {
		return priorities;
	}
	
	public Map<String, Integer> getPriorityMap() {
		return priorityMap;
	}

	public int getDefPriority() {
		return defPriority;
	}
}
