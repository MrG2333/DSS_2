/**
 * @author George Stoian
 * The MUDServerInterfaceImplementation heavily modifies the ShoutServerMainLine from the
 * RMI practical
 * 
*/

package cs3524.solutions.mud;

import java.rmi.Naming;
import java.lang.SecurityManager;
import java.rmi.server.UnicastRemoteObject;


import java.net.InetAddress;


public class MUDServerMainLine{

    public static void main(String args[])
    {
        if(args.length < 2)
        {
            String s = "Use: java ShoutServerMainLine <registry_port> <server_port>";
            System.err.print(s);
            return;
        }
        try {
            String hostname = (InetAddress.getLocalHost()).getCanonicalHostName() ;
            int registryport = Integer.parseInt( args[0] ) ;
            int serverport = Integer.parseInt( args[1] );
        
            // We need to specify a security policy that gives the Java
            // Virtual Machine permission to contact DNS servers and 
            // interact with other JVMs via Internet protocols.
            // You can specify the policy at the command line using
            // -Djava.security.policy=thepolicyIwantyoutouse
            // Or within the program, thus:
            System.setProperty( "java.security.policy", "mud.policy" ) ;
            System.setSecurityManager( new SecurityManager() ) ;
    
            // Incidentally, you can find out what system properties there
            // are in your JVM by doing this:
            // System.getProperties().list( System.out );
    
            // Generate the remote object(s) that will reside on this
            // server.
            MUDServerImpl mudserv = new MUDServerImpl();
            MUDServerInterface mudstub = (MUDServerInterface)UnicastRemoteObject.exportObject( mudserv, serverport );
    
            String regURL = "rmi://" + hostname + ":" + registryport + "/MUD";
                System.out.println("Registering " + regURL );
                Naming.rebind( regURL, mudstub );
    
            // Note the server will not shut down!
        }  
        catch(java.net.UnknownHostException e) {
            System.err.println( "Cannot get local host name." );
            System.err.println( e.getMessage() );
        }
        catch (java.io.IOException e) {
                System.err.println( "Failed to register." );
            System.err.println( e.getMessage() );
            }
    }

}