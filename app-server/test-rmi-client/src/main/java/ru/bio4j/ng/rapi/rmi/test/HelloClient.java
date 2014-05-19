package ru.bio4j.ng.rapi.rmi.test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.model.transport.BioResponse;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.rapi.BioRemote;

/**
 * Client to invoke the RMI service hosted on another JVM running on localhost.
 *
 * @version 
 */
public final class HelloClient {

    private HelloClient() {
        // use Main
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Getting registry");
        Registry registry = LocateRegistry.getRegistry("localhost", 37541);

        System.out.println("Lookup service");
        BioRemote bioRemote = (BioRemote) registry.lookup("bio4j.remote.rmi");

        System.out.println("Invoking RMI ...");

        BioRequestJStoreGet request = new BioRequestJStoreGet();
        request.setBioCode("ekbp.users.list");
        String jsonString = Jsons.encode(request);

        String out = bioRemote.process("tester", "crud.dt.gt", jsonString);

        BioResponse response = Jsons.decode(out, BioResponse.class);
        System.out.println("... the result is: " + response.getBioCode());

        System.out.println(out);
    }

}
