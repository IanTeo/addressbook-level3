package seedu.addressbook.ui;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seedu.addressbook.commands.ExitCommand;
import seedu.addressbook.logic.Logic;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.ReadOnlyPerson;

import java.util.List;
import java.util.Optional;

import static seedu.addressbook.common.Messages.*;

/**
 * Main Window of the GUI.
 */
public class MainWindow {

    private Logic logic;
    private Stoppable mainApp;

    public MainWindow(){
    }

    public void setLogic(Logic logic){
        this.logic = logic;
    }

    public void setMainApp(Stoppable mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextField commandInput;
    
    @FXML
    private TableView<ReadOnlyPerson> personTable;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> noColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> nameColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> phoneColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> emailColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> addressColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> tagColumn;


    @FXML
    void onCommand(ActionEvent event) {
        try {
            String userCommandText = commandInput.getText();
            CommandResult result = logic.execute(userCommandText);
            if(isExitCommand(result)){
                exitApp();
                return;
            }
            displayResult(result);
            clearCommandInput();
        } catch (Exception e) {
            display(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void exitApp() throws Exception {
        mainApp.stop();
    }

    /** Returns true of the result given is the result of an exit command */
    private boolean isExitCommand(CommandResult result) {
        return result.feedbackToUser.equals(ExitCommand.MESSAGE_EXIT_ACKNOWEDGEMENT);
    }

    /** Clears the command input box */
    private void clearCommandInput() {
        commandInput.setText("");
    }

    /** Clears the output display area */
    public void clearOutputConsole(){
        outputConsole.clear();
    }

    /** Displays the result of a command execution to the user. */
    public void displayResult(CommandResult result) {
        clearOutputConsole();
        final Optional<List<? extends ReadOnlyPerson>> resultPersons = result.getRelevantPersons();
        if(resultPersons.isPresent()) {
            display(resultPersons.get());
        }
        display(result.feedbackToUser);
        if (result.getRelevantPersons().isPresent()) {
            ObservableList<ReadOnlyPerson> persons = FXCollections.observableArrayList(result.getRelevantPersons().get());
            personTable.setItems(persons);
        }
    }

    public void displayWelcomeMessage(String version, String storageFilePath) {
        String storageFileInfo = String.format(MESSAGE_USING_STORAGE_FILE, storageFilePath);
        display(MESSAGE_WELCOME, version, MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE, storageFileInfo);
    }
    
    public void initialize() {
        // Initialize the person table with the six columns.
        noColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(personTable.getItems().indexOf(cellData.getValue()) + 1)));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName().fullName));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone().value));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail().value));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress().value));
        tagColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTags().toString()));
        
        // Make the six columns not sortable
        noColumn.setSortable(false);
        nameColumn.setSortable(false);
        phoneColumn.setSortable(false);
        emailColumn.setSortable(false);
        addressColumn.setSortable(false);
        tagColumn.setSortable(false);
    }

    /**
     * Displays the list of persons in the output display area, formatted as an indexed list.
     * Private contact details are hidden.
     */
    private void display(List<? extends ReadOnlyPerson> persons) {
        display(new Formatter().format(persons));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        outputConsole.setText(outputConsole.getText() + new Formatter().format(messages));
    }

}
