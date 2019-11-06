import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {


    private Socket socket1 = null;
    private Socket socket2 = null;
    private ServerSocket server = null;
    private static Server serverObject = null;
    private static ObjectInputStream inputPlayer1 = null;
    private static ObjectOutputStream outputPlayer1 = null;
    private static ObjectInputStream inputPlayer2 = null;
    private static ObjectOutputStream outputPlayer2 = null;
    
    private static boolean gameOver = false;
    private static String messageString1 = "";
    private static String messageString2 = "";
    private static String messageString3 = "";
    private static String messageString4 = "";

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
    public void waitForPlayer(){
        try {
            while (gameOver == false) {
                socket1 = server.accept();
                System.out.println("Player 1 accepted! Spawning game thread");
                Thread gameThread = new Thread(serverObject);
                gameThread.start();

                socket2 = server.accept();
                System.out.print("Player 2 accepted! Joining game thread");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                System.out.println("Game Closing");
                    server.close();
                    inputPlayer1.close();
                    outputPlayer1.close();
                    inputPlayer2.close();
                    outputPlayer2.close();
                System.out.println("Game Ended.");
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }

    @Override
    public void run() {
        try {
            inputPlayer1 = new ObjectInputStream(socket1.getInputStream());
            outputPlayer1 = new ObjectOutputStream(socket1.getOutputStream());

            Message serverMessage1 = new Message();
            serverMessage1.setMsgType(Message.MSG_REQUEST_INIT);
            serverMessage1.setMsg("Please enter the coordinates for your 6 ships\n2 x Aircraft carrier - 5 squares each\n2 x Destroyers - 3 squares each\n2 x Submarines - 1 square each\nPlease use this format for each ship(## ##)\nThe first coordinate is the start\nThe next coordinate is the direction the ship goes in");
            outputPlayer1.writeObject(serverMessage1);
            outputPlayer1.flush();
            outputPlayer1.reset();

            Message messageObject1 = (Message) inputPlayer1.readObject();
            System.out.println(messageObject1.getMsg());

            while (socket2 == null) {
                System.out.print("");
            }

            outputPlayer2 = new ObjectOutputStream(socket2.getOutputStream());
            inputPlayer2 = new ObjectInputStream(socket2.getInputStream());

            Message serverMessage2 = new Message();
            serverMessage2.setMsgType(Message.MSG_REQUEST_INIT);
            serverMessage2.setMsg("Please enter the coordinates for your 6 ships\n2 x Aircraft carrier - 5 squares each\n2 x Destroyers - 3 squares each\n2 x Submarines - 1 square each\nPlease use this format for each ship(## ##)\nThe first coordinate is the start\nThe next coordinate is the direction the ship goes in");
            System.out.println(serverMessage2.getMsg());
            outputPlayer2.writeObject(serverMessage2);
            outputPlayer2.flush();
            outputPlayer2.reset();

            Message messageObject2 = (Message) inputPlayer2.readObject();
            System.out.println(messageObject2.getMsg());

            while (true) {
                
                serverMessage1.setMsg(messageContent(messageString1, messageString3));
                serverMessage1.setMsgType(Message.MSG_REQUEST_PLAY);
                System.out.println("serverMessage1: " + serverMessage1.getMsg());
                serverMessage1.Ftable = messageObject1.Ftable;
                serverMessage1.Ptable = messageObject1.Ptable;
                outputPlayer1.writeObject(serverMessage1);
                outputPlayer1.flush();
                outputPlayer1.reset();
                
                messageString1 = "";
                messageString2 = "";

                messageObject1 = (Message) inputPlayer1.readObject();

                if (messageObject1.getMsgType() == Message.MSG_RESPONSE_PLAY) {
                    messageString3 = "";
                    messageString4 = "";
                    String coordinate = messageObject1.getMsg();
                    if (messageObject2.Ftable.checkIfHit(coordinate)) {
                        boolean sunk = messageObject2.Ftable.sunkenShips(coordinate);
                        String shipAt = messageObject2.Ftable.getShipAt(coordinate);

                        messageObject2.Ftable.insertHit(coordinate, "X");
                        messageObject1.Ptable.insertHit(coordinate, "X");

                        if (sunk) {
                            messageString1 = "You sunk the opponents " + checkingShip(shipAt);
                            messageString2 = "The opponent sank your " + checkingShip(shipAt);
                        }
                    } else {
                        messageString1 = "";
                        messageString2 = "";
                        messageObject1.Ptable.insertHit(coordinate, "O");
                    }
                }

                if (messageObject2.Ftable.gameIsOver()) {
                    break;
                }

                serverMessage2.setMsg(messageContent(messageString4, messageString2));
                serverMessage2.setMsgType(Message.MSG_REQUEST_PLAY);
                serverMessage2.Ftable = messageObject2.Ftable;
                serverMessage2.Ptable = messageObject2.Ptable;
                outputPlayer2.writeObject(serverMessage2);
                outputPlayer2.flush();
                outputPlayer2.reset();

                messageObject2 = (Message) inputPlayer2.readObject();

                if (messageObject2.getMsgType() == Message.MSG_RESPONSE_PLAY) {
                    String coordinate = messageObject2.getMsg();
                    if (messageObject1.Ftable.checkIfHit(coordinate)) {
                        boolean sunk = messageObject1.Ftable.sunkenShips(coordinate);
                        String shipLetter = messageObject1.Ftable.getShipAt(coordinate);

                        messageObject1.Ftable.insertHit(coordinate, "X");
                        messageObject2.Ptable.insertHit(coordinate, "X");
                        if (sunk) {
                            messageString4 = "You sunk the opponents " + checkingShip(shipLetter);
                            messageString3 = "The opponent sank your " + checkingShip(shipLetter);
                        }
                    } else {
                        messageString4 = "";
                        messageObject2.Ptable.insertHit(coordinate, "O");
                    }
                }

                if (messageObject1.Ftable.gameIsOver()) {
                    break;
                }
            }

            if (messageObject2.Ftable.gameIsOver()) {
                serverMessage1.setMsg("Game Over! You win");
                serverMessage1.setMsgType(Message.MSG_REQUEST_GAME_OVER);
                serverMessage1.Ftable = messageObject1.Ftable;
                serverMessage1.Ptable = messageObject1.Ptable;
                outputPlayer1.writeObject(serverMessage1);
                outputPlayer1.flush();
                outputPlayer1.reset();

                serverMessage2.setMsg("Game Over! You lose");
                serverMessage2.setMsgType(Message.MSG_REQUEST_GAME_OVER);
                serverMessage2.Ftable = messageObject2.Ftable;
                serverMessage2.Ptable = messageObject2.Ptable;
                outputPlayer2.writeObject(serverMessage2);
                outputPlayer2.flush();
                outputPlayer2.reset();
                
                gameOver  = true;
            } else if (messageObject1.Ftable.gameIsOver()) {
                serverMessage1.setMsg("Game Over! You lose");
                serverMessage1.setMsgType(Message.MSG_REQUEST_GAME_OVER);
                serverMessage1.Ftable = messageObject1.Ftable;
                serverMessage1.Ptable = messageObject1.Ptable;
                outputPlayer1.writeObject(serverMessage1);
                outputPlayer1.flush();
                outputPlayer1.reset();

                serverMessage2.setMsg("Game Over! You win");
                serverMessage2.setMsgType(Message.MSG_REQUEST_GAME_OVER);
                serverMessage2.Ftable = messageObject2.Ftable;
                serverMessage2.Ptable = messageObject2.Ptable;
                outputPlayer2.writeObject(serverMessage2);
                outputPlayer2.flush();
                outputPlayer2.reset();
                
                gameOver = true;
            }

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private String messageContent(String msg1, String msg2){
        String messageContent = null;
        if(msg1.length() == 0 && msg2.length() == 0){
            messageContent = msg1 + msg2;
        }
        else if(msg1.length() > 0 && msg2.length() == 0){
            messageContent = msg1;
        }
        else if(msg1.length() == 0 && msg2.length() > 0){
            messageContent = msg2;
        }
        else if(msg1.length() > 0 && msg2.length() > 0 ){
            messageContent = msg1 + " and " + msg2;
        }
        return messageContent;
    }

    private String checkingShip(String sunkenShip) {
        switch (sunkenShip) {
            case "A":
                sunkenShip = "Aircraft";
                break;
            case "D":
                sunkenShip = "Destroyer";
                break;
            case "S":
                sunkenShip = "Submarine";
                break;
            default:
                break;
        }
        return sunkenShip;
    }

    public static void main(String[] args) {
        serverObject = new Server(5000);
        serverObject.waitForPlayer();
    }
}
