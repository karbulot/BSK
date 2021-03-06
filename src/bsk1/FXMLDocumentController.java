/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsk1;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label fileLabel, currentUserLabel, messageLabel, currentUserLabelLabel;
    @FXML
    private Button openButton, encryptButton, decryptButton, addUserButton;
    @FXML
    private RadioButton ecbRadio, ofbRadio, cbcRadio, cfbRadio;
    @FXML
    private ToggleGroup mode;
    @FXML
    private TextField outputNameTextField, userNameTextField, passwordTextField, blockSizeTextField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableView userTable;
    @FXML
    private TableColumn usernameColumn;
    private File file, usersFile;
    private User currentUser;
    private List<User> users = new ArrayList<>();
    private ObservableList<User> usersObservable = FXCollections.observableList(users);
;
    
    
    @FXML
    private void openButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(openButton.getScene().getWindow());
        fileLabel.setText(file.getName());
    }
    
    @FXML
    private void encryptButtonAction(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        List<User> selectedUsers = userTable.getSelectionModel().getSelectedItems();
        progressBar.setProgress(0);
       
        if (ecbRadio.isSelected()){
            Aes.encrypt(file,outputNameTextField.getText(),selectedUsers, "ECB", 128);
        } else if (cbcRadio.isSelected()){
            Aes.encrypt(file,outputNameTextField.getText(),selectedUsers, "CBC", 128);
        } else if (ofbRadio.isSelected()){
            Aes.encrypt(file,outputNameTextField.getText(),selectedUsers, "OFB", (int) Math.pow(2,Integer.parseInt(blockSizeTextField.getText())));
        } else if (cfbRadio.isSelected()){
            Aes.encrypt(file,outputNameTextField.getText(),selectedUsers, "CFB", (int) Math.pow(2,Integer.parseInt(blockSizeTextField.getText())));
        }
        progressBar.setProgress(100);
        messageLabel.setText("File encrypted successfully.");
    }
    
    @FXML
    private void decryptButtonAction(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, ShortBufferException, BadPaddingException, SAXException, ParserConfigurationException {
        Aes.decrypt(file,outputNameTextField.getText(),currentUser);
        messageLabel.setText("File decrypted successfully.");
        
    }
    
    @FXML
    private void addUserButtonAction(ActionEvent event) throws NoSuchAlgorithmException, IOException{
        if (passwordTextField.getText().length() < 8){
            messageLabel.setText("Password too short. Please use at least 8 characters.");
            return;
        }
        User user = new User(userNameTextField.getText(), passwordTextField.getText());
        System.out.println(user);
        users.add(user);
        usersObservable = FXCollections.observableList(users);
        userTable.setItems(usersObservable);
        saveUsers();
        userNameTextField.clear();
        passwordTextField.clear();
        messageLabel.setText("User "+user.getName()+" added successfully.");

    }
    @FXML
    private void switchUserButtonAction(ActionEvent event) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for (User user : users){
            if (user.getName().equals(userNameTextField.getText())){
                if (Arrays.equals(user.getPasswordHash(), digest.digest(passwordTextField.getText().getBytes()))){
                    currentUser = user;
                    currentUserLabel.setText(user.getName());
                    messageLabel.setText("Logged in as "+user.getName()+".");
                    userNameTextField.clear();
                    passwordTextField.clear();
                    return;
                } else {
                    messageLabel.setText("Wrong Password");
                    userNameTextField.clear();
                    passwordTextField.clear();
                    return;
                }
            }
            
        }
        userNameTextField.clear();
        passwordTextField.clear();
        messageLabel.setText("User not found.");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            loadUsers();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        usernameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        userTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        userTable.setItems(usersObservable);
        try {
            if (users.isEmpty()) users.add(new User("admin", "admin"));
            currentUser = users.get(0);
            currentUserLabel.setText(users.get(0).getName());
            saveUsers();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }    
    
    private void saveUsers() throws IOException{
        for (User u: users){
            new File("Users").mkdir();
            File dir = new File("Users\\"+u.getName());
            dir.mkdir();
            Path publicKey, privateKey, password;
            publicKey = Paths.get(dir.getPath(),"key.pub");
            privateKey = Paths.get(dir.getPath(),"key.key");
            password = Paths.get(dir.getPath(),"pass.txt");
            Files.write(publicKey, u.getPublicKeyString());
            Files.write(privateKey, u.getPrivateKeyString());
            Files.write(password, u.getPasswordHash());
        }
    }
    
    private void loadUsers() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        File usersdir = new File("Users");
        usersdir.mkdir();
        for (File u : usersdir.listFiles()){
            byte[] privateKey = null, publicKey = null, password = null;
            for (File f : u.listFiles()){
                switch (f.getName()){
                    case "key.pub": publicKey = Files.readAllBytes(f.toPath()); break;
                    case "key.key": privateKey = Files.readAllBytes(f.toPath()); break;
                    case "pass.txt": password = Files.readAllBytes(f.toPath()); break;
                }
            }
            users.add(new User(u.getName(),password,publicKey,privateKey));
        }
    }
}
