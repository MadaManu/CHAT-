/*
@authors: Vladut Madalin Druta
		Antonio Nikolova
		Mark Whelan
*/


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
//import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.awt.FlowLayout;
//import java.awt.GridBagLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
//import javax.swing.JEditorPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
//import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
//import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Gui extends Observable implements MouseListener, ActionListener,
		KeyListener, WindowListener {

	private String serverName = "";
	private boolean isConnectedToServer;
	private JFrame theWindow;
	private JLabel conectionInfo;
	private JTextField inputChat;
	private JTextPane chatLines;
	private JTextPane onlineList;

	private JTextField userName = new JTextField();
	private JToggleButton audioButton;

	public Gui(int width, int height, boolean isConnToSrv) {

		// add listener on the audioButton :)
		// also when mouse hoovering over the username display an infromation
		// panel

		// DON'T FORGET TO KEEP EVERYTHING AS TIDY AS POSSIBLE AND STRUCTURE
		// THEM ON METHODS AS MUCH AS YOU CAN!

		//
		// MTU timestamp (3 int) + 10max.char(name) + 256 char (message) + CRC
		// check + byte(check what type of data is sent) MTU

		// MTU byte(check data) + name + rest + crc MTU audio/video
		//

		this.isConnectedToServer = isConnToSrv;

		theWindow = new JFrame();

		theWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		theWindow.setBounds(30, 30, width, height);
		theWindow.setResizable(false);

		JPanel conectivity = new JPanel();

		JButton connectDisconect = new JButton();
		connectDisconect.setBounds(15, 15, 200, 250);
		connectDisconect.addActionListener(this);
		theWindow.addWindowListener(this);
		conectionInfo = new JLabel();
		conectionInfo.setEnabled(false);

		chatLines = new JTextPane();

		// theMessgs.setBounds(0, 0, 100, 100);
		// theMessgs.setRows(30);
		// theMessgs.setSize(600, 300);

		inputChat = new JTextField(40);
		inputChat.setText("Click and add text to chat");
		inputChat
				.setDocument(new FixedSizeDocument(Mediator.MAX_MESSAGE_LENGTH));
		inputChat.addMouseListener((MouseListener) this);
		JButton sendChatLine = new JButton();
		sendChatLine.setText("Send");
		sendChatLine.addActionListener((ActionListener) this);
		sendChatLine.setMargin(null);
		sendChatLine.setBounds(0, 0, 100, 10);
		inputChat.addKeyListener((KeyListener) this);

		if (isConnectedToServer) {
			connectDisconect.setText("Disconnect");
			conectionInfo.setText("Connected to " + serverName);
			addStringToChat("System", 0, 0, 0, "No chat!");
		} else {
			connectDisconect.setText("Connect!");
			conectionInfo.setText("Not connected!");
			addStringToChat("System", 0, 0, 0, "You are not connected...");
		}

		conectivity.add(connectDisconect);
		conectivity.add(conectionInfo);

		Dimension sizeForTheChat = new Dimension(650, 250);
		chatLines.setPreferredSize(sizeForTheChat);
		chatLines.setEditable(false);
		chatLines.setAutoscrolls(true);
		JPanel theMessages = new JPanel();
		JScrollPane theScroller = new JScrollPane(chatLines);
		theScroller.setAutoscrolls(true);
		theMessages.add(theScroller);

		JPanel chatInputLine = new JPanel();
		chatInputLine.add(inputChat);
		inputChat.setName("chatline");
		chatInputLine.add(sendChatLine);

		Dimension sizeForTheOnlineList = new Dimension(200, 150);
		onlineList = new JTextPane();
		onlineList.setPreferredSize(sizeForTheOnlineList);
		onlineList.setEditable(false);

		// <-- Getting the first line look nice for the Online users List
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, Color.black);
		SimpleAttributeSet underlineText = new SimpleAttributeSet();
		StyleConstants.setUnderline(underlineText, true);
		onlineList.setCharacterAttributes(set, true);
		Document doc = onlineList.getStyledDocument();
		try {
			doc.insertString(0, "      ", set);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		try {
			doc.insertString(doc.getLength(), "~~| Users Online |~~",
					underlineText);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// -->

		JPanel peopleOnlinePanel = new JPanel();
		JScrollPane anotherScroll = new JScrollPane(onlineList);
		anotherScroll.setPreferredSize(sizeForTheOnlineList);
		peopleOnlinePanel.add(anotherScroll);

		userName.setName("username");
		userName.addKeyListener((KeyListener) this);
		userName.addMouseListener((MouseListener) this);
		JPanel userNamePanel = new JPanel();
		JLabel userNameDisplay = new JLabel();
		userNameDisplay.setText("Username:");
		userNamePanel.add(userNameDisplay);
		userNamePanel.add(userName);
		peopleOnlinePanel.add(userNamePanel);

		JPanel audioPanel = new JPanel();
		audioButton = new JToggleButton();
		audioButton.setName("audio-toggle");
		audioButton.addMouseListener((MouseListener) this);
		// audioButton.setText("Audio");
		ImageIcon image = new ImageIcon("microphone_icon.png");

		Image img = image.getImage();
		Image newimg = img.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newimg);

		JLabel theVolumeLabel = new JLabel("Volume:");
		JLabel theMicrophoneLabel = new JLabel("Talk:");

		// this is gonna be the volume slide
		JSlider theVolumeSlider = new JSlider();

		// theVolumeLabel.setBounds(0, 0, 100, 10);
		// theVolumeSlider.setMaximum(100);
		// theVolumeSlider.setMinimum(0);
		// theVolumeSlider.setSize(40, 10);
		// theVolumeSlider.setBounds(80, 0,40, 20);
		theVolumeSlider.setBorder(null);

		audioButton.setIcon(newIcon);
		audioButton.setBorder(null);

		// audioButton.setMargin(null);
		audioPanel.add(theVolumeLabel);
		audioPanel.add(theVolumeSlider);
		audioPanel.add(theMicrophoneLabel);
		audioPanel.add(audioButton);

		// theWindow.setLayout(new BorderLayout());
		theWindow.setLayout(null); // doing the position on the panels using the
									// "processing way" - specifying where the
									// things should go
		// this can be done by using the setBounds ( x , y , width, height ) ;

		conectivity.setBounds(width / 2 - 250, 0, 500, 30);
		theMessages.setBounds(20, 45, 650, 250);
		userNamePanel.setBounds(650, 45, 250, 30);
		peopleOnlinePanel.setBounds(690, 90, 200, 150);

		chatInputLine.setBounds(20, 300, 550, 30);

		audioPanel.setBounds(600, 290, 200, 100);

		theWindow.add(conectivity);
		theWindow.add(theMessages);
		theWindow.add(peopleOnlinePanel);
		theWindow.add(chatInputLine);
		theWindow.add(userNamePanel);
		theWindow.add(audioPanel);

		theWindow.setVisible(true);

	}

	public void setConnectionInfoLabel(String server) {
		serverName = server;
		conectionInfo.setText("Connected to " + serverName);
	}

	public void addUserName(String name) {
		userName.setText(name);
	}

	// method to add users to the online list
	public void addUserToList(String user) {
		// Document doc = onlineList.getStyledDocument();
		if (isConnectedToServer) {
			SimpleAttributeSet set = new SimpleAttributeSet();
			StyleConstants.setForeground(set, Color.green);
			StyleConstants.setBold(set, true);

			onlineList.setCharacterAttributes(set, true);
			Document doc = onlineList.getStyledDocument();
			try {
				doc.insertString(doc.getLength(), "\n - " + user, set);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

	}

	// method to remove users from the online list
	public void removeUserFromList(String user) {
		Document doc = onlineList.getStyledDocument();
		try {
			String temp = doc.getText(0, doc.getEndPosition().getOffset());
			System.out.println(temp);
			int index = temp.indexOf(user);
			System.out.println("Index : " + index + "length" + user.length());
			doc.remove(index - 4, user.length() + 4);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void removeAllFromList() {
		Document doc = onlineList.getStyledDocument();
		try {
			String temp = doc.getText(0, doc.getEndPosition().getOffset());
			System.out.println(temp);
			String header = "      ~~| Users Online |~~";
			int index = temp.indexOf(header);
			System.out.println("Index : " + header.length() + "length"
					+ temp.length());
			doc.remove(header.length(), temp.length() - 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void addStringToChat(String user, int hour, int minute, int second,
			String toAdd) { // this method is public because we gonna use this
							// in main to add chat lines!
		if (user.equals("System")) {
			SimpleAttributeSet set = new SimpleAttributeSet();
			StyleConstants.setForeground(set, Color.red);
			StyleConstants.setBold(set, true);

			Document doc = new DefaultStyledDocument();

			try {

				doc.insertString(0, user + ": " + toAdd, set);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			chatLines.setStyledDocument((StyledDocument) doc);
		} else if (isConnectedToServer) {
			SimpleAttributeSet set = new SimpleAttributeSet();
			if (user.equals("me")) {
				StyleConstants.setBold(set, true);
				// Set the attributes before adding text
				chatLines.setCharacterAttributes(set, true);
			} else {
				if (StyleConstants.isBold(set))
					StyleConstants.setBold(set, false);

				StyleConstants.setItalic(set, true);

				// Set the attributes before adding text
				chatLines.setCharacterAttributes(set, true);
			}
			Document doc = chatLines.getStyledDocument();

			try {
				doc.insertString(0, user + "[" + hour + ":" + minute + ":"
						+ second + "]: " + toAdd + "\n", set);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

	}

	private void sendChatText() {
		if (inputChat.getText().equals(""))
			return;
		if (isConnectedToServer) {
			setChanged();
			notifyObservers(inputChat.getText());

			Calendar calendar = new GregorianCalendar();
			addStringToChat("me", calendar.get(Calendar.HOUR),
					calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND), inputChat.getText());
			inputChat.setText("");
			// System.out.println(calendar.get(Calendar.AM_PM));
		} else {
			addStringToChat("System", 0, 0, 0, "Sorry you are not connected");
			inputChat.setText("!! Sorry you are not connected !!");
		}
	}

	public void actionPerformed(ActionEvent e) {
		JButton theBut = (JButton) e.getSource();
		if (e.getActionCommand().equals("Connect!")) {
			isConnectedToServer = true;
			theBut.setText("Disconnect");
			conectionInfo.setText("Connected to " + serverName);
			addStringToChat("System", 0, 0, 0, "No chat!");
			setChanged();
			notifyObservers(true);

		} else if (e.getActionCommand().equals("Disconnect")) {
			isConnectedToServer = false;
			theBut.setText("Connect!");
			conectionInfo.setText("Not connected!");
			addStringToChat("System", 0, 0, 0, "You are not connected...");
			setChanged();
			notifyObservers(false);
			// System.out.println("HERRRRRRRREEEEEEEEEEEEEEEEEEEEEE!");
		} else if (e.getActionCommand().equals("Send")) {
			sendChatText();
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getComponent().getName().equals("chatline")) {
			inputChat.setText("");
		} else if (e.getComponent().getName().equals("username")) {
			userName.setText("");
		} else if (e.getComponent().getName().equals("audio-toggle")) {
			JToggleButton temp;
			temp = (JToggleButton) e.getComponent();
			boolean isSelected;
			if (temp.isSelected()) {
				isSelected = temp.isSelected();
				AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
				TargetDataLine line;
				DataLine.Info info = new DataLine.Info(TargetDataLine.class,
						format); // format is an AudioFormat object
				if (!AudioSystem.isLineSupported(info)) {
					// Handle the error ...
				}
				// Obtain and open the line.
				try {
					line = (TargetDataLine) AudioSystem.getLine(info);
					line.open(format);

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					int numBytesRead;
					byte[] data = new byte[line.getBufferSize() / 5];

					// Begin audio capture.
					line.start();

					// Here, stopped is a global boolean set by another thread.
//					while (isSelected) {
						// Read the next chunk of data from the TargetDataLine.
						numBytesRead = line.read(data, 0, data.length);
						// Save this chunk of data.
						out.write(data, 0, numBytesRead);
						System.out.println("numBytesRead: "+numBytesRead);
						byte audio[] = out.toByteArray();
						System.out.println("bytes length: "+audio.length);
						
//						THIS IS TO PLAY THE SOUND
						
//						byte audio[] = out.toByteArray();
						  InputStream input = new ByteArrayInputStream(audio);
						  AudioInputStream ais = new AudioInputStream(input, 
						    format, audio.length / format.getFrameSize());
						  DataLine.Info info1 = new DataLine.Info(
			                         SourceDataLine.class, format);
			  SourceDataLine line1 = 
			             (SourceDataLine)AudioSystem.getLine(info1);
			  line1.open(format);
			  line1.start();
			  int bufferSize = (int) format.getSampleRate() 
					    * format.getFrameSize();
					  byte buffer[] = new byte[bufferSize];

					  int count;
					  try {
						while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
						    if (count > 0) {
						      line1.write(buffer, 0, count);
						    }
						  }
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					  line1.drain();
					  line1.close();
						
//					}
				} catch (LineUnavailableException ex) {
					// Handle the error ...
				}

				System.out.println("YOU HAVE SELECTED THE BUTTON!");
				// this means that the button is selected now and the audio
				// should be recorded!
			} else {
				isSelected = temp.isSelected();
				System.out.println("DESELECT!");
				// this means the button is deselected so the audio should stop!
			}

		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 10) { // if u press enter
			if (e.getComponent().getName().equals("chatline")) {
				sendChatText();
			} else if (e.getComponent().getName().equals("username")) {
				System.out
						.println("U PRESSED ENTER WHEN U ARE IN THE USERNAME!!!");

				// We need a way to let the user change it's name --- here it
				// should be done, so it will have assignmed a default name
				// but if he changes it and presses enter it should change it to
				// whatever he picked :)

			}
		}

	}

	public void windowClosed(WindowEvent arg0) {
		setChanged();
		notifyObservers(arg0);
		System.out.println("Closing Window");

	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}
}
