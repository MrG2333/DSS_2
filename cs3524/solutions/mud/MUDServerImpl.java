/**
 * @author George Stoian
 * The MUDServerInterfaceImplementation heavily modifies the ShoutServerImpl from the
 * RMI practical
 * 
*/

package cs3524.solutions.mud;

import cs3524.solutions.mud.MUDServerInterface;

import java.rmi.RemoteException;
import java.util.*;

public class MUDServerImpl implements MUDServerInterface {

    private MUD MUDInstance;
    private Map<String, MUD> Muds = new HashMap<>();
    private static Map<String, List<String>> userItems = new HashMap<String, List<String>>();
    //player_name,
    private static Map<String, MUDClientInterface> clients = new HashMap<String, MUDClientInterface>();
    private int maxUsers = 10;
    private int maxMuds = 5;

    public MUDServerImpl() {
        Muds.put("Map1", new MUD("mymud.edg", "mymud.msg", "mymud.thg"));
        Muds.put("Map2", new MUD("mymud1.edg", "mymud1.msg", "mymud1.thg"));
        Muds.put("Map3", new MUD("mymud2.edg", "mymud2.msg", "mymud2.thg"));
    }

    /**
     * List the available MUDs.
     */

    public String listMudsAvailable() {
        String muds;
        muds = Muds.keySet().toString();
        return muds;
    }

    /**
     * First time joining a MUD Imposes the max number of players per MUD
     * 
     * @throws RemoteException
     */
    public String joinMUD(String name_of_mud, String player_name) throws RemoteException {
        
        if(!Muds.containsKey(name_of_mud))
        {
            return "Mud does not exist";
        }

        String answer;
        answer = logMUDs(player_name);
        String start_location = getStartLocation(name_of_mud);
       

        if (!Muds.get(name_of_mud).users.containsKey(player_name)) {
            Muds.get(name_of_mud).userItems.put(player_name, new ArrayList<String>());
            addPlayerThing(start_location, player_name,name_of_mud);
            Muds.get(name_of_mud).users.put(player_name, start_location);
            answer = "Accepted " + answer;
            return answer;
        } else {
            return ("Accepted Player already reagistered");
        }

    }
    public boolean mudExists(String mud_name)
    {
        if(Muds.containsKey(mud_name)) {
            return true;
        }
        return false;
    }



    /**
     * Log client Interface
     * 
     * @throws RemoteException
     */

    public void logClientInterface(String player_name, MUDClientInterface cli,String name_of_mud, String start_location) throws RemoteException
    {
        clients.put(player_name, cli);
        playerEntersLocation(player_name ,name_of_mud, start_location);
    }

    public void playerEntersLocation(String player_name,String mud_name, String location) throws RemoteException
    {
        String message = "Player "+player_name+" has entered the location";
        
        for (Map.Entry<String,String> entry : Muds.get(mud_name).users.entrySet())  
        {   
           
            if(entry.getValue().equals(location) && !entry.getKey().equals(player_name))
            {
                clients.get(entry.getKey()).receiveMessage(message);
            }
        }
    } 
    


    /**
     * Change the current MUD
     */

    public String switchMUD(String new_mud, String player_name, String current_mud) {

        String location;

        location = Muds.get(current_mud).users.get(player_name);
        String aux_player_name = "user:" + player_name;
        if (Muds.get(new_mud).users.containsKey(player_name)) {
            Muds.get(current_mud).delThing(location, aux_player_name);
            
            addPlayerThing(Muds.get(new_mud).users.get(player_name), player_name,new_mud);
            return "Switched MUD";
        } else {
            return "First join mud before switching";
        }

    }

    /**
     * Quite the Game.
     * 
     * It removes the player from all the MUDs on the server.
     */

    public void quitGame(String player_name) {
        for (Map.Entry<String, MUD> maps : Muds.entrySet()) {
            for (Map.Entry<String, String> players : maps.getValue().users.entrySet()) {
                if (player_name.equals(players.getKey())) {
                    String aux_player_name = "user:" + player_name;
                    String player_location = players.getValue();
                    for (int i = 0; i < maps.getValue().userItems.get(player_name).size(); i++) {
                        maps.getValue().createThing(player_location, maps.getValue().userItems.get(player_name).get(i));
                    }
                    maps.getValue().delThing(player_location, aux_player_name);
                    maps.getValue().users.remove(player_name);
                }
            }
        }
    }

    public void quitCurrentGame(String player_name, String mud_name) {
        String location;
        location = Muds.get(mud_name).users.get(player_name);
        String item;
        // Drop items on the floor
        for (int i = 0; i < Muds.get(mud_name).userItems.get(player_name).size(); i++) {
            item = Muds.get(mud_name).userItems.get(player_name).get(i);
            Muds.get(mud_name).createThing(location, item);
        }
        player_name = "user:" + player_name;
       
        Muds.get(mud_name).delThing(location, player_name);
        Muds.get(mud_name).users.remove(player_name);
        Muds.get(mud_name).userItems.remove(player_name);
    }

    public String getStartLocation(String mud_name) {
        return Muds.get(mud_name).startLocation();
    }

    public String getLocationInfo(String mud_name, String player_location) {
        return Muds.get(mud_name).getVertex(player_location).toString();
    }

    public void addPlayerThing(String player_location, String player_name,String mud_name) {
        Muds.get(mud_name).users.put(player_name, player_location);
        player_name = "user:" + player_name;
        Muds.get(mud_name).createThing(player_location, player_name);
    }

    public String movePlayer(String player_location, String direction, String player_name,String mud_name)
            throws RemoteException {
        
        String aux_player_name = "user:" + player_name;
        String location = Muds.get(mud_name).moveThing(player_location, direction, aux_player_name);
        
       
        Muds.get(mud_name).users.replace(player_name, location);
       
        
        playerEntersLocation(player_name,mud_name, location);
        return location;
    }

    public String pickObject(String object, String location, String player_name, String mud_name)
            throws RemoteException {
        if (Muds.get(mud_name).thingExists(location, object)) {
            Muds.get(mud_name).userItems.get(player_name).add(object);
            Muds.get(mud_name).delThing(location, object);
            broadcastObjectPicked(object, mud_name, player_name);
            return "Success";
        }
        return "Fail";
    }

    public void broadcastObjectPicked(String object, String mud_name, String player_name) throws RemoteException
    {
        String message = "Player "+player_name+" has picked "+ object;
        
        for (Map.Entry<String,String> entry : Muds.get(mud_name).users.entrySet())  
        {   
           
            if(!entry.getKey().equals(player_name))
            {
                clients.get(entry.getKey()).receiveMessage(message);
            }
        }
    }

    public String addNewMUD(String mud_name) {
        if (maxMuds > Muds.size()) {
            Muds.put(mud_name, new MUD("mymud.edg", "mymud.msg", "mymud.thg"));
            return "Success";
        } else {
            return "Max number of MUDs reached";
        }
    }

    /**
     * Log muds. Checks if there is space for new users
     */
    public String logMUDs(String player_name) {
        if (userItems.containsKey(player_name)) {
            String an = "Logged as " + player_name;
            return an;
        } else {
            if (maxUsers > userItems.size()) {
                return "New user";
            } else {
                return "Not enough space on Server";
            }
        }
    }

    /**
     * Lists the items in the inventory
     */
    public String listItemsPlayer(String mud_name, String player_name) {
        String answer;

        answer = Muds.get(mud_name).userItems.get(player_name).toString();
        return answer;
    }

    /**
     * Get current player locaiton.
     */
    public String getCurrentLocation(String mud_name, String player_name) {
        return Muds.get(mud_name).users.get(player_name);
    }

    public void sendMessageToClient(MUDClientInterface client, String message) throws RemoteException
    {
        client.receiveMessage(message);
    }

    public boolean userExists(String user_name, String mud_name)
    {
        if(Muds.get(mud_name).users.containsKey(user_name))
        {
            return true;
        }
        return false;
    }

    public String listUsersMUD(String mud_name)
    {
        String answer;
        answer = Muds.get(mud_name).users.keySet().toString();
        return answer;
    }

    public void messagePlayer(String player_name, String message) throws RemoteException
    {
        clients.get(player_name).receiveMessage(message);
    }

    public void messsageEveryone(String player_name, String message) throws RemoteException
    {
        for (Map.Entry<String,MUDClientInterface> entry : clients.entrySet())
        {   
           
            if(!entry.getKey().equals(player_name))
            {
                entry.getValue().receiveMessage(message);
            }
        }
    }
}