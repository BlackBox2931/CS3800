import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    private Socket socket = null;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;
    private Message messageObject = null;
    private BattleShipTable YourTable = new BattleShipTable();
    private BattleShipTable OpponentTable = new BattleShipTable();
    private String[] coordinate = null;
    private boolean gameOver = false;
    
    public Client(String host){
        try {
            socket = new Socket(host, 5000);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void printTable(){
        System.out.println("F-Table:\n" + YourTable.toString());
        System.out.println("");
        System.out.println("P-Table:\n" + OpponentTable.toString());
    }
    
    
    public void turnByTurn(){
        Scanner userInput = new Scanner(System.in);
        String attackCoordinate = null;
        try {
            while(gameOver == false){
                messageObject = (Message)input.readObject();
                System.out.println(messageObject.getMsg());
                attackCoordinate = userInput.nextLine();
                YourTable = messageObject.Ftable;
                OpponentTable = messageObject.Ptable;
                printTable();

                messageObject.setMsgType(4);
                messageObject.setMsg(attackCoordinate);
            }
            //DO GAME OVER
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
    }
    
    public void waitForInitialMessage(){
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            messageObject = (Message)input.readObject();
            
            printTable();
            System.out.println(messageObject.getMsg());
            Scanner userInput = new Scanner(System.in);
            
            System.out.println("Please enter location of first Aircraft carrier: ");
            coordinate = userInput.nextLine().split(" ");
            YourTable.insertAirCarrier(coordinate[0], coordinate[1]);
            
            System.out.println("Please enter location of second Aircraft carrier: ");
            coordinate = userInput.nextLine().split(" ");
            YourTable.insertAirCarrier(coordinate[0], coordinate[1]);
            
            System.out.println("Please enter location of first Destroyer: ");
            coordinate = userInput.nextLine().split(" ");
            YourTable.insertDestroyer(coordinate[0], coordinate[1]);
            
            System.out.println("Please enter location of second Destroyer: ");
            coordinate = userInput.nextLine().split(" ");
            YourTable.insertDestroyer(coordinate[0], coordinate[1]);
            
            System.out.println("Please enter location of first Submarine: ");
            coordinate = userInput.nextLine().split(" ");
            YourTable.insertSubmarine(coordinate[0]);
            
            System.out.println("Please enter location of second Submarine: ");
            coordinate = userInput.nextLine().split(" ");
            YourTable.insertSubmarine(coordinate[0]);
            
            messageObject.Ftable = YourTable;
            messageObject.Ptable = OpponentTable;
            messageObject.setMsgType(2);
            output.writeObject(messageObject);
            printTable();
            turnByTurn();
            
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static void main(String args[]){ 
        Client client = new Client("127.0.0.1");
        client.waitForInitialMessage();
    } 
}
