
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BattleShipTable implements Serializable {

    /* Constants*/
    //Size of each type of ship
    static final int AIRCRAFT_CARRIER_SIZE = 5;
    static final int DESTROYER_SIZE = 3;
    static final int SUBMARINE_SIZE = 1;

    //symbols use on the board
    /*
	   "A": Aircraft
	   "D": Destroyer
	   "S": Submarine
	   
	   "X": Hit
	   "O": Miss
	   "Z": default value
     */
    static final String AIRCRAFT_CARRIER_SYMBOL = "A";
    static final String DESTROYER_SYMBOL = "D";
    static final String SUBMARINE_SYMBOL = "S";
    static final String HIT_SYMBOL = "X";
    static final String MISS_SYMBOL = "O";
    static final String DEFAULT_SYMBOL = "Z";

    String[][] table = null;

    Set<String> aCarrier = null;
    Set<String> aCarrier2 = null;
    Set<String> destroyer = null;
    Set<String> destroyer2 = null;
    int totalNumShips = 6;

    // constructor 
    public BattleShipTable() 
    {
        this.table = new String[10][10];
        //set default values
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                this.table[i][j] = "Z";
            }
        }
    }

    /*convert alpha_numeric to the X and Y coordinates*/
    private int[] AlphaNumerictoXY(String alpha_coordinates) throws NumberFormatException {
        //get the alpha part
        int[] ret = new int[2];
        ret[0] = this.helperAlphaToX(alpha_coordinates.charAt(0));
        //get the numeric part
        ret[1] = Integer.parseInt(alpha_coordinates.substring(1));
        return ret;
    }

    private int helperAlphaToX(char alpha) {
        return (int) alpha - (int) 'A';
    }

    private String XYToAlphaNumeric(int[] xy) {
        return "" + ((char) (xy[0] + (int) 'A')) + "" + xy[1];
    }
    //print out the table

    public String toString() {
        String ret = new String();
        System.out.println("    0   1   2   3   4   5   6   7   8   9  ");
        for (int i = 0; i < 10; ++i) {
            ret = ret + "" + (char) ((int) 'A' + i) + " | ";
            for (int j = 0; j < 10; ++j) {
                ret = ret + this.table[i][j] + " | ";
            }
            ret = ret + "\n";
        }
        return ret;
    }
    
    public boolean checkIfHit(String x1){
        int[] xy = this.AlphaNumerictoXY(x1);
        String valueAtCoor = this.table[xy[0]][xy[1]];
        
        return !valueAtCoor.equals("Z") && !valueAtCoor.equals("X") && !valueAtCoor.equals("O");
    }
    
   public String getShipAt(String x1){
        int[] xy = this.AlphaNumerictoXY(x1);
        String letter = this.table[xy[0]][xy[1]];
        return letter;
    }
    
    public boolean sunkenShips(String x1){
        
        boolean shipIsSunk = false;
        int[] xy = this.AlphaNumerictoXY(x1);
        String letter = this.table[xy[0]][xy[1]];
        
        if(aCarrier.size() > 0 && aCarrier.contains(x1)){
            aCarrier.remove(x1);
            if(aCarrier.isEmpty()){
                shipIsSunk = true;
                totalNumShips--;
            }
        }
        else if(aCarrier2.size() > 0 && aCarrier2.contains(x1)){
            aCarrier2.remove(x1);
            if(aCarrier2.isEmpty()){
                shipIsSunk = true;
                totalNumShips--;
            }
        }
        else if(destroyer.size() > 0 && destroyer.contains(x1)){
            destroyer.remove(x1);
            if(destroyer.isEmpty()){
                shipIsSunk = true;
                totalNumShips--;
            }
        }
        else if(destroyer2.size() > 0 && destroyer2.contains(x1)){
            destroyer2.remove(x1);
            if(destroyer2.isEmpty()){
                shipIsSunk = true;
                totalNumShips--;
            }
        }
        else if(letter.equals("S"))
        {
            shipIsSunk = true;
            totalNumShips--;
        }
        
        return shipIsSunk;
    }
    
    public boolean gameIsOver(){
        return totalNumShips == 0;
    }

    public void insertHit(String x1, String s) {
        this.insertSinglePoint(this.AlphaNumerictoXY(x1), s);
    }

    public boolean insertSubmarine(String x1) {
        //check if it can be inserted
        if (this.insertSinglePoint(this.AlphaNumerictoXY(x1), "S")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean insertAirCarrier(String x1, String x2) {
        //check if it can be inserted
        if (this.insertShip(x1, x2, BattleShipTable.AIRCRAFT_CARRIER_SIZE, "A")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean insertDestroyer(String x1, String x2) {
        //check if it can be inserted	
        if (this.insertShip(x1, x2, BattleShipTable.DESTROYER_SIZE, "D")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean insertShip(String x1, String x2, int len, String s) {
        int[] xy1 = this.AlphaNumerictoXY(x1);
        int[] xy2 = this.AlphaNumerictoXY(x2);
        if (!(xy1[0] >= 0 && xy1[0] <= 9 && xy1[1] >= 0 && xy1[1] <= 9)) return false;
        if (!(xy2[0] >= 0 && xy2[0] <= 9 && xy2[1] >= 0 && xy2[1] <= 9)) return false;
        

        if (xy1[0] == xy2[0] && (xy1[1] + 1) == xy2[1]) {// along the x axis
            if (checkAlongXAxis(this.AlphaNumerictoXY(x1), len)) {//insert the battleship
                this.insertAlongXAxis(this.AlphaNumerictoXY(x1), len, s);
                return true;
            } else {//prompt the user again
                return false;
            }
        } else if (xy1[1] == xy2[1] && (xy1[0] + 1) == xy2[0]) {// along the y axis
            if (checkAlongYAxis(this.AlphaNumerictoXY(x1), len)) {//insert the battleship
                this.insertAlongYAxis(this.AlphaNumerictoXY(x1), len, s);
                return true;
            } else {//prompt the user again
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean insertSinglePoint(int[] xy, String s) {
        if (this.table[xy[0]][xy[1]].equals("Z")) {
            this.table[xy[0]][xy[1]] = s;
            return true;
        } else if(!this.table[xy[0]][xy[1]].equals("X")){
            this.table[xy[0]][xy[1]] = s;
            return false;
        }
        
        return false;
    }

    private boolean checkAlongXAxis(int[] xy, int len) {
        if (xy[1] + len > 10) {
            return false;
        }
        for (int j = xy[1]; j < xy[1] + len; ++j) {
            if (!this.table[xy[0]][j].equals("Z")) {
                return false;
            }
        }
        return true;
    }

    private void insertAlongXAxis(int[] xy, int len, String s) {
        Set<String> coordinates = new HashSet<String>();

        for (int j = xy[1]; j < xy[1] + len; ++j) {
            this.table[xy[0]][j] = s;
            coordinates.add(this.XYToAlphaNumeric(xy).charAt(0) + "" + j);
        }

        setShipLocation(coordinates, len);
    }

    private boolean checkAlongYAxis(int[] xy, int len) {
        if (xy[0] + len > 10) {
            return false;
        }
        for (int i = xy[0]; i < xy[0] + len; ++i) {
            if (!this.table[i][xy[1]].equals("Z")) {
                return false;
            }
        }
        return true;
    }

    private void insertAlongYAxis(int[] xy, int len, String s) {
        Set coordinates = new HashSet();

        for (int i = xy[0]; i < xy[0] + len; ++i) {
            this.table[i][xy[1]] = s;
            coordinates.add(this.XYToAlphaNumeric(xy).charAt(0) + "" + i);
        }

        setShipLocation(coordinates, len);
    }
    
    private void setShipLocation(Set<String> coordinates, int lengthOfShip) {
        if (lengthOfShip == BattleShipTable.AIRCRAFT_CARRIER_SIZE) {
            if (aCarrier == null) {
                System.out.println("AC 1");
                aCarrier = coordinates;

            } else if (aCarrier2 == null) {
                System.out.println("AC 2");
                aCarrier2 = coordinates;
            }
        } else if (lengthOfShip == BattleShipTable.DESTROYER_SIZE) {
            if (destroyer == null) {
                System.out.println("D1");
                destroyer = coordinates;
            } else if (destroyer2 == null) {
                System.out.println("D2");
                destroyer2 = coordinates;
            }
        }
    }
}
