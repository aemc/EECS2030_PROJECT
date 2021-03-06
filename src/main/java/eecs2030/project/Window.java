package eecs2030.project;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import eecs2030.project.Models.TableModel;
import eecs2030.project.Utilities.Constants;
import eecs2030.project.Utilities.Database;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible in initializing the main Window Visual Aspect as well as the high score table.
 */
@SuppressWarnings("serial")
public class Window extends JFrame implements ActionListener {

    private JPanel leftbox;
    private JPanel rightbox;
    private Database database;
    private TableModel tableModel;
    private Box mainMenuBox;
    private JTextField playerNameTF;

    public Window() throws Exception {
        super(Constants.GAME_TITLE);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.setResizable(false);

        //Set the Main Window in the center of the Screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());

        // <------ INITIAL SETUP FUNCTIONS ---->
        screenLeftSide();
        screenRightSide();
        databaseSetUp();
        // <------ END SETUP FUNCTIONS --->

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

    }

    /**
     * Creates a new database
     *
     */
    private void databaseSetUp() {
        this.database = Database.getInstance();
        DatabaseReference databaseRef = database.getDatabaseRef();
        databaseRef.orderByChild("points").limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                tableModel.addScore(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                //Values never Changes.
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                tableModel.removeScore(snapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                //Values are sorted on the Client Side so this is not needed.
            }

            @Override
            public void onCancelled(DatabaseError error) {
                database.pushError(error.getMessage() + " ||  " + error.getDetails() + " || " + error.getCode());
            }
        });
    }

    private void screenRightSide() {
        //Box Holder for the game
        rightbox = new JPanel(new BorderLayout());
        rightbox.setBackground(Color.ORANGE);
        rightbox.setPreferredSize(new Dimension(Constants.GAME_WIDTH, Constants.HEIGHT));
        addMainMenu();
        this.getContentPane().add(rightbox, BorderLayout.CENTER);
    }


    private void screenLeftSide() {
        tableModel = new TableModel();
        JTable table = new JTable();
        table.setModel(tableModel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
        tablePanel.add(table, BorderLayout.CENTER);

        ImageIcon image = new ImageIcon(getClass().getResource("logo.png"));
        JLabel label = new JLabel("", image, JLabel.CENTER);

        leftbox = new JPanel(new BorderLayout());
        leftbox.add(new JLabel(Constants.HIGHSCORES_LABEL), BorderLayout.PAGE_START);
        leftbox.add(tablePanel, BorderLayout.CENTER);
        leftbox.add(label, BorderLayout.PAGE_END);
        this.getContentPane().add(leftbox, BorderLayout.WEST);
    }

    private void addMainMenu() {
        JLabel playerNameLabel = new JLabel(Constants.PLAYER_NAME_LABEL);
        this.playerNameTF = new JTextField("", 20);

        JButton btnStart = new JButton(Constants.START_GAME_BUTTON);
        JButton btnQuit = new JButton(Constants.EXIT_GAME_BUTTON);
        btnStart.setEnabled(false);

        btnStart.setActionCommand(Constants.START_COMMAND);
        btnQuit.setActionCommand(Constants.EXIT_COMMAND);

        btnStart.addActionListener(this);
        btnQuit.addActionListener(this);

        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQuit.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Listen for changes in the text
        this.playerNameTF.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            private void warn() {
                String playerName = playerNameTF.getText();
                btnStart.setEnabled((playerName != null) && playerName.matches("[a-zA-Z][A-Za-z0-9_]{2,17}"));
            }
        });

        Box InputBox = Box.createHorizontalBox();
        InputBox.add(Box.createHorizontalStrut(100));
        InputBox.add(playerNameLabel);
        InputBox.add(Box.createHorizontalStrut(Constants.HORIZONTAL_PADDING));
        InputBox.add(this.playerNameTF);
        InputBox.add(Box.createHorizontalStrut(100));

        Box buttonsBox = Box.createHorizontalBox();
        buttonsBox.add(btnStart);
        buttonsBox.add(Box.createHorizontalStrut(Constants.HORIZONTAL_PADDING));
        buttonsBox.add(btnQuit);

        Box reqBox = Box.createHorizontalBox();
        reqBox.add(Box.createHorizontalStrut(Constants.GAME_WIDTH/2 - 100));
        reqBox.add(new Label("Name needs to be more than 3 characters long!"));


        mainMenuBox = Box.createVerticalBox();
        mainMenuBox.add(Box.createVerticalStrut((int) (Constants.HEIGHT/2.5)));
        mainMenuBox.add(InputBox);
        mainMenuBox.add(reqBox);
        mainMenuBox.add(Box.createVerticalStrut(Constants.VERTICAL_PADDING));
        mainMenuBox.add(buttonsBox);
        mainMenuBox.add(Box.createVerticalStrut(Constants.HEIGHT));

        rightbox.add(mainMenuBox, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case Constants.START_COMMAND:
                mainMenuBox.setVisible(false);
                rightbox.grabFocus();
                String name = this.playerNameTF.getText().trim();
                GameController newGame = new GameController(name);
                rightbox.add(newGame, BorderLayout.CENTER);
                new Thread(newGame).start();
                newGame.requestFocus();
                this.revalidate();
                break;
            case Constants.EXIT_COMMAND:
                System.exit(0);
                break;
        }
    }
}
