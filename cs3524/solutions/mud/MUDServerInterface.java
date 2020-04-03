/**
 * @author George Stoian
 * The MUDServerInterface heavily modifies the ShotServerInterface from the
 * RMI practical
 * 
*/

package cs3524.solutions.mud;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;


public interface MUDServerInterface extends Remote
{
    String getStartLocation(String current_MUD_name) 
    throws RemoteException;
    
    String getLocationInfo(String mud_name, String player_location) 
    throws RemoteException;

    void addPlayerThing(String player_location, String player_name, String mud_name)
    throws RemoteException;

    String movePlayer(String player_location, String direction, String player_name, String mud_name)
    throws RemoteException;

    public String pickObject(String object,String location, String player_name, String current_MUD_name) 
    throws RemoteException;

    public String dropObject(String object,String location, String player_name, String current_MUD_name) 
    throws RemoteException;

    public String listMudsAvailable() 
    throws RemoteException;
    

    public String joinMUD(String mud_to_join,String player_name)
    throws RemoteException;

    public void quitGame(String player_name)
    throws RemoteException;

    public String switchMUD(String switch_mud,String player_name, String current_mud_nam)
    throws RemoteException;

    public String addNewMUD(String name_new_mud)
    throws RemoteException;

    public void quitCurrentGame(String player_name,String mud_name)
    throws RemoteException;

    public String listItemsPlayer(String current_MUD_name, String player_name) 
    throws RemoteException;

    public String logMUDs(String player_name)
    throws RemoteException;
    
    public String getCurrentLocation(String mud_name, String player_name)
    throws RemoteException;

    public void logClientInterface(String player_name, MUDClientInterface mud_client_stub, String player_name2, String player_location)
    throws RemoteException;

    public boolean mudExists(String mud_to_join) 
    throws RemoteException;

    public String listUsersMUD(String current_MUD_name)
    throws RemoteException;

    public boolean userExists(String user_to_message, String current_MUD_name)
    throws RemoteException;

    public void messagePlayer(String user_to_message, String message)
    throws RemoteException;

    public void messsageEveryone(String player_name, String message)
    throws RemoteException;



}
