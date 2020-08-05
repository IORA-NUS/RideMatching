import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class readDriver {
	
 public static void main(String[] args) {
        readDriver readdriver = new readDriver();
        String[][] array = readdriver.read_driver();
 }


 public String[][] read_driver() {
        String[][] result = new String[12018][5];
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream( System.getProperty("user.dir") + "\\RideMatching_2020\\SourceData\\" + "driver_15_16.txt")));

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