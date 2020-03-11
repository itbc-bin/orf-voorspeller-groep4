public class ORFPredictorGUI {
    public static void main(String[] args) {
        ORF orf1 = new ORF("ATATACAGAGCAGCAGAGGCACGAGCGACAGGCAGCAGAGACGCAAGATTCGCTGGCSTCTSCGGCGCGAGTGATAGTAGAGGCGTCTCGTATATGCGCTC", "forward", "label", 1, 2);
        System.out.println(orf1.getTranslation());
    }
}
