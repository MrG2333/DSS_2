package cs3524.solutions.mud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.rmi.Naming;
import java.rmi.Remote;
import java.lang.SecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MUDClientMainLine
{
    private static MUDServerInterface MUDServer;

    public static void main(String args[]) {
        if (args.length < 3) {
            System.err.println("Use: java MUDClient <host> <registry_port> <callback_port>");
        }
        try {
        String hostname = args[0];
        int registry_port = Integer.parseInt(args[1]);
        int callbackport = Integer.parseInt(args[2]);

        System.setProperty("java.security.policy", "mud.policy");
        System.setSecurityManager(new SecurityManager());

        
            MUDClient mud_client = new MUDClient();
            MUDClientInterface mud_client_stub = (MUDClientInterface) UnicastRemoteObject.exportObject(mud_client,
                    callbackport);


            String regURL = "rmi://" + hostname + ":" + registry_port + "/MUD";
            System.out.println("Looking up " + regURL);
            
            
            MUDServer = (MUDServerInterface) Naming.lookup(regURL);
            MUDClient.startMUDGame(mud_client_stub,MUDServer);
        } catch (java.io.IOException e) {
            System.err.println("I/O error.");
            System.err.println(e.getMessage());
        } catch (java.rmi.NotBoundException e) {
            System.err.println("Server not bound.");
            System.err.println(e.getMessage());
        }
    }
}