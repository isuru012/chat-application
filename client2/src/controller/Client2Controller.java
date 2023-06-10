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


                while (true) {
                    String s = dataInputStream.readUTF();

                    // Check if the received message is an image
                    if (s.equals("IMAGE")) {
                        receiveImage();
                    } else {
                        getMessage(s);
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
            dataOutputStream.writeInt(0);
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
    ImageView imageView = new ImageView();

    @FXML
    void onMouseClickedImageSelect(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Load and resize the selected image
            Image image = new Image(selectedFile.toURI().toString());
            double scaleFactor = 0.5; // Desired scale factor for resizing (e.g., 0.5 for 50% smaller)
            imageView.setImage(resizeImage(image, scaleFactor));

            // Now you can send the resized image to the chat
            sendImageToChat(selectedFile);
        }
    }

    private Image resizeImage(Image image, double scaleFactor) {
        double newWidth = image.getWidth() * scaleFactor;
        double newHeight = image.getHeight() * scaleFactor;
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(newWidth);
        imageView.setFitHeight(newHeight);
        return imageView.snapshot(null, null);
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
            double maxWidth = 200; // Change this to your desired maximum width
            double maxHeight = 200; // Change this to your desired maximum height

            // Calculate the scaling factor
            double scaleFactor = Math.min(maxWidth / originalImage.getWidth(), maxHeight / originalImage.getHeight());

            // Create a new ImageView to display the resized image
            ImageView imageView = new ImageView();
            imageView.setImage(originalImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(originalImage.getWidth() * scaleFactor);
            imageView.setFitHeight(originalImage.getHeight() * scaleFactor);

            // Convert the resized image to a BufferedImage
            BufferedImage resizedImage = SwingFXUtils.fromFXImage(imageView.snapshot(null, null), null);

            // Write the resized image to a ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", byteArrayOutputStream);

            // Send the resized image data to the server
            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            dataOutputStream.writeInt(1); // Message type: Image
            dataOutputStream.write(size);
            dataOutputStream.write(byteArrayOutputStream.toByteArray());
            dataOutputStream.flush();

            // Display the resized image in the chat UI
            HBox hBox = new HBox(imageView);
            hBox.setAlignment(Pos.CENTER_RIGHT);
            vBox.getChildren().add(hBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveImage() {
        try {
            byte[] sizeAr = new byte[4];
            dataInputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            dataInputStream.readFully(imageAr);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageAr);
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            ImageView receivedImageView = new ImageView();
            receivedImageView.setImage(SwingFXUtils.toFXImage(image, null));

            Platform.runLater(() -> {
                HBox hBox = new HBox(receivedImageView);
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