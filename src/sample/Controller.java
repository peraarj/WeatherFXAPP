package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.sql.*;

public class Controller {
    private static final String USERNAME = "peraarj";
    private static final String PASSWORD = "";
    private static final String CONN_STRING = "jdbc:mysql://www.db4free.net:3306/weatherdb?verifyServerCertificate=false&useSSL=false";

    @FXML
    private Button Button1;

    @FXML
    private Label TitleLbl;

    @FXML
    private Label ZipLbl;

    @FXML
    private Label alertOut_lbl;

    @FXML
    private TextField code_field;


    // Add county box fields and buttons
    @FXML
    private TextField countyCode_addbox;

    @FXML
    private TextField countyName_addbox;

    @FXML
    private Button add_btn;

    // Delete county ox field and button
    @FXML
    private Button del_btn;

    @FXML
    private TextField countyCode_delbox;

    //Status label for program
    @FXML
    private Label status_lbl;

    // random variables needed
    private Element e;
    private String weatherAlert;
    Connection conn = null;
    Connection conn1 = null;
    String countyCode = null;
    String countyName = null;
    // This method is the submit button for the weather alert look up.
    public void buttonclick(ActionEvent actionEvent) throws SQLException {
        countyCode = code_field.getText();
        ResultSet rs1 = null;
        Statement getStringstate = null;

        try {
            status_lbl.setText("Status: Retrieving weather alert update...");
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Document doc = Jsoup.connect("https://alerts.weather.gov/cap/wwaatmget.php?x="+countyCode+"&y=1").get();
            String html = doc.html();
            //System.out.println(doc);
            Document newDoc = Jsoup.parse(html, "", Parser.xmlParser());
            Elements xmlTitle = newDoc.select("title");
            System.out.println(xmlTitle.text());
            String desc = xmlTitle.text();
            String sql = "UPDATE WeatherScrape SET AlertDescription ='" + desc + "' WHERE CountyCode ='" + countyCode +"'";
            String sql2 = "SELECT AlertDescription FROM WeatherScrape Where CountyCode='" + countyCode +"'";
            status_lbl.setText("Status: Connecting to Database...");
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            status_lbl.setText("Status: Connected...");

            preparedStatement.executeUpdate();
            status_lbl.setText("Status: Database Updated...");

            getStringstate = conn.createStatement();
            status_lbl.setText("Status: Querying weather alert...");
            rs1 = getStringstate.executeQuery(sql2);

            status_lbl.setText("Status: Coating data...");
            if(rs1.next()){

                String outText = rs1.getString(1);
                System.out.println(outText);
                alertOut_lbl.setText(outText);
            }

            status_lbl.setText("Status: Query complete. Waiting...");

        } catch (SQLException e) {
            status_lbl.setText("Status: Error!");
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            code_field.setText("");
            if (conn != null) {
                conn.close();
                System.out.println("Connection closed");
            }
        }
    }

    // Method to control the action for deleting records from the data base
    public void clickadd(ActionEvent actionEvent) throws SQLException {
        countyCode = countyCode_addbox.getText();
        countyName = countyName_addbox.getText();
        status_lbl.setText("Status: Connecting...");
        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            String sql = "INSERT INTO WeatherScrape ( CountyName, CountyCode) VALUES ( '" + countyName + "', '" + countyCode + "')";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            status_lbl.setText("Status: Connected...");
            preparedStatement.executeUpdate();



        } catch (SQLException ex) {
            status_lbl.setText("Status: Error!");
            System.err.println(e);
        } finally {
            countyCode_addbox.setText("");
            countyName_addbox.setText("");
            alertOut_lbl.setText("");
            if (conn != null) {
                conn.close();
                status_lbl.setText("Status: New County added. Waiting...");
                System.out.println("Connection closed");

            }
        }
    }

        // Method to controll the action for adding records to the database
        public void clickdel (ActionEvent actionEvent) throws SQLException {
            countyCode = countyCode_delbox.getText();
            status_lbl.setText("Status: Connecting...");
            try {

                conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
                String sql = "DELETE FROM WeatherScrape WHERE CountyCode='" + countyCode + "'";

                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.executeUpdate();
                status_lbl.setText("Status: Connected...");
                status_lbl.setText("Status: Deleting County...");


            } catch (SQLException ex) {
                System.err.println(e);
                status_lbl.setText("Status: Error...");
            } finally {
                countyCode_delbox.setText("");
                status_lbl.setText("Status: Deletion complete. Waiting...");
                alertOut_lbl.setText("");
                if (conn != null) {
                    conn.close();
                    System.out.println("Connection closed");
                }
            }
        }

        public void initialize () {
            status_lbl.setText("");
        }
    }