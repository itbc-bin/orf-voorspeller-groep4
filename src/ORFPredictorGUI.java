import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ORFPredictorGUI extends JFrame implements ActionListener {
    private static ArrayList<ORF> ORFs = new ArrayList<>();
    private static String sequence;

    public static void main(String[] args) {
        ORFs.add(new ORF("ATATACAGAGCAGCAGAGGCACGAGCGACAGGCAGCAGAGACGCAAGATTCGCTGGCTCTCGGCGCGAGTGATAGTAGAGGCGTCTCGTATATGCGCTC", "+-", "ORF+i", 1, 2));
        String filepath = System.getProperty("user.dir") + File.separator + "Data" + File.separator + "SD.fa";
        File file = new File(filepath);
        readFile(file);
        System.out.println(sequence);
        System.out.println(ORFs.get(0).getTranslation());
    }

    private static void readFile(File file) {
        StringBuilder seqTemp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    assert false;
                    seqTemp.append(line.toUpperCase());
                }
            }
            sequence = seqTemp.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
