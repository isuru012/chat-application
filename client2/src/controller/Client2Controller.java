package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import static javafx.application.Application.launch;

public class Client2Controller extends Application{

    public JFXTextField txtLogin;
    public JFXButton btnSendId;
    public ImageView imgSelect;
    public Label lblClient;
    public ScrollPane scrollPane;
    public ImageView emojiSelect;
    public Pane loginPane;
    public ImageView loginPic;
    public ImageView btnLogin;
    public VBox vBox;
    @FXML
    private JFXListView<?> chatListView;

    @FXML
    private JFXTextArea txtArea;

    final int port = 3000;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Socket socket;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL resource = this.getClass().getResource("../view/Client2.fxml");
        Parent window = FXMLLoader.load(resource);
        Scene scene = new Scene(window);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/chatt.png")));
        primaryStage.show();

    }

    public void initialize() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost",port);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());



                while (true){
                    String s = dataInputStream.readUTF();
                    getMessage(s);
                }
            }catch (Exception e){

            }
        }).start();

    }

    @FXML
    void btnSend(ActionEvent event) throws IOException {

        String msg = txtArea.getText();

        if (!msg.equals("")) {
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();


            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);

            Text text = new Text(msg);
            TextFlow textFlow = new TextFlow(text);

            textFlow.setMaxWidth(400);
            textFlow.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 8; -fx-border-radius: 10; -fx-background-radius: 10");
        /*textFlow.setStyle("-fx-padding: 10");
        textFlow.setStyle("-fx-border-radius: 100");*/


            hBox.getChildren().add(textFlow);
            vBox.getChildren().add(hBox);
            txtArea.clear();
        }
    }

    void getMessage(String msg){
        Platform.runLater(() -> {
            HBox hBox1=new HBox();
            hBox1.setAlignment(Pos.CENTER_LEFT);

            Text text1=new Text(msg);
            TextFlow textFlow1=new TextFlow(text1);

            textFlow1.setMaxWidth(400);
            textFlow1.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 8; -fx-border-radius: 10; -fx-background-radius: 10");


            hBox1.getChildren().add(textFlow1);
            vBox.getChildren().add(hBox1);
        });
    }

    @FXML
    void onActionKeyReleasedTxtArea(KeyEvent event) {

    }

    @FXML
    void onMouseClickedImageSelect(MouseEvent event) {

    }

    public void onMouseClickLoginButton(MouseEvent mouseEvent) {
        try{

            loginPane.setVisible(false);
            txtArea.setVisible(true);
            btnSendId.setVisible(true);
            imgSelect.setVisible(true);
            emojiSelect.setVisible(true);
            scrollPane.setVisible(true);
            lblClient.setVisible(true);


            String name = txtLogin.getText();
            lblClient.setText(name);

            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}