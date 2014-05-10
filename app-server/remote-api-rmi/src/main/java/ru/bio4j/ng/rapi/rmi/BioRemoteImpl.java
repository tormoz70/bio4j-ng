package ru.bio4j.ng.rapi.rmi;

import ru.bio4j.ng.rapi.BioRemote;

import java.rmi.RemoteException;

public class BioRemoteImpl implements BioRemote {

    private RemoteAPIService owner;

    public BioRemoteImpl(RemoteAPIService owner) {
        this.owner = owner;
    }

    @Override
    public String process(String userUID, String requestType, String requestBody) throws RemoteException {
        return null;
    }
}
