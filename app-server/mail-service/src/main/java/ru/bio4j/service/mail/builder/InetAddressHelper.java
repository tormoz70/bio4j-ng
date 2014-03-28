package ru.bio4j.service.mail.builder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InetAddressHelper {

	private static Logger LOG = LoggerFactory.getLogger(InetAddressHelper.class); 
	
	private InetAddressHelper() {}
	
	public static Inet4Address resolveHost() {
		Inet4Address host = null;
		try {
			Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
			while (enumeration.hasMoreElements()) {
				NetworkInterface networkInterface = enumeration.nextElement();
				if (!networkInterface.isLoopback()) {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (address instanceof Inet4Address) {
							host = (Inet4Address) address;
							break;
						}
					}
				} 
			}
		} catch (Exception e) {
			LOG.warn("Ooops ... ", e);
		}
		return host;
	}
	
}
