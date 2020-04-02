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
        MUDInstance = Muds.get(name_of_mud);
        String answer;
        answer = logMUDs(player_name);
        String start_location = getStartLocation();
        if (!MUDInstance.users.containsKey(player_name)) {
            MUDInstance.userItems.put(player_name, new ArrayList<String>());
            addPlayerThing(start_location, player_name);
            answer = "Accepted " + answer;
            return answer;
        } else {
            addPlayerThing(start_location, player_name);
            return ("Accepted Player already reagistered");
        }

    }

    /**
     * First join on server
     * 
     * @throws RemoteException
     */

    public void firstJoinMuds(String player_name, MUDClientInterface cli) throws RemoteException
    {
        clients.put(player_name, cli);
        clients.get(player_name).receiveMessage("Callback fro the server");
         
    }
    /**
     * Change the current MUD
     */

    public String switchMUD(String new_mud, String player_name) {

        String location;

        location = MUDInstance.users.get(player_name);
        String aux_player_name = "user:" + player_name;
        if (Muds.get(new_mud).users.containsKey(player_name)) {
            MUDInstance.delThing(location, aux_player_name);
            MUDInstance = Muds.get(new_mud);
            addPlayerThing(MUDInstance.users.get(player_name), player_name);
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
        System.out.println("In quit current Game");
        Muds.get(mud_name).delThing(location, player_name);
        Muds.get(mud_name).users.remove(player_name);
        Muds.get(mud_name).userItems.remove(player_name);
    }

    public String getStartLocation() {
        return MUDInstance.startLocation();
    }

    public String getLocationInfo(String location) {
        return MUDInstance.getVertex(location).toString();
    }

    public void addPlayerThing(String player_location, String player_name) {
        MUDInstance.users.put(player_name, player_location);
        player_name = "user:" + player_name;
        MUDInstance.createThing(player_location, player_name);
    }

    public String movePlayer(String player_location, String direction, String player_name) {
        MUDInstance.users.replace(player_name, player_location);
        player_name = "user:" + player_name;
        String location = MUDInstance.moveThing(player_location, direction, player_name);
        return location;
    }

    public String pickObject(String object, String location, String player_name) {
        if (MUDInstance.thingExists(location, object)) {
            MUDInstance.userItems.get(player_name).add(object);
            MUDInstance.delThing(location, object);
            return "Success";
        }
        return "Fail";
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
}