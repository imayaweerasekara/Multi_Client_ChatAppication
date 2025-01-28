package lk.ijse.multi_client_chatapplication.Controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientController {

    @FXML
    private JFXButton client_sendbtn, client_sendImagebtn;

    @FXML
    private TextField client_textfield;

    @FXML
    private VBox client_messageContainer;

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public void initialize() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 4000);

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String type = dataInputStream.readUTF(); // Read message type (text/image)

                    if ("text".equals(type)) {
                        String message = dataInputStream.readUTF(); // Read text message
                        Platform.runLater(() -> {
                            TextArea textMessage = new TextArea("Server: " + message);
                            textMessage.setWrapText(true);
                            textMessage.setEditable(false);
                            client_messageContainer.getChildren().add(textMessage);
                        });
                    } else if ("image".equals(type)) {
                        int length = dataInputStream.readInt(); // Read image byte length
                        byte[] imageBytes = new byte[length];
                        dataInputStream.readFully(imageBytes);

                        // Convert byte array to Image
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        Platform.runLater(() -> {
                            ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(200);
                            imageView.setPreserveRatio(true);
                            client_messageContainer.getChildren().add(imageView);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void client_sendbtn_OnAction(ActionEvent event) throws IOException {
        String message = client_textfield.getText();

        // Send text type identifier
        dataOutputStream.writeUTF("text");
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();


        TextArea textMessage = new TextArea("Client: " + message);
        textMessage.setWrapText(true);
        textMessage.setEditable(false);
        client_messageContainer.getChildren().add(textMessage);

        client_textfield.clear();
    }

    @FXML
    void client_sendImagebtn_OnAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());


            dataOutputStream.writeUTF("image");
            dataOutputStream.writeInt(imageBytes.length);
            dataOutputStream.write(imageBytes);
            dataOutputStream.flush();


            Image image = new Image(selectedFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            client_messageContainer.getChildren().add(imageView);
        }
    }
}



