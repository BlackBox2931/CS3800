import java.net.*;
import java.io.*;
import java.text.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{
    
    private Socket socket = null;
    private Socket socket2 = null;
    private ServerSocket server = null;
    private ObjectInputStream inputPlayer1 = null;
    private ObjectOutputStream outputPlayer1 = null;
    private ObjectInputStream inputPlayer2 = null;
    private ObjectOutputStream outputPlayer2 = null;
    private static Server serverObject = null;
    private boolean gameOver = false;
    private Message messageObject = null;
    private Message messageObject2 = null;
    
    public Server(int port){
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
    
    public void waitForPlayer(){
        System.out.println("Waiting for players to join...");
        try {
            socket = server.accept();
            System.out.println("Player 1 accepted! Spawning game thread");
            setUpGameThread();
            socket2 = server.accept();
            System.out.println("Player2 accepted! Joining game thread");
            while(gameOver == false){
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setUpGameThread(){
        Thread gameThread = new Thread(serverObject);
        gameThread.start();
    }
    
    public static void main(String args[]){
        serverObject = new Server(5000);
        serverObject.waitForPlayer();
        
    }
    
    public void turnByTurn(){
        try {
            while(gameOver == false){
                messageObject.setMsgType(3);
                outputPlayer1.writeObject(messageObject2);
                messageObject = (Message)inputPlayer1.readObject();
                if(messageObject.getMsgType() == 5){
                    gameOver = true;
                    break;
                }
                
                String attackCoordinate = messageObject.getMsg();
                //check ship sunk
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        messageObject = new Message(1);    
        try {
            inputPlayer1 = new ObjectInputStream(socket.getInputStream());
            outputPlayer1 = new ObjectOutputStream(socket.getOutputStream());
            outputPlayer1.writeObject(messageObject);
            System.out.println("Message to Player 1 sent!");
            while(socket2 == null){
                System.out.print("");
            }
            
            messageObject2 = new Message(1);
            messageObject2.setMsgType(1);
            inputPlayer2 = new ObjectInputStream(socket2.getInputStream());
            outputPlayer2 = new ObjectOutputStream(socket2.getOutputStream());
            outputPlayer2.writeObject(messageObject2);
            System.out.println("Message to Player 2 sent!");
            
            messageObject2 = (Message)inputPlayer2.readObject();
            
            turnByTurn();
            
            inputPlayer1.close();
            outputPlayer1.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
