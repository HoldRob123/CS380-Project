import java.io.*;
import java.util.ArrayList;

// Sandbox to generate cached VINS
public class Sandbox {

    public static void main(String[] args) {
        VINDecoderMain mainSystem = new VINDecoderMain();
        String inputFile = "C:\\Users\\isaka\\OneDrive\\Documents\\GitHub\\cs380project\\src\\main\\java\\textfiles\\vins.txt";
        String outputFile = "vinresults.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                // Example: process VIN to get mock data (replace with actual logic)
                String vin = line;
                ArrayList<Vehicle> list = mainSystem.confirmSearch(line);

                // Space-separated output
                String outputLine = vin + " " + list.toString();

                bw.write(outputLine);
                bw.newLine();
            }

            System.out.println("Space-separated data written to: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
