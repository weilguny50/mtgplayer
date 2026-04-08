package jfx;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import jfx.httpClient.ImageClient;
import jfx.makeDecklist.CardObject;
import jfx.makeDecklist.DecklistCreator;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;
import static javafx.scene.input.KeyCode.F11;
import static jfx.ClientConnection1.mainConnection;

public class Controller implements Initializable {
    @FXML
    public ImageView mtgcard;
    @FXML
    public Button newcardbutton;
    @FXML
    public AnchorPane controller;
    @FXML
    public ScrollPane myscrollpane;
    @FXML
    public Button newcounterbutton;
    @FXML
    public ImageView deckimage1;


    Socket server;
    PrintWriter out;
    DecklistCreator dc;
    ArrayList<CardObject> decklist;
    Deckhandler dh;
    String baseUrl = "http://localhost:8080";
    ImageClient iclient;
    int whoAmI;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iclient = new ImageClient(baseUrl);
        server = mainConnection();
        ClientRead cl = new ClientRead(server,this);
        Thread listenFromServer = new Thread(cl,"listenForUpdatesFromServer");
        listenFromServer.start();
        dc = new DecklistCreator();
        dh = new Deckhandler(dc.create(),controller);
        try {//Hier den PrintWriter EIN MAL machen, dass nicht immer ein neuer gemacht wird.
            out = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    int left;
    int top;
    int right;
    public void playerOrder(){
        if(whoAmI==1){
            left = 2;
            top = 3;
            right = 4;
        }if(whoAmI==2){
            left = 3;
            top = 4;
            right = 1;
        }if(whoAmI==3){
            left = 4;
            top = 1;
            right = 2;
        }if(whoAmI==4){
            left = 1;
            top = 2;
            right = 3;
        }
    }
    int sarrs=0;
    public void keypressed(KeyEvent e){//Sinn: mit wasd scrollen zu können, damit man nicht immer komisch mit der Maus den Scrollbalken jagen muss
    switch(e.getText()){//KeyEvent get Text holt die gedrückte Taste, je nach Taste im H oder V Value rauf oder runter, H/V Value ist 0-1, wenns nicht ganz
        case "w": myscrollpane.setVvalue(myscrollpane.getVvalue()-0.05);//0.1 plus oder minus rechnen kann, weil es zu nah an 0 bzw. 1 dran ist, dann wird auf
        if(myscrollpane.getVvalue()<0.05){myscrollpane.setVvalue(0);}break;//0 bzw. 1 gesetzt
        case "a": myscrollpane.setHvalue(myscrollpane.getHvalue()-0.05);
        if(myscrollpane.getHvalue()<0.05){myscrollpane.setHvalue(0);}break;
        case "s": myscrollpane.setVvalue(myscrollpane.getVvalue()+0.05);
        if(myscrollpane.getVvalue()>0.95){myscrollpane.setVvalue(1);}break;
        case "d": myscrollpane.setHvalue(myscrollpane.getHvalue()+0.05);
        if(myscrollpane.getHvalue()>0.95){myscrollpane.setHvalue(1);}break;}
        if(e.getCode().equals(F11)){//F11 für Fullscreen
            Main.mystage.setFullScreen(true);//Stage ganz oben als Objekt vom Main über Methode abgespeichert.//
            }if(sarrs==0){
            playerOrder();
            sarrs=1;
        }
    }

    public void openContextMenu(){
        //todo kontext menü machen für deck und karten am feld
    }

    public  void newcounterbuttonpress(){//Button für Statusfeld erzeugung
        TextField txf = new TextField();//Es ist das Selbe was die Kartenbilder erzeugt.
        txf.setPrefWidth(200);      //Einfach ein TextField wallah
        txf.setPrefHeight(64);
        txf.setLayoutX(1590);
        txf.setLayoutY(1873);
        txf.setFont(new Font("System Italic", 24));
        controller.getChildren().add(txf);
        //tapAndViewOrder.makeDraggable(txf);
        txf.setId(returnNewUUID()+"§"+":txf:");
        txf.setPromptText("ENTER = update");
        txf.setOnDragDetected(this::cardDragDetected);//setOnDragDetected referenziert auf die Methode carddragdetected
        txf.setOnMousePressed(this::onMousePressed);
        txf.setOnAction(this::hitEnterOnTextfield);
        newNodeToServer(txf);
    }

    Path directory = Path.of(Path.of(System.getProperty("user.dir")).getParent() + "/1ClientCards");
    File clientFolder = new File(String.valueOf(directory));

    public void createNewCard(MouseEvent e) throws FileNotFoundException {
        String id = dh.drawACard();
        if(id!=null) {

            File myFile = null;
            for (File f : clientFolder.listFiles()) {
                if (f.getName().equals(id)) {
                    myFile = f;
                }
            }
            if (myFile == null) {
                try {
                    iclient.download(id, directory);
                    iclient.download(id.substring(0,id.length()-11)+"_back_.jpg",directory);//Gleichzeitiger Download der möglichen Rückseite.
                } catch (IOException | InterruptedException _) {
                }
                for (File f : clientFolder.listFiles()) {
                    if (f.getName().equals(id)) {
                        myFile = f;
                    }
                }
            }
            Image img = new Image(new FileInputStream(myFile));//mit Fileinputstream wenn das Bild einfach so in einem Ordner ist.
            //Image img = new Image("file:"+myFile); mysteriös
            ImageView iv = new ImageView(img);
            iv.setFitWidth(200);
            iv.setFitHeight(280);
            iv.setPreserveRatio(true);
            iv.setLayoutX(2650);
            iv.setLayoutY(2700);
            controller.getChildren().add(iv);
            //tapAndViewOrder.makeDraggable(iv);//make draggable
            iv.setId(returnNewUUID() + "§" + id);//id setzen
            iv.setManaged(false);
            iv.setOnDragDetected(this::cardDragDetected);//setOnDragDetected referenziert auf die Methode carddragdetected
            iv.setOnMousePressed(this::onMousePressed);
            newNodeToServer(iv);
            //iv.setImage(new Image(getClass().getResource("/backside.jpg").toExternalForm())); bild ändern von karte
        }
    }

    public void ownDeckNewCard(MouseEvent e) throws FileNotFoundException {

        if(e.getButton() == MouseButton.MIDDLE){//Mausrad click
            createNewCard(e);
        }
    }

    double nodeOffsetX;
    double nodeOffsetY;
    double viewOrder = 9.0; //double 9 bis 0, niedrigerer Wert = höhere Priorität.

    public void onMousePressed(MouseEvent e){
        Node node = (Node) e.getSource();
        //Jedes Mal, wenn auf eine Karte geklickt wird, wird von der viewOrder Variable
        // 0.00001 abgezogen, somit kann man insgesamt 999999 Mal auf eine Karte klicken bis es abschmiert.
        node.setViewOrder(viewOrder = viewOrder - 0.00001);

        if (e.getButton() == MouseButton.SECONDARY) {//Auf Rechtsclick auf Karte checken

            if (node.getRotate() == 0||node.getRotate() == 360) {  //wenn nicht getappt ist, dann tappen,
                node.setRotate(270);
            } else if (node.getRotate()==270||node.getRotate()==630){ //wenn getappt ist, dann untappen
                node.setRotate(0);
            }
            out.println(node.getId()+"~"+node.getRotate()+"~"+node.getLayoutX()+"~"+node.getLayoutY());
            node.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);//Contextmenü Event consumen, damit es nicht aufploppt bei Textfields
        }
        if(e.getButton() == MouseButton.MIDDLE){
            ImageView iv = (ImageView) e.getSource();
            String id[] = iv.getId().split("§");
            String newId = "";
            File newImage = null;
            if(id[1].contains("_back_")){
                newId = id[1].replaceAll("_back_","_front_");
            } else if (id[1].contains("_front_")) {
                newId = id[1].replaceAll("_front_","_back_");
            }
            for (File f : clientFolder.listFiles()){
                if(f.getName().equals(newId)){
                    newImage=f;
                    String fullNewId = id[0]+"§"+newId;
                    out.println(iv.getId()+"~"+fullNewId+"~"+newId);//an server und server an clients schicken
                    iv.setId(fullNewId);//id wieder zusammenbauen und setzen.
                }
            }
            if(newImage!=null){
                try {
                    iv.setImage(new Image(new FileInputStream(newImage)));
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
    }

    public void cardDragDetected(MouseEvent mouseEvent) {

        if (mouseEvent.getButton() == MouseButton.PRIMARY) {//check ob linke Maustaste gedrückt wurde, nur dann gehts
            Node node = (Node) mouseEvent.getSource();//Node wo draufgeklickt wird(ImagView/TextField)
            Dragboard db = node.startDragAndDrop(TransferMode.ANY);//Dragboard speichert was man draggt, node.startdraganddrop gibt ein Dragboard Objekt zurück, das nicht eingeschränkt ist wegen Transfermode.ANY
            ClipboardContent content = new ClipboardContent();//Clipboard ist wie Strg+C
            content.putString("Hello!");//Hier den Inhalt des Clipboards festlegen, kann auch eine Datei oder was anderes sein.
            db.setContent(content);//Clipboard in das Dragboard setzen

            nodeOffsetX = node.getLayoutX() - mouseEvent.getScreenX();//Hier die Start-Koordinaten setzten wenn der drag detected wird.
            nodeOffsetY = node.getLayoutY() - mouseEvent.getScreenY();
        }

    }

    public void hitEnterOnTextfield(ActionEvent e){

            TextField n = (TextField) e.getSource();
            if(n.getText().isEmpty()){
                out.println(n.getId()+"~"+" ");
            }else{
                out.println(n.getId()+"~"+n.getText());
            }
    }

    public void draggedoverAnchorpane(DragEvent d){//steht jetzt in fxml beim AnchorPane

        d.acceptTransferModes(TransferMode.ANY);//macht, dass der Anchorpane etwas gedropptes annehmen kann, bzw. isaccepted bei Abruf auf true setzt

        Node node = (Node) d.getGestureSource();//Wieder die Node die verschoben wird bekommen
        node.relocate(nodeOffsetX + d.getScreenX(), nodeOffsetY + d.getScreenY());//hier in der draggedover methode relocate machen, dass es die verschiebe-animation hat.
    }

    public void droppedonAnchorpane(DragEvent d) {//steht jetzt in fxml beim AnchorPane

        d.setDropCompleted(true);//Sagt, dass der drop completed ist, ist nur nötig, um es von woanders abfragen zu können, obs erfolgreich ist
        Node node = (Node) d.getGestureSource();
        double x = node.getLayoutX();
        double y = node.getLayoutY();
        out.println(node.getId()+"~"+node.getRotate()+"~"+x+"~"+y);
    }

    public void newNodeToServer(Node n){
        out.println(n.getId()+"~"+n.getRotate()+"~"+n.getLayoutX()+"~"+n.getLayoutY()+"~"+"NEW");
    }

    public void adjustNodesFromServer(String string) throws FileNotFoundException {

        String mulm = null;
        String[] data = null;
        int fromWho = 0;
        double addRotation;
        if(string.length() != 1){//Wenn am Anfang der Handshake und die Übergabe von welcher Client ist man, dann soll das nicht passieren.
        String[] split = string.split("~");
        fromWho = Integer.parseInt(split[split.length-1]);
        ArrayList<String> withoutWho = new ArrayList<>();
        withoutWho.addAll(Arrays.asList(split));
        withoutWho.removeLast();//Alles ausser dem letzten. Der letzte Slot ist, von wem es kommt.
        mulm = withoutWho.get(0);
        for (int i = 0; i < withoutWho.size()-1; i++) {
                mulm = mulm+"~"+withoutWho.get(i+1);
        }
        data = mulm.split("~");
            if(left==fromWho){
                addRotation=90;
            } else if (top==fromWho) {
                addRotation=180;
            } else if (right==fromWho) {
                addRotation=270;
            } else {
                addRotation = 0;
            }
        } else {
            addRotation = 0;
        }
        if (string.length() == 1) {
            data = new String[]{string};
        }

        Node card=null;
        if(data.length==5){//Länge 5 NEUE KARTE---------------------------------------------------------------------

            String[] data1 = data[0].split("§");
            String[] finalData = data;
            String newCoords = correctCoordinates(finalData[2],finalData[3],fromWho);
            String[] newCoords1 = newCoords.split("~");
            Platform.runLater(() -> {//So führt man Sachen von einem anderen Thread aus.//Wenn man das Platform.runLater() nicht macht, dann schreits dass der Thread nicht der JavaFX Thread ist.
                try {createNewNodeFromServer(finalData[0],data1[1], String.valueOf(Double.parseDouble(finalData[1])+addRotation), newCoords1[0], newCoords1[1]);
                } catch (FileNotFoundException e) {throw new RuntimeException(e);}
            });
        } else if (data.length==4){//Länge 4 POSITION/ROTATION ANPASSEN---------------------------------------------------------------

        for (Node n:controller.getChildren()){
            if(n.getId()!=null && n.getId().equals(data[0])){
                card=n;
            }
        }
        if (card != null) {
            String newCoords = correctCoordinates(data[2],data[3],fromWho);
            String[] newCoords1 = newCoords.split("~");
            card.setRotate(Double.parseDouble(data[1])+addRotation);
            card.setLayoutX(Double.parseDouble(newCoords1[0]));
            card.setLayoutY(Double.parseDouble(newCoords1[1]));
        }
        } else if (data.length==3){//Länge 3 FRONT/BACK ANPASSEN----------------------------------------------------------------
            for (Node n : controller.getChildren()){
                if(n.getId()!=null&&n.getId().equals(data[0])){
                    File newImage = null;
                    ImageView iv = (ImageView) n;
                    for (File f : clientFolder.listFiles()){
                        if(f.getName().equals(data[2])){
                            newImage=f;
                            iv.setId(data[1]);//id wieder zusammenbauen und setzen.
                        }
                    }
                    iv.setImage(new Image(new FileInputStream(newImage)));
                }
            }
        } else if (data.length == 2){//Länge 2 TEXTFELD INHALT ÄNDERN---------------------------------------------------------------
            TextField t;
            for (Node n:controller.getChildren()){
                if(n.getId()!=null && n.getId().equals(data[0])){


                    t = (TextField) n;
                    if(data[1].equals(" ")){
                        t.setText("");
                    }else {
                    t.setText(data[1]);
                    }
                }
            }
        } else if (data.length == 1) {
            whoAmI = Integer.parseInt(data[0]);
        }
    }

    public String correctCoordinates(String x,String y,int fromWho){
        double ix = Double.parseDouble(x);
        double iy = Double.parseDouble(y);
        if(fromWho==left) {ix-=1500;iy-=1500;iy=-iy;ix+=1500;iy+=1500;}
        if(fromWho==top) {ix-=1500;iy-=1500;iy=-iy;ix=-ix;ix+=1500;iy+=1500;}
        if(fromWho==right) {ix-=1500;iy-=1500;ix=-ix;ix+=1500;iy+=1500;}
        return ix+"~"+iy;
    }

    public void createNewNodeFromServer(String fullID, String id, String rotation, String x, String y) throws FileNotFoundException {

        if(id.equals(":txf:")){
            TextField txf = new TextField();
            txf.setPrefWidth(200);
            txf.setPrefHeight(64);
            txf.setLayoutX(Double.parseDouble(x));
            txf.setLayoutY(Double.parseDouble(y));
            txf.setFont(new Font("System Italic", 24));
            controller.getChildren().add(txf);
            txf.setId(fullID);
            txf.setPromptText("ENTER = update");
            txf.setRotate(Double.parseDouble(rotation));
            txf.setOnDragDetected(this::cardDragDetected);
            txf.setOnMousePressed(this::onMousePressed);
            txf.setOnAction(this::hitEnterOnTextfield);
        } else {
            File myFile=null;//wiederholter Code, ja ist schlecht aber ich kanns ja zum schluss noch polishen
            for (File f : clientFolder.listFiles()){
                if(f.getName().equals(id)){
                    myFile = f;
                }
            }
            if (myFile == null) {
                try {
                    iclient.download(id, directory);
                    iclient.download(id.substring(0,id.length()-11)+"_back_.jpg",directory);//Gleichzeitiger Download der möglichen Rückseite.
                } catch (IOException | InterruptedException _) {
                }
                for (File f : clientFolder.listFiles()) {
                    if (f.getName().equals(id)) {
                        myFile = f;
                    }
                }
            }
            Image img = new Image(new FileInputStream(myFile));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(200);
            iv.setFitHeight(280);
            iv.setPreserveRatio(true);
            iv.setLayoutX(Double.parseDouble(x));
            iv.setLayoutY(Double.parseDouble(y));
            controller.getChildren().add(iv);
            iv.setId(fullID);
            iv.setRotate(Double.parseDouble(rotation));
            iv.setManaged(false);
            iv.setOnDragDetected(this::cardDragDetected);
            iv.setOnMousePressed(this::onMousePressed);
        }
    }

    public String returnNewUUID(){
        return String.valueOf(UUID.randomUUID());
    }
}
