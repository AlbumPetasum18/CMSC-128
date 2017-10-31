import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.*;
//------------------------------files edited : this, Bus_Info, book1,
/*
    For directory tab
    "Records of buses"
 */
//to be added: add button, edit button  refresh button .  Match rownum to index in busInfo make a currentrownum where it is the row being edited. then match it to bus_info
//index to change the data. put it in the observableList and change the table, csv (by matching vars[0] to index), and firebase.
public class DirectoryController implements Initializable {

    @FXML private TableView<Bus_Info> directoryTable;
    @FXML private TableColumn<Bus_Info, String> BusID;
    @FXML private TableColumn<Bus_Info, String> company;
    @FXML private TableColumn<Bus_Info, String> destination;
    @FXML private TableColumn<Bus_Info, String> plate_num;
    @FXML private TableColumn<Bus_Info, String> seat_cap; //seatcap became integer to make editing possible.
    @FXML private TableColumn<Bus_Info, String> type;

    private String currentNewBusID;
    private String currentNewCompany;
    private String currentNewDestination;
    private String currentNewPlateNum;
    private String currentNewSeatCap;
    private String currentNewType;

    private String filename = "C:\\Users\\006104\\Desktop\\128\\src\\main\\resources\\Book1.csv"; // variable created bec. two methods are using the same filename. editBuses and loadBuses
    private DatabaseReference database;
    @FXML private TextField rowNum;//----------------------------------------------------------------rowNum----------------------------------- --------------

    private ObservableList<Bus_Info> Bus_Info;
    private Bus_Info currentBus_Info;
    int ctr2 = 0;
    int ctr = 0; //------------------------------------for the edit names-------------------------------------
    //HBox hbox = new HBox();
    //-----------------------------------------------------------------from add fxml and add controller
    public String toAdd;
    public int rowToAdd;

    // button which switches the scene to Realtime
    public void switchToRealtime(ActionEvent event) throws IOException {
        Parent realtime = FXMLLoader.load(getClass().getResource("fxml/RealtimeView.fxml"));
        Scene realtimeScene = new Scene(realtime);
        realtimeScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(realtimeScene);
    }

    public void switchToReports(ActionEvent event) throws IOException {
        // todo
    }

    public void switchToDirectory(ActionEvent event) throws IOException {
        // todo
    }
    //@FXML private PasswordField password;
   // private String pass = password.toString();
   /* public boolean isValidUser() {
        if (password.toString().equals("pass")) {
            return true;
        }
        return false;
    }*/
  //  @FXML private Text text;
    public void setStringToAdd(String toAdd){
        this.toAdd = toAdd;
    }
    public void setRowToAdd(int rowToAdd){
        this.rowToAdd = rowToAdd;
    }
    public void addData(){
        System.out.println("\n\nni work ang add");
    }
    public void opeanAddData(ActionEvent event) throws IOException{
        Parent realtime = FXMLLoader.load(getClass().getResource("fxml/A.fxml"));
        Scene realtimeScene = new Scene(realtime);
        realtimeScene.getStylesheets().add("style.css");

        Stage window = new Stage();
        window.setScene(realtimeScene);
        window.show();
        //window.setScene(realtimeScene);
        /*try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/A.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }*/
    }
    @FXML public void tableEditable(){  // System.out.println("\n\n"+password.toString()+"\n");
      //  if(isValidUser()) {
        //    text.setData("Press done to close edit mode");
            directoryTable.setEditable(true);  //the meothod for Edit table button
      //  }directoryTable.setEditable(false);
    }

    @FXML public void closeEditMode(){
        directoryTable.setEditable(false);  // method for done edit button
    //    text.setData("Enter Password and press Edit Table to go to edit mode ");
    }
    @FXML public void refresh(){
        directoryTable.setEditable(false);///for the mean time this has  to be controlled by a function. The state should be dynamic.
        BusID.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("BusID"));
        company.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("company"));
        destination.setCellValueFactory(new PropertyValueFactory<Bus_Info, String >("destination"));
        type.setCellValueFactory(new PropertyValueFactory<Bus_Info, String >("type"));
        seat_cap.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("seat_cap"));
        plate_num.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("plate_num"));
        Bus_Info = FXCollections.observableArrayList();
        loadBuses();
        database = FirebaseDatabase.getInstance().getReference();
        BusID.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn()); //creates a textfield (a textfield alone you can write anything but doesn't have any effect when you exit the field.)
        company.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        destination.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        type.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        seat_cap.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());     //once integer but transformed to string for this line.bec this cannot be applied to an int data.
        plate_num.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
      //  loadBuses(filename);
     //   directoryTable.refresh();
    }
    public void initialize(URL location, ResourceBundle resources) {
        refresh(); //setting the table(named refresh coz also used by refreshbutton)
        //user needs to double click on the cell for this to operate.
        BusID.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                event.getRowValue().setBusID(event.getNewValue());
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,1 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\n"+currentNewBusID+"\n this is the row data"+event.getRowValue().toString()); //testing lang ang row num
            }
        });
        company.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,3 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\n"+currentNewCompany+"\n this is the row num "+event.getTablePosition().getRow()); //testing lang ang row num
            }
        });
        destination.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,4 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\n"+currentNewDestination+"\n this is the row num "+event.getTablePosition().getRow()); //testing lang ang row num
            }
        });
        seat_cap.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,5 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\n"+currentNewSeatCap+"\n this is the row num "+event.getTablePosition().getRow()); //testing lang ang row num
            }
        });
        type.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,6 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\n"+currentNewType+"\n this is the row num "+event.getTablePosition().getRow()); //testing lang ang row num
            }
        });
        plate_num.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,2 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\n"+currentNewPlateNum+"\n this is the row num "+event.getTablePosition().getRow()); //testing lang ang row num
            }
        });
        //directoryTable.refresh();
    }
    @FXML public void delete(){//------------------------------------------------delete HERE!!------------------------------------------------------------
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String currentLine;
            StringBuffer sb = new StringBuffer();
            int ctr = 0;// always increments until reaches row num.
            while((currentLine = br.readLine())!= null){ //di maayu duha ka while.
                if(ctr == Integer.parseInt(rowNum.getText())) { //meaning this is the row to be edited.
                    currentLine = "";
                }
                else {//------------------------wynaut ang paggedit sa numbers kay buhaton sa data assigning sa table ? ----------------------try-----------
                    sb.append(currentLine); //we placed each line and also the edited line.
                    sb.append('\n'); //separate lines
                }
                ctr++;
            }
            String inputStr = sb.toString(); //sb is buffer
            br.close();
            System.out.println(inputStr);
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
            //inputStr.split(",\n");
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
        refresh();
    }
    @FXML public void addLine(){ // ------------------------------------need pa sa add ang prompt window or HBOx??--------------------------------
        if(toAdd != null && rowToAdd >= 0)
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String currentLine;
            String stringHolder;
            StringBuffer sb = new StringBuffer();
            int ctr = 0;// always increments until reaches row num.
            while((currentLine = br.readLine())!= null){ //di maayu duha ka while.
                if(ctr == rowToAdd) { //meaning this is the row to be edited. ------------^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^i-edit intawn ni ^^**))______----------
                    stringHolder = currentLine;
                    currentLine = toAdd;
                    sb.append(currentLine); sb.append('\n');
                    sb.append(stringHolder); sb.append('\n');

                }
                else {
                    sb.append(currentLine); //we placed each line and also the edited line.
                    sb.append('\n'); //separate lines
                }
                ctr++;
            }
            String inputStr = sb.toString(); //sb is buffer
            br.close();
            System.out.println(inputStr);
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
            //inputStr.split(",\n");
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
        //loadBuses(filename);
    }
    /*      loads buses from the csv file*/
    public void editCSV(String replace, int row, int col)throws IOException   {
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String currentLine;
            StringBuffer sb = new StringBuffer();
            int ctr = 0;// always increments until reaches row num.
            while((currentLine = br.readLine())!= null){ //di maayu duha ka while.
                if(ctr == row) { //meaning this is the row to be edited.
                    int varsCtr = 0;
                    String[] vars = currentLine.split(",");  //using , and \n as delimiters, we split currentLine
                    System.out.println(vars.length+"\n\n");
                    currentLine = "";              //need to empty currentLine to give way for replacements
                    while(varsCtr <= vars.length-1){  //pwede ni ilain ug method
                        //vars.length-1 kay si varsCtr starts with zero man.
                        if(varsCtr == col) {//when varsCtr reaches col, the replace string will be added to current line instead of the old string.
                            if(varsCtr == vars.length-1)
                                currentLine += replace;        //avoid adding comma to last string of the line.
                            else
                                currentLine += replace + ","; //commas are to simulate the old arrangement.
                        }
                        else {
                            if (varsCtr == vars.length-1)
                                currentLine += vars[varsCtr]; //avoid adding comma to last string of the line.
                            else
                                currentLine += vars[varsCtr] + ",";
                        }
                        varsCtr++;
                    }
                }
                sb.append(currentLine); //we placed each line and also the edited line.
                sb.append('\n'); //separate lines
                ctr++;
            }
            String inputStr = sb.toString(); //sb is buffer
            br.close();
            System.out.println(inputStr);
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
            //inputStr.split(",\n");
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        } System.out.println("before firebase\n");
        refresh();
        directoryTable.setEditable(true);
        //loadBusesToFirebase(filename); System.out.println("\n\nafter firebase");//------I uncomment ni if not debugging ____-----___--_--__===++++===+*7)90_
    }
    public void loadBusesToFirebase(String filename) {
        DatabaseReference busRef = database.child("Bus_Info");
        Map<String, Bus_Info> buses = new HashMap<String, Bus_Info>();
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();

            String sCurrentLine = null;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] vars = sCurrentLine.split(",");
                Bus_Info b = new Bus_Info(vars[2],vars[3],vars[4],vars[5], vars[6]);
                buses.put(vars[1], b);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        busRef.setValue(buses);
    }
    public void loadBuses() {
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();

            String sCurrentLine = null;
            while ((sCurrentLine = br.readLine()) != null) {


                String[] vars = sCurrentLine.split(",");    //System.out.println("Observer!!!"+vars[0]);
               // Bus_Info b = new Bus_Info(Integer.parseInt(vars[0]),vars[1],vars[2],vars[3],vars[4],Integer.parseInt(vars[5]), vars[6]);
                Bus_Info b = new Bus_Info(vars[1],vars[2],vars[3],vars[4],vars[5], vars[6]);
                //System.out.println(b.getCompany() + " " + b.getDestination() + " " + b.getPlate_num()+ " " +b.getSeat_cap()+ " " +b.getType());
                b.setIndex(Integer.parseInt(vars[0]));
                Bus_Info.add(b);
                directoryTable.setItems(Bus_Info);
                //
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }System.out.println("sadf3");
    }
}