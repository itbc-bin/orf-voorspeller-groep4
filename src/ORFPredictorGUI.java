public class ORFPredictorGUI {
    public static void main(String[] args) {
        ORF orf1 = new ORF("ATATACAGAGCAGCAGAGGCACGAGCGACAGGCAGCAGAGACGCAAGATTCGCTGGCTCTCGGCGCGAGTGATAGTAGAGGCGTCTCGTATATGCGCTC", "forward", "label", 1, 2);
        System.out.println(orf1.getTranslation());
    }
}
