package cs3524.solutions.mud;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MUDClientInterface extends Remote
{
    //this is used by the server to send a message
    public void receiveMessage( String message )
    throws RemoteException;
    
}
