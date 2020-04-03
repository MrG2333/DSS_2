
/**
 * @author George Stoian
 *
*/
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

public class MUDClient implements MUDClientInterface{

    private static String player_location;
    private static MUDServerInterface MUDServer;
    private static String current_MUD_name;
    private static String player_name;
    private static boolean game_on = true;
    private static BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
    private static MUDClientInterface client_stub;

    public MUDClient() throws RemoteException {
    }


    public static void startMUDGame(MUDClientInterface mud_client_stub, MUDServerInterface mud_server) throws IOException {
        MUDServer = mud_server;
        client_stub = mud_client_stub;
        initialSetup();
        infoCurrentLocation();
        while (game_on) {
            try {
                System.out.println("");
                String choice = user_input.readLine().toLowerCase();
                
                doPlayerChoice(choice);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void doPlayerChoice(String choice) throws IOException {
        String[] split_choice = choice.split(" ");
        String command = split_choice[0];
        switch (command) {

            case "":
                clearTerminal();
                infoCurrentLocation();

                break;
            case "move":
                String direction = split_choice[1];
                player_location = MUDServer.movePlayer(player_location, direction, player_name,current_MUD_name);
                infoCurrentLocation();
                break;

            case "pick":// -1 if the object does not exist
                String obj_to_pick = split_choice[1];
                pickUpThing(obj_to_pick);
                break;
            case "drop":
                String obj_to_drop = split_choice[1];
                dropThing(obj_to_drop);
                break;
            case "list_users":
                listUsersCurrentLocation();
                break;
               
            case "list_items":
                listItems();
                break;
            case "join_muds":
                joinMuds();
                break;
            case "list_things":
                listThings();
                break;
            case "quit_game":
                quitMUDAndGame();
                break;
            case "switch_mud":
                swtichMud();
                break;
            case "exit_mud":
                exitCurrentMUD();
                break;
            case "new_mud":
                createNewMUD();
            case "help":
                printCommands();
                break;
            case "message_chat":
                messageEveryoneOnMUD();
                break;
            case "message_user":
                messagePlayer();
                break;
            default:
                System.out.println("Wrong command. Type help to show the list of commands.");
        }
    }

    private static void initialSetup() throws IOException {
       
        
        player_name = chosePlayerName();
        joinMuds();
        
        
    }

    private static String chosePlayerName() throws IOException {
        System.out.println("Chose player name:");
        String name = user_input.readLine();
        return name;
    }


    public static void infoCurrentLocation() throws RemoteException {
        System.out.println(MUDServer.getLocationInfo(current_MUD_name,player_location));
    }

    /**
     * Addds object from ground to inventory. Removes it from the map.
     * 
     */
    public static void pickUpThing(String object) throws RemoteException {
        String answer = MUDServer.pickObject(object, player_location,player_name,current_MUD_name);
        System.out.println(answer);
    }

    /**
     * Drop item
     */

    public static void dropThing(String object) throws RemoteException
    {
        String answer = MUDServer.dropObject(object, player_location, player_name,current_MUD_name);
        System.out.println(answer);
    }

    /**
     * List only users at location.
     *
     */
    public static void listUsersCurrentLocation() throws RemoteException {
        String summary = MUDServer.getLocationInfo(current_MUD_name, player_location);
        String[] split_summary = summary.split(" ");
        ArrayList<String> users_location = new ArrayList<String>();
        for (String s : split_summary) {
            if (s.contains("user")) {
                String[] get_user = s.split(":");
                users_location.add(get_user[1]);
            }
        }
        Iterator i = users_location.iterator();
        while (i.hasNext()) {
            System.out.print(i.next() + " ");
        }
    }

    /**
     * List all items in posesion.
     * 
     * @throws RemoteException
     *           

     */
    public static void listItems() throws RemoteException {
        System.out.println(current_MUD_name);
        String answer = MUDServer.listItemsPlayer(current_MUD_name,player_name);
        System.out.println(answer);
    }

    /**
     * List the things at the current location;
     */
    public static void listThings() throws RemoteException {
        String summary = MUDServer.getLocationInfo(current_MUD_name ,player_location);
        String things = summary.split("see:")[1];
        System.out.println(things);
    }

    /**
     * Print all available commands.
     */

    public static void printCommands() throws RemoteException {
        System.out.println("Commadns:");
        System.out.println("move <direction>      --moves the player in the wanted direction");
        System.out.println("pick <object>         --puts the item on the ground in the player's inventory");
        System.out.println("drop <item>           --Drop an item from your inventory");
        System.out.println("list_users            --lists other players at the current location");
        System.out.println("list_items            --lists the items in the player's inventory");
        System.out.println("join_muds             --Shows available muds that the player can join for the first time");
        System.out.println("list_things           --Lists things on the ground players or items");
        System.out.println("quit_game             --Quits all MUDs and exists the game");
        System.out.println("exit_mud              --Exists the current MUD");
        System.out.println("switch_mud            --Switches focus from the current MUD");
        System.out.println("message_chat          --Message everyone on current MUD");
        System.out.println("message_user          --Message specific user on MUD");
        System.out.println("help                  --Prints this command list");
        
    }

    /**
     * Shows what muds are running on the server
     * 
     * @throws RemoteException
     */
    public static void listMuds() throws RemoteException {
       String muds;
        muds = MUDServer.listMudsAvailable();
        System.out.println(muds);

    }

    /**
     * Check if MUD exists
     * 
     */
    public static boolean existsMUD(String mud_name) throws RemoteException
    {
        String muds;
        muds = MUDServer.listMudsAvailable();
        muds = muds.replace(",", "");
        muds = muds.replace("[","");
        muds = muds.replace("]", "");
        String[] split_muds = muds.split(" ");
        List<String> wordList = Arrays.asList(split_muds);  
        if(!wordList.contains(mud_name))
        {
            System.out.println("MUD does not exist");
            return false;
        }
        return true;
    }
    
    /**
     * Select a MUD to join.
     * 
     * @throws IOException
     */

    public static void joinMuds() throws IOException {
        System.out.println("Chose a MUD to join");
        System.out.println("Available MUDs");
        //ADD check if MUD exists
        
        listMuds();
        String mud_to_join = user_input.readLine();
        String answer_join;
        
        while(!MUDServer.mudExists(mud_to_join))
        {
            System.out.println("Insert a MUD that exists");
            mud_to_join = user_input.readLine();
        }


        answer_join = MUDServer.joinMUD(mud_to_join,player_name);
        
        
        if(answer_join.contains("Accepted"))
        {
            
            current_MUD_name = mud_to_join;
            player_location = MUDServer.getStartLocation(current_MUD_name);
            MUDServer.logClientInterface(player_name, client_stub,current_MUD_name,player_location);
            System.out.println(answer_join);
        }
        else 
        {
            System.out.println(answer_join);
        }
    }

    /**
     * Quit everything. When quiting like this all items are droped and and the
     * player is delted from all MUD instances.
     * 
     */
    public static void quitMUDAndGame() throws RemoteException {
        MUDServer.quitGame(player_name);
        System.exit(0);
    }

    /**
     * Switch MUD
     * 
     * @throws IOException
     */
    public static void swtichMud() throws IOException {
        System.out.println("Available MUDs");
        listMuds();
        System.out.println("Chose MUD");
        String switch_mud = user_input.readLine();
        if(existsMUD(switch_mud)){
            
            String answer;
            answer = MUDServer.switchMUD(switch_mud,player_name, current_MUD_name);
            System.out.println(answer);
            if(answer.equals("Switched MUD"))
            {
                player_location = MUDServer.getCurrentLocation(current_MUD_name, player_name);
                current_MUD_name = switch_mud;
            }
            
        }
        
    }

    /**
     * Exit current MUD dropping items
     * 
     * @throws IOException
     * 
     */
    public static void exitCurrentMUD() throws IOException
    {
        MUDServer.quitCurrentGame(player_name,current_MUD_name);
        System.out.println("switch or join a new mud?");
        String user_answer;
        user_answer = user_input.readLine();
        
        while(!(user_answer.equals("join") || user_answer.equals("switch")))
        {
            System.out.println("Chose between join or switch");
            user_answer = user_input.readLine();
        }
        
        
        if(user_answer.equals("switch"))
        {

            swtichMud();
        }
        else 
        {
            joinMuds();
        }
        
    }

    /**
     * Create new MUD
     * 
     */
    public static void createNewMUD() throws IOException
    {
        System.out.println("Insert the name of the new MUD:");
        String name_new_mud = user_input.readLine();
        String answer;
        answer = MUDServer.addNewMUD(name_new_mud);
        System.out.println(answer);
    }
    

    private static void clearTerminal() {
		System.out.print("\033[H\033[2J");
	}

    public void receiveMessage( String message )
    {
        System.out.println(message);
    }

    /**
     * Message everyone on.
     * 
     * @throws IOException
     */
    private static void messageEveryoneOnMUD() throws IOException
    {
        String message;
        System.out.println("Insert message to send");
        message = user_input.readLine();
        MUDServer.messsageEveryone(player_name,message);
    }
    

    private static void messagePlayer() throws IOException
    {
        
        String user_to_message;
        String message;

        System.out.println("Select user on MUD to message");
        System.out.println(MUDServer.listUsersMUD(current_MUD_name));
        user_to_message = user_input.readLine();
        while(!MUDServer.userExists(user_to_message,current_MUD_name))
        {
        System.out.println("Insert a user that exists");
        System.out.println("Available users:"+MUDServer.listUsersMUD(current_MUD_name));
        user_to_message = user_input.readLine();
        }
    
        System.out.println("Insert message to send");
        message = user_input.readLine();
        MUDServer.messagePlayer(user_to_message,message);
    }

}