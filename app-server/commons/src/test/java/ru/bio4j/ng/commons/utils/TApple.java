package ru.bio4j.ng.commons.utils;

public class TApple {
	private String name;
	private Double wheight;

	public TApple() {
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Double getWheight() {
		return wheight;
	}

	public synchronized void setWheight(Double wheight) {
		this.wheight = wheight;
	}
}
