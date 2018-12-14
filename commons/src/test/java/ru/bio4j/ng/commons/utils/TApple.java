package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.model.transport.ABean;

public class TApple {
	private String name;
	private Double wheight;
	private ABean bean;

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

	public ABean getBean() {
		return bean;
	}

	public void setBean(ABean bean) {
		this.bean = bean;
	}
}
