module lk.ijse.multi_client_chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;


    opens lk.ijse.multi_client_chatapplication to javafx.fxml;
    //exports lk.ijse;
    //exports lk.ijse.multi_client_chatappication;
    exports lk.ijse.multi_client_chatapplication.Controller;
    exports lk.ijse.multi_client_chatapplication.Utill;
    opens lk.ijse.multi_client_chatapplication.Utill to javafx.fxml;
    //exports lk.ijse.multi_client_chatapplication;
}