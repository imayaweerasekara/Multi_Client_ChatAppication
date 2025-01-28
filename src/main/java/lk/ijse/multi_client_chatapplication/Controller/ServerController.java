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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class ServerController {

    @FXML
    private JFXButton server_sendbtn, server_sendImagebtn;

    @FXML
    private TextField server_textfield;

    @FXML
    private VBox server_messageContainer;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public void initialize() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(4000);
                socket = serverSocket.accept();

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String type = dataInputStream.readUTF(); // Read message type (text/image)

                    if ("text".equals(type)) {
                        String message = dataInputStream.readUTF(); // Read text message
                        Platform.runLater(() -> {
                            TextArea textMessage = new TextArea("Client: " + message);
                            textMessage.setWrapText(true);
                            textMessage.setEditable(false);
                            server_messageContainer.getChildren().add(textMessage);
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
                            server_messageContainer.getChildren().add(imageView);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void server_sendbtn_OnAction(ActionEvent event) throws IOException {
        String message = server_textfield.getText();

        // Send text type identifier
        dataOutputStream.writeUTF("text");
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();

        // Display message in the server's UI
        TextArea textMessage = new TextArea("Server: " + message);
        textMessage.setWrapText(true);
        textMessage.setEditable(false);
        server_messageContainer.getChildren().add(textMessage);

        server_textfield.clear();
    }

    @FXML
    void server_sendImagebtn_OnAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());

            // Send image type identifier
            dataOutputStream.writeUTF("image");
            dataOutputStream.writeInt(imageBytes.length);
            dataOutputStream.write(imageBytes);
            dataOutputStream.flush();

            // Display image in the server's UI
            Image image = new Image(selectedFile.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            server_messageContainer.getChildren().add(imageView);
        }
    }
}



