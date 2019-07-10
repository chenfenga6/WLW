package com.Fate_Project.CoAP;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;
import java.net.URISyntaxException;

public class GETClient {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = null;
        uri = new URI("coap://192.168.8.1:5683/wind");
        CoapClient client = new CoapClient(uri);
        CoapResponse resource = client.get();
        System.out.println("*** "+resource.getCode());
        System.out.println("*** "+resource.getOptions());
        System.out.println("*** "+resource.getPayload());
        System.out.println("*** "+resource.getResponseText());

    }
}
