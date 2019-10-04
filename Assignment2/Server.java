 import java.net.*; 
import java.io.*; 
import java.text.*;
import java.time.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server 
{ 
    //initialize socket and input stream 
    private Socket socket = null; 
    private ServerSocket server = null; 
    private InputStream in = null; //things going into server
    private OutputStream out = null; //things going out of server

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String Date(String info, int type){
        String [] fullInfo = info.split(" ");
        String day = fullInfo[1];
        String moment = fullInfo[2];
        String zone = "";
        String forParsing = day + "/" + moment;
        DateFormat utcFormat = new SimpleDateFormat("yy-MM-dd/HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = utcFormat.parse(forParsing);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        DateFormat format = new SimpleDateFormat("MM/dd/yy, hh:mm a");
        
        switch(type){
            case 1:
                zone = "GMT";
                break;
            case 2:
                zone = "PST";
                break;
            case 3:
                zone = "EST";
                break; 
        }
          
        format.setTimeZone(TimeZone.getTimeZone(zone));
        String timeDate = format.format(date);
        return timeDate;
    }
    
    public void response(String path){
        String html = "";
        TimeClient client = new TimeClient("time.nist.gov");
        String info = client.inputStream();
        switch(path){
            case "/time":
                html = "<h2>GMT Date/Time: " + Date(info,1) + "</h2>\r\n" + "</h2>\r\n" + "<h2>EST Date/Time: " + Date(info,3) + "<h2>PST Date/Time: " + Date(info,2) + "</h2>\r\n";
                break;
            case "/time?zone=all":
                html = "<h2>GMT Date/Time: " + Date(info,1) + "</h2>\r\n" + "<h2>EST Date/Time: " + Date(info,3) + "<h2>PST Date/Time: " + Date(info,2) + "</h2>\r\n" + "</h2>\r\n";
                break;
            case "/time?zone=pst":
                html = "<h2>PST Date/Time: " + Date(info,2) + "</h2>\r\n";
                break;
            case "/time?zone=est":
                html = "<h2>EST Date/Time: " + Date(info,3) + "</h2>\r\n";
                break;
            default:
                html = "<h2>Invalid Request</h2>";
                break;
        }
        
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n" + html;
        try {
            out.write(response.getBytes("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public String getRequest(){
        String path = "";
        InputStreamReader listener = null;
        try {
            listener = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(listener);
            String []getRequest = reader.readLine().split(" ");
            path = getRequest[1];
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return path;
    }
    
    public void startServer(){
        while(true){
            try {
                socket = server.accept();
                in = socket.getInputStream();
                out = socket.getOutputStream();
                response(getRequest());
                in.close();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String args[]) 
    { 
        Server server = new Server(5000);
        server.startServer();
    } 
} 
