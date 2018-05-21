/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsk1;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

/**
 *
 * @author Admin
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label fileLabel;
    @FXML
    private Button openButton, encryptButton, decryptButton;
    @FXML
    private RadioButton ecbRadio, ofbRadio, cbcRadio, cfbRadio;
    @FXML
    private ToggleGroup mode;
    @FXML
    private TextField outputNameTextField;
    private File file;
    
    
    @FXML
    private void openButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(openButton.getScene().getWindow());
        fileLabel.setText(file.getName());
    }
    
    @FXML
    private void encryptButtonAction(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        if (ecbRadio.isSelected()){
            Aes.encryptEcb(file,outputNameTextField.getText());
        } else if (ofbRadio.isSelected()){
            //Aes.encryptCbc(file,outputNameTextField.getText());
        } else if (cbcRadio.isSelected()){
            
        } else if (cfbRadio.isSelected()){
            
        }
    }
    
    @FXML
    private void decryptButtonAction(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        if (ecbRadio.isSelected()){
            Aes.decryptEcb(file,outputNameTextField.getText());
        } else if (ofbRadio.isSelected()){
            //Aes.decryptCbc(file,outputNameTextField.getText());
        } else if (cbcRadio.isSelected()){
            
        } else if (cfbRadio.isSelected()){
            
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
