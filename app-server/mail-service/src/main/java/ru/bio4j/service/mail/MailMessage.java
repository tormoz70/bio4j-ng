package ru.bio4j.service.mail;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class MailMessage implements Serializable {

	private String subject;
	
	private String[] to; 
	
	private HashMap<String, Object> context = new HashMap<>();
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public void addToContext(String field, Object value) {
		context.put(field, value);
	}
	
	public Map<String, Object> getContext() {
		return context;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + Arrays.hashCode(to);
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
		MailMessage other = (MailMessage) obj;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (!Arrays.equals(to, other.to))
			return false;
		return true;
	}

}
