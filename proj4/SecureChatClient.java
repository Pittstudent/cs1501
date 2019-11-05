import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

	public static final int PORT = 8765;
	ObjectOutputStream myWriter;
	ObjectInputStream  myReader;
	BigInteger e;
	BigInteger n;
	JTextArea outputArea;
	JLabel prompt;
	JTextField inputField;
	String myName, serverName, encrType;
	Socket connection;
	SymCipher cipher;

	public SecureChatClient ()
	{
		try{
			myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
			serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
			InetAddress addr =
					InetAddress.getByName(serverName);
			connection = new Socket(addr, PORT);   // Connect to server with new
			
			myWriter = new ObjectOutputStream(connection.getOutputStream());
			myWriter.flush();
			myReader = new ObjectInputStream(connection.getInputStream());
			
			e = (BigInteger)myReader.readObject();
			n = (BigInteger)myReader.readObject();

			encrType = (String)myReader.readObject();
			
			if (encrType.equals("Add")){
				System.out.println("\nThe type of symmetric encryption: Add128");
				cipher = new Add128();
			}else{
				System.out.println("\nThe type of symmetric encryption: Substitute");
				cipher = new Substitute();
			}
			byte[] keyTemp = cipher.getKey();
			BigInteger key = new BigInteger(1, keyTemp);
			System.out.println("\nN: "+n);
			System.out.println("\nE: "+e);
			System.out.println("\nSymmetric Key: "+key);

			key = key.modPow(e, n);
			myWriter.writeObject(key);
			myWriter.flush();
			byte[] encName = cipher.encode(myName);
			byte [] msg1 = myName.getBytes();
			BigInteger biRep = new BigInteger(1, msg1);
			BigInteger biEnc = new BigInteger(1, encName);
			System.out.println("\n\t Encryption \n------------------------------------------------------------------");
			System.out.println("Original String Message:\t "+myName);
			System.out.println("The corresponding array of bytes:\t "+biRep);
			System.out.println("The encrypted array of bytes:\t "+biEnc);
			myWriter.writeObject(encName);
			myWriter.flush();

			this.setTitle(myName);      // Set title to identify chatter
			Box b = Box.createHorizontalBox();  // Set up graphical environment for
			outputArea = new JTextArea(8, 30);  // user
			outputArea.setEditable(false);
			b.add(new JScrollPane(outputArea));
			outputArea.append("Welcome to the Chat Group, " + myName + "\n");
			inputField = new JTextField("");  // This is where user will type input
			inputField.addActionListener(this);
			prompt = new JLabel("Type your messages below:");
			Container c = getContentPane();
			c.add(b, BorderLayout.NORTH);
			c.add(prompt, BorderLayout.CENTER);
			c.add(inputField, BorderLayout.SOUTH);

			Thread outputThread = new Thread(this);  // Thread is to receive strings
			outputThread.start();					// from Server
			addWindowListener(
					new WindowAdapter(){
						public void windowClosing(WindowEvent e){ 
							try{
								myWriter.writeObject(cipher.encode("CLIENT CLOSING"));
								myWriter.flush();
								byte[] encName = cipher.encode("CLIENT CLOSING");
								byte [] msg1 = "CLIENT CLOSING".getBytes();
								BigInteger biRep = new BigInteger(1, msg1);
								BigInteger biEnc = new BigInteger(1, encName);
								System.out.println("\n\t Encryption \n------------------------------------------------------------------"+
													"\nOriginal String Message:\t CLIENT CLOSING"+
													"\nThe corresponding array of bytes:\t "+biRep+
													"\nThe encrypted array of bytes:\t "+biEnc);	
								myWriter.close();
							}catch (IOException ex){
								ex.printStackTrace();
							}
						 	System.exit(0);
						}
					}
				);
			setSize(500, 200);
			setVisible(true);

		}catch (Exception exception){
			exception.printStackTrace();
			System.out.println("Problem starting client!");
		}
	}

	public void run()
	{
		while (true)
		{
			 try{
				byte[] tempMsg = (byte[])myReader.readObject();
				String currMsg = cipher.decode(tempMsg);
				byte [] msg1 = currMsg.getBytes();
				BigInteger biRep = new BigInteger(1, tempMsg);
				BigInteger biDcrypt = new BigInteger(1, msg1);

				System.out.println("\n\t Decryption \n------------------------------------------------------------------"+
									"\nThe array of bytes received:\t "+biRep+
									"\nThe decrypted array of bytes:\t "+biDcrypt+				
									"\nThe Corresponding String Message:\t "+currMsg);

				outputArea.append(currMsg+"\n");
			 }
			 catch (Exception e){
				System.out.println(e +  ", closing client!");
				break;
			 }
		}
		System.exit(0);
	}

	public void actionPerformed(ActionEvent e)
	{
		String currMsg = e.getActionCommand();	  // Get input value
		inputField.setText("");
		try{
			myWriter.writeObject(cipher.encode(myName+": "+currMsg));
			myWriter.flush();   // Add name and send it
	
			byte [] msg2 = (myName+": "+currMsg).getBytes();
			BigInteger biRep = new BigInteger(1, msg2);
			BigInteger biEnc = new BigInteger(1, cipher.encode(myName+": "+currMsg));
			System.out.println("\n\t Encryption \n------------------------------------------------------------------"+
				"\nThe Original String Message:\t "+myName+": "+currMsg+
				"\nThe corresponding array of bytes:\t "+biRep+
				"\nThe encrypted array of bytes:\t "+biEnc);
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}											

	public static void main(String [] args){
		 SecureChatClient JR = new SecureChatClient();
		 JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}
