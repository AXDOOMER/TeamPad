// Copyright (C) 2015-2017  Alexandre-Xavier Labonté-Lamoureux

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

// How to compile and execute: 
// javac -encoding UTF8 Editor.java
// java Editor

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;		// import java.util.Arrays;
import java.io.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.xml.bind.DatatypeConverter;
import java.net.*;		// Sockets
import java.util.zip.*;		// GZIP streams


public class Editor extends JFrame {

	static Editor mainWindowReference;
	static String language = "English";
	int tabSize = 4;	// Get the default tab size			int tabSize = textarea.getTabSize(); // 8
	String defaultFont = "Courier New";
	int textSize = 14;		// zoom
	final float textSizeIncrement = 2.0f;		// zoom increment
	boolean lineNumbering = false;		// lines
	boolean lineWarp = false;		// wrap
	boolean statusBar = true;		// stats
	Map<String, String> configs = new HashMap<String, String>();
	String lastSelectedDirectory = "";
	Stack<String> undoList = new Stack<String>();
	Stack<String> redoList = new Stack<String>();
	String lastCommand = "calc.exe";
	String lastIP = "127.0.0.1";
	
	// Networking
	int port = 8166;
	Socket sock = null;
	BufferedReader reader = null;
	PrintWriter writer = null;

	Language lang = new Language(language);

	public Editor(String[] args) {
		mainWindowReference = this;
		
		// Benchmarking
		long timeStart = System.currentTimeMillis();

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
		try {
			tabSize = Integer.parseInt(configs.get("tab"));
		} catch (Exception e) {
			System.err.println("'tab' is missing from the configuration file or has an invalid value.");
		}
		try {
			defaultFont = configs.get("font");
		} catch (Exception e) {
			System.err.println("'font' is missing from the configuration file or has an invalid value.");
		}
		try {
			language = configs.get("lang");
		} catch (Exception e) {
			System.err.println("'lang' is missing from the configuration file or has an invalid value.");
		}
		try {
			textSize = Integer.parseInt(configs.get("zoom"));
		} catch (Exception e) {
			System.err.println("'zoom' is missing from the configuration file or has an invalid value.");
		}
		try {
			lineNumbering = Boolean.parseBoolean(configs.get("lines"));
		} catch (Exception e) {
			System.err.println("'lines' is missing from the configuration file or has an invalid value.");
		}
		try {
			lineWarp = Boolean.parseBoolean(configs.get("warp"));
		} catch (Exception e) {
			System.err.println("'warp' is missing from the configuration file or has an invalid value.");
		}
		try {
			statusBar = Boolean.parseBoolean(configs.get("stats"));
		} catch (Exception e) {
			System.err.println("'stats' is missing from the configuration file or has an invalid value.");
		}
		try {
			port = Integer.parseInt(configs.get("port"));
		} catch (Exception e) {
			System.err.println("'port' is missing from the configuration file or has an invalid value.");
		}
		try {
			lastIP = configs.get("lastip");
		} catch (Exception e) {
			System.err.println("'lastip' is missing from the configuration file or has an invalid value.");
		}
		try {
			lastCommand = configs.get("lastcmd");
		} catch (Exception e) {
			System.err.println("'lastcmd' is missing from the configuration file or has an invalid value.");
		}
		
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
		undoList.push(txtArea.getText());

		// Add to the frame, put the textarea in a scrollpane first so we get the scrollbars
		JScrollPane scrollPane = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED/*, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS*/);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		mainFrame.add(scrollPane);
		mainFrame.add(lengthLabel);
		mainFrame.add(lineLabel);
		
		// Add the layout to the frame
		this.add(mainFrame);
		
		// Set the window icon
		this.setIconImage(new ProxyImageIcon("file_edit.gif").getImage());
	
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
		JMenu fileMenu = new JMenu(lang.GetStringForLang("File"));
		menuBar.add(fileMenu);
		// Create items of the file menu
		JMenuItem newAction = new JMenuItem(lang.GetStringForLang("New"), new ProxyImageIcon("Document New-01.png"));
		JMenuItem openAction = new JMenuItem(lang.GetStringForLang("Open"), new ProxyImageIcon("Open_file.png"));
		JMenuItem saveAction = new JMenuItem(lang.GetStringForLang("Save"), new ProxyImageIcon("save.png"));
		JMenuItem saveAsAction = new JMenuItem(lang.GetStringForLang("Save As"), new ProxyImageIcon("saveas_icon.gif"));
		JMenuItem exitAction = new JMenuItem(lang.GetStringForLang("Exit"), new ProxyImageIcon("exit-24-000000.png"));
		// Add the items to the file menu
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();		// Separator
		// The launcher for external programs: 
		JMenuItem runAction = new JMenuItem(lang.GetStringForLang("Run"), new ProxyImageIcon("debug.png"));
		fileMenu.add(runAction);
		fileMenu.addSeparator();	// Separator
		fileMenu.add(exitAction);
		
		exitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				System.exit(0);
			}
		});
		
		openAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				JFileChooser fileChooser = new JFileChooser();
				
				if (lastSelectedDirectory != "") {
					fileChooser.setCurrentDirectory(new File(lastSelectedDirectory));
				}
				
				int returnVal = fileChooser.showOpenDialog(mainWindowReference);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						System.out.println("Opening file: " + fileChooser.getSelectedFile().getName());
						lastSelectedDirectory = fileChooser.getCurrentDirectory().toString();
						BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile().getAbsolutePath()));
						
						undoList = new Stack<String>();
						redoList = new Stack<String>();
						
						txtArea.setText("");
						txtArea.read(reader, null);
						reader.close();
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}
				}
			}
		});

		// Edit menu
		JMenu editMenu = new JMenu(lang.GetStringForLang("Edit"));
		menuBar.add(editMenu);
		// Create the items of the edit menu
		JMenuItem undoAction = new JMenuItem(lang.GetStringForLang("Undo"), new ProxyImageIcon("back_undo.png"));
		JMenuItem redoAction = new JMenuItem(lang.GetStringForLang("Redo"), new ProxyImageIcon("botao-refazer.png"));
		JMenuItem cutAction = new JMenuItem(lang.GetStringForLang("Cut"), new ProxyImageIcon("mai1444425541180_lowres_en-us.png"));
		JMenuItem copyAction = new JMenuItem(lang.GetStringForLang("Copy"), new ProxyImageIcon("icon_copy_n.png"));
		JMenuItem pasteAction = new JMenuItem(lang.GetStringForLang("Paste"), new ProxyImageIcon("1398640998.png"));
		JMenuItem selectAllAction = new JMenuItem(lang.GetStringForLang("Select All"), new ProxyImageIcon("edit_select_all.png"));
		JMenuItem selectLineAction = new JMenuItem(lang.GetStringForLang("Select Line"), new ProxyImageIcon("editor_panel_tab_icon.gif"));
		JMenuItem findAction = new JMenuItem(lang.GetStringForLang("Find and replace"), new ProxyImageIcon("edit_find_replace.png"));
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
		
		undoAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (!undoList.empty()) {
					redoList.push(txtArea.getText());
					txtArea.setText(undoList.pop());
				}
			}
		});
		
		redoAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (!redoList.empty()) {
					undoList.push(txtArea.getText());
					txtArea.setText(redoList.pop());
				}
			}
		});
		
		// Option menu
		JMenu optionMenu = new JMenu(lang.GetStringForLang("Option"));
		menuBar.add(optionMenu);
		// Create the items of the option menu
		JMenuItem increaseAction = new JMenuItem(lang.GetStringForLang("Increase text size"), new ProxyImageIcon("nav_zoomin.png"));
		// Add an action to zoom in
		increaseAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				textSize += textSizeIncrement;
				txtArea.setFont(new Font(txtArea.getFont().getName(), Font.PLAIN, textSize));
			}
		});
		
		JMenuItem decreaseAction = new JMenuItem(lang.GetStringForLang("Decrease text size"), new ProxyImageIcon("nav_zoomout.png"));
		// Add an action to zoom out
		decreaseAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				textSize -= textSizeIncrement;
				txtArea.setFont(new Font(txtArea.getFont().getName(), Font.PLAIN, textSize));
			}
		});

		JCheckBoxMenuItem wordWarpAction = new JCheckBoxMenuItem(lang.GetStringForLang("Word warp"), new ProxyImageIcon("WordWrap.png"));
		JCheckBoxMenuItem statusBarAction = new JCheckBoxMenuItem(lang.GetStringForLang("Status bar"), new ProxyImageIcon("ui-status-bar.png"));
		
		// Fonts
		JMenuItem fontsAction = new JMenuItem(lang.GetStringForLang("Font"), new ProxyImageIcon("truetype.gif"));

		fontsAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				String[] fontlist = (GraphicsEnvironment.getLocalGraphicsEnvironment()).getAvailableFontFamilyNames();
				JComboBox jcb = new JComboBox<String>(fontlist);
				
				jcb.setSelectedItem(txtArea.getFont().getName());
				
				System.out.println("\nCURRENT=" + txtArea.getFont().getName());
				
				String[] options = { "OK" };
				
				int n = (int)JOptionPane.showOptionDialog(
					mainWindowReference, 
					jcb, 
					"Font selection", 
					JOptionPane.DEFAULT_OPTION, 
					JOptionPane.PLAIN_MESSAGE, 
					null,
					options, options[0]);
					
				if (n >= 0) {
					txtArea.setFont(new Font(jcb.getSelectedItem().toString(), Font.PLAIN, textSize));
					System.out.println("\nFONT=" + jcb.getSelectedItem());
				}
			}
		});	

		// Languages
		JMenu languagesMenu = new JMenu(lang.GetStringForLang("Language"));
		languagesMenu.setIcon(new ProxyImageIcon("bubble_icon.gif"));
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
						JOptionPane.showMessageDialog(mainWindowReference, lang.GetStringForLang("RestartTranslate"), lang.GetStringForLang("Notice"), JOptionPane.INFORMATION_MESSAGE);
						lang.setLanguage(language);
					}
				}
			});
		}
		
		// Add the items to the option menu
		optionMenu.add(increaseAction);
		optionMenu.add(decreaseAction);
		optionMenu.addSeparator();		// Separator
		optionMenu.add(wordWarpAction);
		optionMenu.add(statusBarAction);
		optionMenu.addSeparator();		// Separator
		/*optionMenu.add(fontAction);*/
		optionMenu.add(fontsAction);
		/*optionMenu.add(languageAction);*/
		optionMenu.add(languagesMenu);
		
		// Team menu
		JMenu teamMenu = new JMenu(lang.GetStringForLang("Team"));
		menuBar.add(teamMenu);
		// Create the items of the team menu
		JCheckBoxMenuItem allowConnectionsAction = new JCheckBoxMenuItem(lang.GetStringForLang("Allow connections"), new ProxyImageIcon("netico.gif"));
		JMenuItem connectToAction = new JMenuItem(lang.GetStringForLang("Connect to"), new ProxyImageIcon("network_icon.jpg"));
		JMenuItem sendMessageAction = new JMenuItem(lang.GetStringForLang("Send a message"), new ProxyImageIcon("c02228162.jpg"));
		JMenuItem disconnectAllAction = new JMenuItem(lang.GetStringForLang("Disconnect all"), new ProxyImageIcon("icon_disconnect_agent.bmp.png"));
		// Add the items to the team menu
		teamMenu.add(allowConnectionsAction);
		teamMenu.add(connectToAction);
		teamMenu.addSeparator();
		teamMenu.add(sendMessageAction);
		teamMenu.add(disconnectAllAction);

		allowConnectionsAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (sock == null) {
					boolean enabled = allowConnectionsAction.isSelected();
					System.out.println("Allow connections: " + enabled);
					
					if (enabled) {
						System.out.println("Integrated server: Feature not implemented.");
					} else {
						// Don't allow connections
						System.out.println("Don't allow connections");
					}
				} else {
					JOptionPane.showMessageDialog(mainWindowReference, "You must disconnect from any client first. ", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		connectToAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {

				if (!allowConnectionsAction.isSelected()) {
					if (sock == null) {
						String ip = (String)JOptionPane.showInputDialog(
							mainWindowReference,
							lang.GetStringForLang("EnterIP"),
							lang.GetStringForLang("AskIP"),
							JOptionPane.PLAIN_MESSAGE,
							new ProxyImageIcon("network_icon.jpg"),
							null,
							lastIP);

						System.out.println("IP=" + ip);
						
						// Try to connect (this connects to a server)
						if ((ip != null) && (ip.length() > 0)) {
							System.out.println("Connection to... " + ip);
							
							try {
								sock = new Socket(ip, port);	// Timeout?
								reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
								writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);	// autoFlush must be true, else it won't send anything. 
								// Connection successful!
								lastIP = ip;
							} catch (Exception e) {
								System.err.println(e.getMessage());
								//connections.clear();
							}
						}
					} else {
						JOptionPane.showMessageDialog(mainWindowReference, "You can only have one connection at once.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(mainWindowReference, "The integrated server must be disabled. ", "Cannot connect", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		disconnectAllAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (reader == null && writer == null && sock == null) {
					JOptionPane.showMessageDialog(mainWindowReference, "There are no connection to disconnect from.", "Already disconnected", JOptionPane.WARNING_MESSAGE);
				} else {
					// Close any socket or stream
					try {
						if (reader != null) {
							reader.close();
							reader = null;
						}
						if (writer != null) {
							writer.close();
							writer = null;
						}
						if (sock != null) {
							sock.close();
							sock = null;
						}
					} catch (Exception e) {
						System.err.println("There was an error while closing sockets and streams.");
					}
				}
			}
		});
		
		sendMessageAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (sock != null) {
					String m = (String)JOptionPane.showInputDialog(
						mainWindowReference,
						"This will broadcast a message to every connected peer.",
						"Send a message",
						JOptionPane.PLAIN_MESSAGE,
						new ProxyImageIcon("c02228162.jpg"),
						null,
						"");

					System.out.println("message: " + m);
					
					// Send the message if it's valid
					if ((m != null) && (m.length() > 0)) {
						try {
							if (writer != null && sock != null) {
								writer.println(m);
								System.out.println("message is sent... " + m);
							} else {
								System.err.println("Not connected to anyone.");
							}
						} catch (Exception e) {
							System.err.println("message couldn't be sent.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(mainWindowReference, "You are not connected to anyone.", "Can't send a message", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		// Help menu
		JMenu helpMenu = new JMenu(lang.GetStringForLang("Help"));
		menuBar.add(helpMenu);
		// Create the menu items
		JMenuItem licenseAction = new JMenuItem(lang.GetStringForLang("License"), new ProxyImageIcon("License.png"));
		JMenuItem aboutAction = new JMenuItem(lang.GetStringForLang("About"), new ProxyImageIcon("iconInfo.gif"));
		// Add the help to the option menu
		helpMenu.add(licenseAction);
		helpMenu.add(aboutAction);
		
		// Display the window
		this.pack();
		this.setVisible(true);
		
		// Add an action
		aboutAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				//JOptionPane.showMessageDialog(null, "Written by: \nAlexandre-Xavier Labonté-Lamoureux\nCopyright(c) 2015\n\nDistributed under the GNU GPL version 3", "About", JOptionPane.INFORMATION_MESSAGE);
				JOptionPane.showMessageDialog(mainWindowReference, lang.GetStringForLang("Copying"), lang.GetStringForLang("About"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		licenseAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				JOptionPane.showMessageDialog(mainWindowReference, "TeamPad: A simple cooperative text editor\nCopyright (C) 2015-2016  Alexandre-Xavier Labonté-Lamoureux\n\nThis program is free software: you can redistribute it and/or modify\nit under the terms of the GNU General Public License as published by\nthe Free Software Foundation, either version 3 of the License, or\n(at your option) any later version.\n\nThis program is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU General Public License for more details.\n\nYou should have received a copy of the GNU General Public License\nalong with this program.  If not, see <http://www.gnu.org/licenses/>.", lang.GetStringForLang("License"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		runAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				String p = (String)JOptionPane.showInputDialog(
					mainWindowReference,
					lang.GetStringForLang("EnterPath"),
					lang.GetStringForLang("CommandLine"),
					JOptionPane.INFORMATION_MESSAGE,
					new ProxyImageIcon("debug.png"),
					null,
					lastCommand);

				System.out.println("PATH=" + p);
				
				// Start the application
				if ((p != null) && (p.length() > 0)) {
					try {
						Runtime rt = Runtime.getRuntime();
						Process pr = rt.exec(p);	// Execute
						lastCommand = p;
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}
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
			// This counts the file size, not the characters. 
			// If we want to count the characters, we need to remove the special ones (Unicode and control characters).
			public void changedUpdate(DocumentEvent e) {
				lengthLabel.setText(lang.GetStringForLang("Size") + " " + (txtArea.getText()).length());
			}
			public void insertUpdate(DocumentEvent e) {
				lengthLabel.setText(lang.GetStringForLang("Size") + " " + (txtArea.getText()).length());
			}
			public void removeUpdate(DocumentEvent e) {
				lengthLabel.setText(lang.GetStringForLang("Size") + " " + (txtArea.getText()).length());
			}
		});
		
		txtArea.addKeyListener(new KeyListener() { 
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					//e.consume();
					undoList.push(txtArea.getText());
					redoList = new Stack<String>();
				}
				
				// System info
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F12){
					JOptionPane.showMessageDialog(mainWindowReference, 
					"Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n" +
					System.getProperty("os.name") + " (" + System.getProperty("os.version") + ") " + 
					System.getProperty("os.arch") + " " + Runtime.getRuntime().availableProcessors() + "-cores", 
					lang.GetStringForLang("SystemInfo"),
					JOptionPane.INFORMATION_MESSAGE);
				}
			}
			public void keyReleased(KeyEvent e) {
				// Do nothing, Java wants it to be overriden, but we don't want to change its default behavior. 
			}
			public void keyTyped(KeyEvent e) {
				// Do nothing, Java wants it to be overriden, but we don't want to change its default behavior. 
				System.out.print(".");
			}
		});

		txtArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				try {
					lineLabel.setText(lang.GetStringForLang("Line") + " " + (txtArea.getLineOfOffset(txtArea.getCaretPosition()) + 1));
				} catch (BadLocationException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});
		
		// Size the window correctly
		this.setSize(400, 300);
		scrollPane.setPreferredSize(new Dimension(mainWindowReference.getWidth() - 40, mainWindowReference.getHeight() - 100));
		
		// Display benchmark result
		System.out.println("Teampad Init in " + (System.currentTimeMillis() - timeStart) + "ms");
	}
	
	public static void main(String[] args) {
		
		System.out.println("Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor"));
		System.out.println(System.getProperty("os.name") + " (" + System.getProperty("os.version") + ") " + System.getProperty("os.arch") + " " + Runtime.getRuntime().availableProcessors() + "-cores");
		System.out.println("TEAMPAD VERSION 0.1");
		
		// Test: remove it
		try
		{
			Socket clientSocket = null; 
			
			ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(clientSocket.getOutputStream())); 
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(clientSocket.getInputStream())); 

			TextUpdate tu = (TextUpdate) in.readObject();
			out.writeObject(tu);
			out.flush();
			out.close();
			in.close();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		if (CheckParam(args, "-connect") >= 0) {
			System.out.println("Connect to ip parameter detected");
		}
		
		if (CheckParam(args, "-host") >= 0) {
			System.out.println("Create server parameter detected");
		}
		
		if (CheckParam(args, "-l") >= 0) {
			System.out.println("Goto line parameter detected");
		}
		
		if (CheckParam(args, "-f") >= 0) {
			System.out.println("Load file parameter detected");
		}
		
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		Editor window = new Editor(args);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	public static int CheckParam(String[] args, String arg) {
		return Arrays.asList(args).indexOf(arg);
	}
}
