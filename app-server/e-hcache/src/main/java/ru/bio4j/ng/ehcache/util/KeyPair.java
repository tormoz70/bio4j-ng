package ru.bio4j.ng.ehcache.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KeyPair implements Serializable {

	private final Serializable key1;
	private final Serializable key2;
	public KeyPair(Serializable key1, Serializable key2) {
		super();
		this.key1 = key1;
		this.key2 = key2;
	}
	
	public Serializable getKey1() {
		return key1;
	}

	public Serializable getKey2() {
		return key2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key1 == null) ? 0 : key1.hashCode());
		result = prime * result + ((key2 == null) ? 0 : key2.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyPair other = (KeyPair) obj;
		if (key1 == null) {
			if (other.key1 != null)
				return false;
		} else if (!key1.equals(other.key1))
			return false;
		if (key2 == null) {
			if (other.key2 != null)
				return false;
		} else if (!key2.equals(other.key2))
			return false;
		return true;
	}
}
