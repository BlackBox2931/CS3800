import java.net.*; 
import java.io.*; 

public class TimeClient 
{ 
    private Socket socket = null; 
    private InputStream input = null; //things going into client

    public TimeClient(String host){
        try {
            socket = new Socket(host, 13);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){ 
        TimeClient client = new TimeClient("time.nist.gov");
        String returnTime = client.inputStream();
    } 
        
    public String inputStream(){
        int number = 0;
        String totalReturn = "";
        try {
            input = socket.getInputStream();
            while((number = input.read()) != - 1){
                char letter = (char)number;
                totalReturn = totalReturn + letter;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return totalReturn;
    }
} 
