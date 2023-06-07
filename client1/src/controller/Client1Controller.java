package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import static javafx.application.Application.launch;

public class Client1Controller extends Application{

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

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL resource = this.getClass().getResource("../view/Client1.fxml");
        Parent window = FXMLLoader.load(resource);
        Scene scene = new Scene(window);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/chatt.png")));
        primaryStage.show();

    }

    @FXML
    void btnSend(ActionEvent event) {


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

            lblClient.setText(txtLogin.getText());

            Socket socket = new Socket("localhost",port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            String name = txtLogin.getText();
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();



            while (true){
                String msg = txtArea.getText();

                if (msg.equalsIgnoreCase("CLOSE")){
                    break;
                }

                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
                System.out.println(dataInputStream.readUTF());


            }

            dataOutputStream.close();
            socket.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}