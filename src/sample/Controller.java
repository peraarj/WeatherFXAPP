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
    private Element e;
    private String weatherAlert;
    Connection conn = null;
    Connection conn1 = null;
    public void buttonclick(ActionEvent actionEvent) throws SQLException {
        String countyCode = code_field.getText();
        ResultSet rs1 = null;
        Statement getStringstate = null;


        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Document doc = Jsoup.connect("https://alerts.weather.gov/cap/wwaatmget.php?x="+countyCode+"&y=1").get();
            String html = doc.html();
            Document newDoc = Jsoup.parse(html, "", Parser.xmlParser());
            Elements xmlTitle = newDoc.select("title");
            System.out.println(xmlTitle.text());
            String desc = xmlTitle.text();
            String sql = "UPDATE WeatherScrape SET AlertDescription ='" + desc + "' WHERE CountyCode ='" + countyCode +"'";
            String sql2 = "SELECT AlertDescription FROM WeatherScrape Where CountyCode='" + countyCode +"'";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            System.out.println("Connected");
            preparedStatement.executeUpdate();
            System.out.println(countyCode + " alert description has been updated");

            getStringstate = conn.createStatement();
            rs1 = getStringstate.executeQuery(sql2);

            if(rs1.next()){
                String outText = rs1.getString(1);
                System.out.println(outText);
                alertOut_lbl.setText(outText);
            }





            System.out.println("end of updated");


            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setContentText("Connection success");
            alert.showAndWait();
        } catch (SQLException e) {
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
                System.out.println("Connection closed");
            }
        }
    }

    private Element toString(Element e) {
        this.e = e;
        return e;
    }

    public void initialize(){

    }
}
