import java.io.Serializable;

public class Message implements Serializable
{ 
    //message types
    static final int MSG_REQUEST_INIT = 1;//sent from server to client
    static final int MSG_RESPONSE_INIT = 2; //sent from client to server
    static final int MSG_REQUEST_PLAY = 3; //sent from server to client
    static final int MSG_RESPONSE_PLAY = 4;//sent from client to server
    static final int MSG_REQUEST_GAME_OVER = 5; //sent from server to client

    private int msgType=-1;
    private String msg = null;
    private int [] blockBomb = new int[2]; //x, y coordinates of the block on the opponent's board ot be bombed; this is for the MSG_RESPONSE_PLAY message

    public BattleShipTable Ftable = null;//the player's own board (F-board)
    public BattleShipTable Ptable = null;//the player hits and misses on the opponent board (P-board)

    //getters
    public String getMsg(){
            return this.msg;
    }

    public int getMsgType(){
            return this.msgType;
    }
    
    //setters
    public void setMsg(String m){
            this.msg = m;
    }

    public void setMsgType(int type){
            this.msgType = type;
            checkMsg();
    }
    
    public void checkMsg(){
        switch(msgType){
            case MSG_REQUEST_INIT:
                setMsg("Please enter the coordinates for your 6 ships\n2 x Aircraft carrier - 5 squares each\n2 x Destroyers - 3 squares each\n2 x Submarines - 1 square each\nPlease use this format for each ship(## ##)\nThe first coordinate is the start\nThe next coordinate is the direction the ship goes in");
                break;
            case MSG_RESPONSE_INIT:
                break;
            case MSG_REQUEST_PLAY:
                setMsg("Please enter the coordinate to attack:");
                break;
            case MSG_RESPONSE_PLAY:
                break;
            case MSG_REQUEST_GAME_OVER:
                break;
        }
    }

    // constructor
    public Message(int type) 
    { 
        msgType = type;
        checkMsg();
    } 
    
    public Message(){
        
    }
	
} 
