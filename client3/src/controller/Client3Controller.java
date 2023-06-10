package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import static javafx.application.Application.launch;

public class Client3Controller extends Application{

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
        URL resource = this.getClass().getResource("../view/Client3.fxml");
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

                while (true) {
                    String s = dataInputStream.readUTF();

                    // Check if the received message is an image
                    if (s.equals("IMAGE")) {
                        String msg= dataInputStream.readUTF();
                        receiveImage(msg);
                    } else {
                        String msg= dataInputStream.readUTF();
                        System.out.println(msg);
                        getMessage(msg);
                    }
                }
            }catch (Exception e){

            }
        }).start();

    }

    @FXML
    void btnSend(ActionEvent event) throws IOException {

        String msg = txtArea.getText();

        if (!msg.equals("")) {
            dataOutputStream.writeUTF("TEXT");
            dataOutputStream.flush();

            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();


            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);

            Text text = new Text(msg);
            TextFlow textFlow = new TextFlow(text);

            textFlow.setMaxWidth(400);
            textFlow.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 8; -fx-border-radius: 10; -fx-background-radius: 10");


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
    void onMouseClickedImageSelect(MouseEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {

            dataOutputStream.writeUTF("IMAGE");
            dataOutputStream.flush();

            dataOutputStream.writeUTF(selectedFile.toURI().toString());
            dataOutputStream.flush();

            // Now you can send the resized image to the chat
            sendImageToChat(selectedFile);
        }
    }


    /*private void sendImageToChat(File selectedFile) {
        try {
            BufferedImage image = ImageIO.read(selectedFile);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            dataOutputStream.writeInt(1); // Message type: Image
            dataOutputStream.write(size);
            dataOutputStream.write(byteArrayOutputStream.toByteArray());
            dataOutputStream.flush();

            ImageView imageView = new ImageView(new Image(selectedFile.toURI().toString()));

            HBox hBox = new HBox(imageView);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            vBox.getChildren().add(hBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    private void sendImageToChat(File selectedFile) {
        try {
            // Load the original image
            Image originalImage = new Image(selectedFile.toURI().toString());

            // Calculate the desired width and height for the resized image
            ImageView originalImageView = new ImageView();

            originalImageView.prefHeight(150);
            originalImageView.prefWidth(150);

            originalImageView.setImage(originalImage);

            // Display the resized image in the chat UI
            HBox hBox = new HBox(originalImageView);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            vBox.getChildren().add(hBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void receiveImage(String msg) {
        try {
            /*byte[] sizeAr = new byte[4];
            dataInputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            dataInputStream.readFully(imageAr);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageAr);
            BufferedImage image = ImageIO.read(byteArrayInputStream);*/


            String imagePath = dataInputStream.readUTF();

            ImageView receivedImageView = new ImageView();

            Text text1=new Text(msg);
            TextFlow textFlow1=new TextFlow(text1);

            receivedImageView.prefHeight(150);
            receivedImageView.prefWidth(150);

            receivedImageView.setImage(new Image(imagePath));

            Platform.runLater(() -> {
                HBox hBox = new HBox(receivedImageView);
                hBox.getChildren().add(textFlow1);
                vBox.getChildren().add(hBox);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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