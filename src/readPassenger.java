import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class readPassenger {
	
 public static void main(String[] args) {
        readPassenger readpax = new readPassenger();
        String[][] array = readpax.read_passenger();
 }


 public String[][] read_passenger() {
        String[][] result = new String[53916][9]; //47141;;53916;;101057
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream( System.getProperty("user.dir") + "\\RideMatching_2020\\SourceData\\" + "passenger_15_16.txt")));

            int cnt = 0;
            String line = null;
            while ( (line = br.readLine())!=null) {
                   result[cnt] = line.split(",");
                   cnt++;
            }

            
         } catch (FileNotFoundException e) {
           e.printStackTrace();
         } catch (IOException e) {
           e.printStackTrace();
         }
         return result;
       }
}