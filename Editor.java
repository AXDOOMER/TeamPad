// Copyright (c) Alexandre-Xavier Labonté-Lamoureux, 2015

// javac -encoding UTF8 Editor.java
// java Editor

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
//import java.awt.image.BufferedImage;

public class Editor extends JFrame {
	
	static Editor mainWindowReference;
	static String language = "English";
	int tabSize = 4;	// Get the default tab size			int tabSize = textarea.getTabSize(); // 8
	
	public static String GetStringForLang(String textId) {
		
		String translation = "?";
		
		if (textId.length() > 5) {
			translation = "?????";
		}
		
		if (language.equals("English")) {
			
			switch (textId)
			{
				case "Help": translation = "Help"; break;
				case "About": translation = "About"; break;
				case "Copying": translation = "Written by: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015\n\nDistributed under the GNU GPL version 3"; break;
				
				case "Lenght": translation = "Lenght"; break;
			}
			
		} else if (language.equals("French")) {
			
			switch (textId)
			{
				case "Help": translation = "Aide"; break;
				case "About": translation = "À propos"; break;
				case "Copying": translation = "Écrit par: \nAlexandre-Xavier Labonté-Lamoureux\nDroits d'auteur(c) 2015\n\nDistribué sous la GNU GPL version 3"; break;
				
				case "Lenght": translation = "Longueur"; break;
			}
			
		} else if (language.equals("German")) {
			
			switch (textId)
			{
				case "Help": translation = "Hilfe"; break;
				case "About": translation = "Über"; break;
				case "Copying": translation = "Geschrieben von: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015\n\nUnter der GNU GPL version 3"; break;
			
				case "Lenght": translation = "Länge"; break;
			}
			
		} else if (language.equals("Russian")) {
			
			switch (textId)
			{
				case "Help": translation = "Помогите"; break;
				case "About": translation = "около"; break;
				case "Copying": translation = "написано: \nAlexandre-Xavier Labonté-Lamoureux\nАвторские права 2015\n\nРаспространяется под GNU GPL версии 3"; break;
			
				case "Lenght": translation = "длина"; break;
			}
		} else {
			
			System.err.println("Language '" + language + "' is undefined.");
			System.exit(0);
		}
		
		return translation;
	}
	
	public Editor() {
		mainWindowReference = this;
	
		// Layout
		JPanel mainFrame = new JPanel();
		mainFrame.setLayout(new FlowLayout());
		//this.setLayout(new SpringLayout());
		//this.add(this, BorderLayout.CENTER);
	
		// Set what to display, do a label in the main area
		JLabel emptyLabel = new JLabel("label");
		//emptyLabel.setLocation(300, 0);
		emptyLabel.setPreferredSize(new Dimension(250, 20));
		//frame.setDefaultLookAndFeelDecorated(true);
		
		// Add a textarea
		JTextArea txtArea = new JTextArea();
		//txtArea.setLocation(0, 0);
		//txtArea.setPreferredSize(new Dimension(640, 480));
		txtArea.setTabSize(tabSize);
		txtArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		// textArea.setLineWrap( true );
		// textArea.setWrapStyleWord( true );

		// Add to the frame, put the textarea in a scrollpane first so we get the scrollbars
		JScrollPane scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED/*, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS*/);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		mainFrame.add(scrollPane);
		mainFrame.add(emptyLabel);
		
		// Add the layout to the frame
		this.add(mainFrame);
	
		// Create the window
		//JFrame frame = new JFrame("FrameDemo");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 300));
		this.setTitle("TeamPad - Simple Cooperative Text Editor");
		this.setSize(400, 300);
				
		// Add a menu bar to the frame
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		// File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
		// Create items of the file menu
		JMenuItem newAction = new JMenuItem("New");
		JMenuItem openAction = new JMenuItem("Open");
		JMenuItem saveAction = new JMenuItem("Save");
		JMenuItem saveAsAction = new JMenuItem("Save As");
		JMenuItem saveAllAction = new JMenuItem("Save All");
		JMenuItem closeAction = new JMenuItem("Close");
		JMenuItem exitAction = new JMenuItem("Exit");
		// Add the items to the file menu
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAllAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();	// Separator
		fileMenu.add(closeAction);
		fileMenu.add(exitAction);

		// Edit menu
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		// Create the items of the edit menu
		JMenuItem undoAction = new JMenuItem("Undo");
		JMenuItem redoAction = new JMenuItem("Redo");
		JMenuItem cutAction = new JMenuItem("Cut");
		JMenuItem copyAction = new JMenuItem("Copy");
		JMenuItem pasteAction = new JMenuItem("Paste");
		JMenuItem selectAllAction = new JMenuItem("Select All");
		JMenuItem selectLineAction = new JMenuItem("Select Line");
		JMenuItem findAction = new JMenuItem("Find");
		JMenuItem replaceAction = new JMenuItem("Replace");
		// Add the items to the edit menu
		editMenu.add(undoAction);
		editMenu.add(redoAction);
		editMenu.addSeparator();	// Separator
		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.addSeparator();	// Separator
		editMenu.add(selectAllAction);
		editMenu.add(selectLineAction);
		editMenu.add(findAction);
		editMenu.add(replaceAction);
		
		// Option menu
		JMenu optionMenu = new JMenu("Option");
		menuBar.add(optionMenu);
		// Create the items of the option menu
		JCheckBoxMenuItem lineNumberingAction = new JCheckBoxMenuItem("Line numbering");
		JCheckBoxMenuItem wordWarpAction = new JCheckBoxMenuItem("Word warp");
		JCheckBoxMenuItem statusBarAction = new JCheckBoxMenuItem("Status bar");
		JMenuItem colorsAction = new JMenuItem("Color settings");
		JMenuItem fontAction = new JMenuItem("Font");
		JMenuItem languageAction = new JMenuItem("Language");
		// Add the items to the option menu
		optionMenu.add(lineNumberingAction);
		optionMenu.add(wordWarpAction);
		optionMenu.add(statusBarAction);
		optionMenu.addSeparator();
		optionMenu.add(colorsAction);
		optionMenu.add(fontAction);
		optionMenu.add(languageAction);
		
		// Team menu
		JMenu teamMenu = new JMenu("Team");
		menuBar.add(teamMenu);
		// Create the items of the team menu
		JCheckBoxMenuItem allowConnectionsAction = new JCheckBoxMenuItem("Allow connections");
		JMenuItem connectToAction = new JMenuItem("Connect to...");
		JMenuItem sendMessageAction = new JMenuItem("Send a message...");
		JMenuItem disconnectAllAction = new JMenuItem("Disconnect all");
		// Add the items to the team menu
		teamMenu.add(allowConnectionsAction);
		teamMenu.add(connectToAction);
		teamMenu.addSeparator();
		teamMenu.add(sendMessageAction);
		teamMenu.add(disconnectAllAction);
		
		// Help menu
		JMenu helpMenu = new JMenu(GetStringForLang("Help"));
		menuBar.add(helpMenu);
		// Create the help of the help menu
		JMenuItem aboutAction = new JMenuItem(GetStringForLang("About"));
		// Add the help to the option menu
		helpMenu.add(aboutAction);
		
		// Display the window
		this.pack();
		this.setVisible(true);
		
		// Add an action
		aboutAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				//JOptionPane.showMessageDialog(null, "Written by: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015\n\nDistributed under the GNU GPL version 3", "About", JOptionPane.INFORMATION_MESSAGE);
				JOptionPane.showMessageDialog(mainWindowReference, GetStringForLang("Copying"), GetStringForLang("About"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent comp) {
				// the component was resized...
				System.out.println("window resized: (" + mainWindowReference.getWidth() + "," + mainWindowReference.getHeight() + ")");
				scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
			}
		});
		/*
		txtArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				emptyLabel.setText("label " + txtArea.getText());
			}
		});
		*/
		txtArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				emptyLabel.setText(GetStringForLang("Lenght") + " " + (txtArea.getText()).length());
			}
			public void insertUpdate(DocumentEvent e) {
				emptyLabel.setText(GetStringForLang("Lenght") + " " + (txtArea.getText()).length());
			}
			public void removeUpdate(DocumentEvent e) {
				emptyLabel.setText(GetStringForLang("Lenght") + " " + (txtArea.getText()).length());
			}
		});
	}
	
	public static void main(String[] args) {
		Editor window = new Editor();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
	}
}