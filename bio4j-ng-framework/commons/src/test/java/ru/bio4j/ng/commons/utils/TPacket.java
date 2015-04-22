package ru.bio4j.ng.commons.utils;


public class TPacket {
	private String name;
	private Double volume;
	private TApple[] apples;

	public TPacket() {
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Double getVolume() {
		return volume;
	}

	public synchronized void setVolume(Double volume) {
		this.volume = volume;
	}

	public synchronized TApple[] getApples() {
		return apples;
	}

	public synchronized void setApples(TApple[] apples) {
		this.apples = apples;
	}

}
