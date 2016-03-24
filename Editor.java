// Copyright (c) Alexandre-Xavier Labonté-Lamoureux, 2015

// javac -encoding UTF8 Editor.java
// java Editor

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
//import java.awt.image.BufferedImage;
import java.security.*;
import javax.xml.bind.DatatypeConverter;

public class Editor extends JFrame {
	
	static Editor mainWindowReference;
	static String language = "English";
	int tabSize = 4;	// Get the default tab size			int tabSize = textarea.getTabSize(); // 8
	String defaultFont = "Courier New";
	int textSize = 14;		// zoom
	boolean lineNumbering = false;		// lines
	boolean lineWarp = false;		// wrap
	boolean statusBar = true;		// stats
	Map<String, String> configs = new HashMap<String, String>();
	
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
				case "Line": translation = "Line"; break;
				case "Notice": translation = "Notice"; break;
				case "RestartTranslate": translation = "The interface will be completly translated only once you will restart the program."; break;
			}
			
		} else if (language.equals("French")) {
			
			switch (textId)
			{
				case "Help": translation = "Aide"; break;
				case "About": translation = "À propos"; break;
				case "Copying": translation = "Écrit par: \nAlexandre-Xavier Labonté-Lamoureux\nDroits d'auteur(c) 2015\n\nDistribué sous la GNU GPL version 3"; break;
				
				case "Lenght": translation = "Longueur"; break;
				case "Line": translation = "Ligne"; break;
				case "Notice": translation = "Avertissement"; break;
				case "RestartTranslate": translation = "L'interface sera complètement traduite que lorsque vous aurez redémarré le logicel."; break;
			}
			
		} else if (language.equals("German")) {
			
			switch (textId)
			{
				case "Help": translation = "Hilfe"; break;
				case "About": translation = "Über"; break;
				case "Copying": translation = "Geschrieben von: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015\n\nUnter der GNU GPL version 3"; break;
			
				case "Lenght": translation = "Länge"; break;
				case "Notice": translation = "Beachten"; break;
			}
			
		} else if (language.equals("Russian")) {
			
			switch (textId)
			{
				case "Help": translation = "Помогите"; break;
				case "About": translation = "около"; break;
				case "Copying": translation = "написано: \nAlexandre-Xavier Labonté-Lamoureux\nАвторские права 2015\n\nРаспространяется под GNU GPL версии 3"; break;
			
				case "Lenght": translation = "длина"; break;
				case "Notice": translation = "уведомление"; break;
			}
		} else {
			
			System.err.println("Language '" + language + "' is undefined.");
			System.exit(0);
		}
		
		return translation;
	}
	
	public Editor() {
		mainWindowReference = this;
		
		// Load configuration from file
		try {
			BufferedReader reader = new BufferedReader(new FileReader("config.txt"));
			String ligne;
			while ((ligne = reader.readLine()) != null) {
				if (ligne.length() > 0)
				{
					String[] parts = ligne.split("\t");
					System.out.println(parts[0] + "=" + parts[1]);
					configs.put(parts[0], parts[1]);
				}				
			}
			reader.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("The configuration file contains errors.");
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			//System.exit(1);
		}

		// Apply the settings loaded from the configuration file
		tabSize = Integer.parseInt(configs.get("tab"));
		defaultFont = configs.get("font");
		language = configs.get("lang");
		textSize = Integer.parseInt(configs.get("zoom"));
		lineNumbering = Boolean.parseBoolean(configs.get("lines"));
		lineWarp = Boolean.parseBoolean(configs.get("warp"));
		statusBar = Boolean.parseBoolean(configs.get("stats"));
		
		// Layout
		JPanel mainFrame = new JPanel();
		mainFrame.setLayout(new FlowLayout());
		//this.setLayout(new SpringLayout());
		//this.add(this, BorderLayout.CENTER);
	
		// Set what to display, do a label in the main area
		JLabel lengthLabel = new JLabel("label");
		JLabel lineLabel = new JLabel("label2");
		lengthLabel.setPreferredSize(new Dimension(250, 20));
		lineLabel.setPreferredSize(new Dimension(100, 20));
		//frame.setDefaultLookAndFeelDecorated(true);
		
		// Add a textarea
		JTextArea txtArea = new JTextArea();
		//txtArea.setLocation(0, 0);
		//txtArea.setPreferredSize(new Dimension(640, 480));
		txtArea.setTabSize(tabSize);
		txtArea.setFont(new Font(defaultFont, Font.PLAIN, textSize));
		// textArea.setLineWrap( true );
		// textArea.setWrapStyleWord( true );

		// Add to the frame, put the textarea in a scrollpane first so we get the scrollbars
		JScrollPane scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED/*, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS*/);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		mainFrame.add(scrollPane);
		mainFrame.add(lengthLabel);
		mainFrame.add(lineLabel);
		
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
		fileMenu.add(saveAsAction);
		fileMenu.add(saveAllAction);
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
		JMenuItem findAction = new JMenuItem("Find and replace");
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
		
		// Option menu
		JMenu optionMenu = new JMenu("Option");
		menuBar.add(optionMenu);
		// Create the items of the option menu
		JMenuItem increaseAction = new JMenuItem("Increase text size");
		JMenuItem decreaseAction = new JMenuItem("Decrease text size");
		JCheckBoxMenuItem lineNumberingAction = new JCheckBoxMenuItem("Line numbering");
		JCheckBoxMenuItem wordWarpAction = new JCheckBoxMenuItem("Word warp");
		JCheckBoxMenuItem statusBarAction = new JCheckBoxMenuItem("Status bar");
		JMenuItem colorsAction = new JMenuItem("Color settings");
		/*JMenuItem fontAction = new JMenuItem("Font");*/
		/*JMenuItem languageAction = new JMenuItem("Language");*/
		
		// Fonts
		JMenu fontsMenu = new JMenu("Font");
		ButtonGroup fontsGroup = new ButtonGroup();
		String[] fonts = {"Andale Mono", "Arial", "Consolas", "Courier New", "DejaVu Sans Mono", 
		"Droid Sans Mono", "Fixedsys", "Liberation Mono", "Lucida Console", "Monaco", "Source Code Pro", "System"};

		String[] fontlist=(GraphicsEnvironment.getLocalGraphicsEnvironment()).getAvailableFontFamilyNames();
		ArrayList availfonts = new ArrayList<String>(Arrays.asList(fontlist));

		for (int i = 0; i < fonts.length; i++) {
			if (availfonts.contains(fonts[i]))
			{
				// Add font to menu
				JRadioButtonMenuItem newFont = new JRadioButtonMenuItem(fonts[i]);
				fontsGroup.add(newFont);
				fontsMenu.add(newFont);
			
				if (newFont.getText().equals(defaultFont)) {
					newFont.setSelected(true);
				}
			
				// Add an action on fonts items
				newFont.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg) {
						txtArea.setFont(new Font(((JRadioButtonMenuItem) arg.getSource()).getText(), Font.PLAIN, textSize));
					}
				});
			}
		}

		// Languages
		JMenu languagesMenu = new JMenu("Language");
		ButtonGroup languagesGroup = new ButtonGroup();
		String[] languages = {"Chinese", "English", "French", "German", "Japanese", "Spanish", "Russian"};
		
		for (int i = 0; i < languages.length; i++) {
			// Add the languages to the menu
			JRadioButtonMenuItem newLanguage = new JRadioButtonMenuItem(languages[i]);
			languagesGroup.add(newLanguage);
			languagesMenu.add(newLanguage);
			
			if (newLanguage.getText().equals(language)) {
				newLanguage.setSelected(true);
			}
			
			// Add an action on languages items
			newLanguage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg) {
					if (language != ((JRadioButtonMenuItem) arg.getSource()).getText()) {
						language = ((JRadioButtonMenuItem) arg.getSource()).getText();
						JOptionPane.showMessageDialog(mainWindowReference, GetStringForLang("RestartTranslate"), GetStringForLang("Notice"), JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
		
		// Add the items to the option menu
		optionMenu.add(increaseAction);
		optionMenu.add(decreaseAction);
		optionMenu.addSeparator();		// Separator
		optionMenu.add(lineNumberingAction);
		optionMenu.add(wordWarpAction);
		optionMenu.add(statusBarAction);
		optionMenu.addSeparator();		// Separator
		optionMenu.add(colorsAction);
		/*optionMenu.add(fontAction);*/
		optionMenu.add(fontsMenu);
		/*optionMenu.add(languageAction);*/
		optionMenu.add(languagesMenu);
		
		// Text operations menu
		JMenu operationsMenu = new JMenu("Operations");
		menuBar.add(operationsMenu);
		// Create the items of the operations menu
		JMenuItem encryptionAction = new JMenuItem("Encrypt");		// Offers BlowFish, XOR, DES, AES256, etc. 
		JMenuItem encodeAction = new JMenuItem("Encode");		// Base64 en URL encoding/decoding
		JMenuItem hashesAction = new JMenuItem("Hash");		// Show hashes
		JMenuItem countAction = new JMenuItem("Counts");		// Word count, line count, caracter count, file size, etc.
		JMenuItem runAction = new JMenuItem("Run");
		// Add
		operationsMenu.add(encryptionAction);
		operationsMenu.add(encodeAction);
		operationsMenu.add(hashesAction);
		operationsMenu.add(countAction);
		operationsMenu.addSeparator();		// Separator
		operationsMenu.add(runAction);
		
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
		
		hashesAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				try {
					String[] functions = {"MD5", "SHA-1", "SHA-256"};
					
					String hashes = "UTF-8 Encoded: \n\n";
					byte[] bytesUTF = txtArea.getText().getBytes("UTF-8");
					
					for (int i = 0; i < functions.length; i++) {
						MessageDigest digest = MessageDigest.getInstance(functions[i]);
						byte[] bytes = digest.digest(bytesUTF);
						String str = DatatypeConverter.printHexBinary(bytes);
						hashes += functions[i] + ":  " + str + '\n';
					}
					
					hashes += "\n\n" + "ASCII Encoded: \n\n";
					bytesUTF = txtArea.getText().getBytes("US-ASCII");
					
					for (int i = 0; i < functions.length; i++) {
						MessageDigest digest = MessageDigest.getInstance(functions[i]);
						byte[] bytes = digest.digest(bytesUTF);
						String str = DatatypeConverter.printHexBinary(bytes);
						hashes += functions[i] + ": " + str + '\n';
					}
					
					hashes += "\n\n" + "ISO-8859-1 Encoded: \n\n";
					bytesUTF = txtArea.getText().getBytes("ISO-8859-1");
					
					for (int i = 0; i < functions.length; i++) {
						MessageDigest digest = MessageDigest.getInstance(functions[i]);
						byte[] bytes = digest.digest(bytesUTF);
						String str = DatatypeConverter.printHexBinary(bytes);
						hashes += functions[i] + ": " + str + '\n';
					}

					JOptionPane.showMessageDialog(mainWindowReference, hashes, GetStringForLang("Hashing"), JOptionPane.PLAIN_MESSAGE);
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		
		countAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				JOptionPane.showMessageDialog(mainWindowReference, GetStringForLang("Word Count: \nLineCount: \nCharacter Count:\n"), GetStringForLang("Counts"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		runAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				JOptionPane.showMessageDialog(mainWindowReference, GetStringForLang("Command line:"), GetStringForLang("Run"), JOptionPane.INFORMATION_MESSAGE);
				try {
					Runtime rt = Runtime.getRuntime();
					Process pr = rt.exec("calc.exe");
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent comp) {
				// the component was resized...
				System.out.println("window resized: (" + mainWindowReference.getWidth() + "," + mainWindowReference.getHeight() + ")");
				scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
			}
		});
		this.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent ev) {
				System.out.println("window state changed: " + ev);
				scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
			}
		});
		/*
		txtArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lengthLabel.setText("label " + txtArea.getText());
			}
		});
		*/
		txtArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				lengthLabel.setText(GetStringForLang("Lenght") + " " + (txtArea.getText()).length());
			}
			public void insertUpdate(DocumentEvent e) {
				lengthLabel.setText(GetStringForLang("Lenght") + " " + (txtArea.getText()).length());
			}
			public void removeUpdate(DocumentEvent e) {
				lengthLabel.setText(GetStringForLang("Lenght") + " " + (txtArea.getText()).length());
			}
		});
		
		txtArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				try {
					lineLabel.setText(GetStringForLang("Line") + " " + txtArea.getLineOfOffset(txtArea.getCaretPosition()));
				} catch (BadLocationException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		
		// Size the window correctly
		this.setSize(400, 300);
		scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
		
	}
	
	public static void main(String[] args) {
		Editor window = new Editor();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
