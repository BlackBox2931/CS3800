import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

public class Client {

    private static Socket socket = null;
    private static Message outputMessageObject = new Message();
    private static Message inputMessageObject = new Message();
    private static BattleShipTable fTable = null;
    private static BattleShipTable pTable = null;
    private static ObjectInputStream input = null;
    private static ObjectOutputStream output = null;
    private static boolean gameOver = false;
    
    public Client(String host)
    {
        try
        {
            socket = new Socket(host, 5000);
        } catch (IOException ex) 
        {
            ex.printStackTrace();
        }
    }
    
    private static Message choices(Message messageObject) 
    {
        int msgType = messageObject.getMsgType();
        System.out.println(messageObject.getMsg());

        switch (msgType) 
        {
            case Message.MSG_REQUEST_INIT:
                messageObject.Ftable = setUpBoard();
                messageObject.Ptable = new BattleShipTable();
                messageObject.setMsg("Player 1 ships have been placed");
                messageObject.setMsgType(Message.MSG_RESPONSE_INIT);
                break;
            case Message.MSG_REQUEST_PLAY:
                fTable = messageObject.Ftable;
                pTable = messageObject.Ptable;
                printTable();
                Scanner userInput = new Scanner(System.in);
                System.out.print("Please enter a coordinate to attack: ");
                String coordinate = userInput.nextLine();

                messageObject.setMsgType(Message.MSG_RESPONSE_PLAY);
                messageObject.setMsg(coordinate);
                break;
            case Message.MSG_REQUEST_GAME_OVER:
                gameOver = true;
                try {
                    input.close();
                    output.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
        }

        return messageObject;
    }

    private static BattleShipTable setUpBoard() 
    {
        BattleShipTable personalTable = new BattleShipTable();

        Scanner userInput = new Scanner(System.in);
        String[] coordinate = null;

        System.out.println("Please enter location of first Aircraft carrier: ");
        coordinate = userInput.nextLine().split(" ");
        personalTable.insertAirCarrier(coordinate[0], coordinate[1]);

        System.out.println("Please enter location of second Aircraft carrier: ");
        coordinate = userInput.nextLine().split(" ");
        personalTable.insertAirCarrier(coordinate[0], coordinate[1]);

        System.out.println("Please enter location of first Destroyer: ");
        coordinate = userInput.nextLine().split(" ");
        personalTable.insertDestroyer(coordinate[0], coordinate[1]);

        System.out.println("Please enter location of second Destroyer: ");
        coordinate = userInput.nextLine().split(" ");
        personalTable.insertDestroyer(coordinate[0], coordinate[1]);

        System.out.println("Please enter location of first Submarine: ");
        coordinate = userInput.nextLine().split(" ");
        personalTable.insertSubmarine(coordinate[0]);

        System.out.println("Please enter location of second Submarine: ");
        coordinate = userInput.nextLine().split(" ");
        personalTable.insertSubmarine(coordinate[0]);

        return personalTable;
    }

    private static void printTable() 
    {
        System.out.println("F-Table:\n" + fTable.toString());
        System.out.println("");
        System.out.println("P-Table:\n" + pTable.toString());
    }
    
    public static void waitForMessage()
    {
        try 
        {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            while (gameOver == false) 
            {
                inputMessageObject = (Message)input.readObject();
                outputMessageObject = choices(inputMessageObject);
                if(gameOver == false)
                {
                    output.writeObject(outputMessageObject);
                    output.flush();
                    output.reset();
                }
            }
        } catch (IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]) 
    {
        Client client = new Client("127.0.0.1");
        client.waitForMessage();
    }

}
