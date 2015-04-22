package ru.bio4j.ng.rapi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BioRemote extends Remote {
    String process(String userUID, String requestType, String requestBody) throws RemoteException;
}
