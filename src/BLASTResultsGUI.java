import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Class to make a GUI to display the data from the database in a JTable.
 *
 * @author Yaris van Thiel
 * @version 1.0
 */


public class BLASTResultsGUI extends JFrame implements ActionListener
{
    private static BLASTResultsGUI frame;
    private static String ORFHeader;
    private JTable resultTable;
    private DefaultTableModel model;
    private JButton showAllResults;

    public static void main(String[] args)
    {
        frame = new BLASTResultsGUI();
        ORFHeader = args.length > 0 ? args[0] : "";
        frame.setTitle(ORFHeader.equals("") ? "All results" : String.format("ORF: %s", ORFHeader));
        frame.setSize(1000, 600);
        frame.createGUI();
        frame.setVisible(true);
    }

    /**
     * This methods creates the GUI with a refresh button and a table.
     * The function of the refreshbutton is get all the data in the database.
     * The table displays the data.
     */
    public void createGUI()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        showAllResults = new JButton("Show all results");
        showAllResults.addActionListener(this);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(showAllResults);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        String[] columnNames = {"Header", "Description", "Bit score", "Query coverage", "E-value", "Identity percentage", "Accession"};
        model = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(model);
        JScrollPane jpane = new JScrollPane(resultTable);
        tablePanel.add(jpane);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(buttonPanel);
        mainPanel.add(tablePanel);

        frame.add(new JScrollPane(mainPanel));
        getResults(ORFHeader);
    }

    /**
     * This method makes a connection to the database where the data is stored.
     * It then retrieves the data that should be showed in the table. It calls the showResult method to
     * display the data.
     */
    public void getResults(String header)
    {
        String user = "ounhs@hannl-hlo-bioinformatica-mysqlsrv";
        String password = "LM6lx70EFxVb";
        String url = "jdbc:mysql://hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com:3306/ounhs?serverTimezone=UTC";
        Connection connection;
        try
        {
            connection = DriverManager.getConnection(url, user, password);
            String command = "select header, defenition, bit_score, query_cov, " +
                    "e_value, ident_perc, accession from protein join protein_attribute pa on " +
                    "protein.name_id = pa.name_id join sequence s on pa.seq_id = s.seq_id";
            if (header.length() > 0)
            {
                command = command.concat(String.format(" where header = '%s'", header));
            }
            command = command.concat(" order by header");
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(command))
            {
                showResult(resultSet);
                connection.close();
            }
            catch (SQLException e)
            {
                JOptionPane.showMessageDialog(frame, "Er is iets mis gegaan tijdens het ophalen van de gegevens uit de database");
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(frame, "Er is iets mis gegaan tijdens het ophalen van de gegevens uit de database");
        }
    }

    /**
     * This method gets a ResultSet containing all of the selected data from the database. It then
     * inserts each row of data to a new row in the table.
     *
     * @param resultSet A ResultSet containg all of the selected data from the database.
     */
    public void showResult(ResultSet resultSet) throws SQLException
    {
        if (!resultSet.next())
        {
            model.addRow(new Object[]{"", "", "", "", "", "", "", ""});
            JOptionPane.showMessageDialog(frame, String.format("Geen resultaten voor %s", ORFHeader));
        }
        else
        {
            do
            {
                // for some reason this starts at 1 instead of 0
                Object[] rowData = {resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getString(6),
                        resultSet.getString(7)};
                model.addRow(rowData);
            }
            while (resultSet.next());
        }

    }

    /**
     * This method is called when the refresh button is pressed. It first removes all the current elements
     * from the table, it then calls the getResults method to get all the data and display it again.
     */
    public void getAllResults()
    {
        frame.setTitle("All results");
        DefaultTableModel table = (DefaultTableModel) resultTable.getModel();
        table.getDataVector().removeAllElements();
        table.fireTableDataChanged();
        getResults("");
    }

    /**
     * This method listenes if the refresh button is pressed. If it's pressed, the refreshResults method will be called.
     *
     * @param actionEvent An ActionEvent from the refresh button
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getSource() == showAllResults) getAllResults();
    }
}
