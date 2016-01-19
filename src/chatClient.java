
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Bjorna
 */
public class chatClient extends JFrame {

    static JTextArea jOutput;
    public JTextField jWrite;
    public JButton jSend;
    public JButton jConnect;
    public JLabel jChat;
    public JScrollPane Scroll;
    String host = "localhost";
    int port = 5000;
    String name = "";
    
    PrintWriter out;
    BufferedReader in = null;
    String message;

    public static void main(String[] args) throws IOException {
 
        chatClient chat = new chatClient();
        chat.init();
        chat.setVisible(true);
        chat.run();
    }

    public void init() {

        this.setSize(550, 550);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);

        this.setTitle("Welcome to the ChatServer");
        jChat = new JLabel("Write your message here: ");
        jChat.setSize(200, 30);
        jChat.setLocation(50, 360);
        this.add(jChat);

        jOutput = new JTextArea("Begin group chat. \n");
        jOutput.setSize(300, 300);
        jOutput.setLocation(50, 50);
        jOutput.setBackground(Color.white);
        jOutput.setEnabled(false);
        this.add(jOutput);

        jWrite = new JTextField();
        jWrite.setSize(300, 30);
        jWrite.setLocation(50, 400);
        this.add(jWrite);

        jSend = new JButton("Send message");
        jSend.setSize(150, 20);
        jSend.setLocation(380, 400);
        this.add(jSend);

        jSend.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    jBtnSendActionPerformed();
                } catch (IOException ex) {
                    jOutput.append("Could not perform action. \n");
                }
            }

            private void jBtnSendActionPerformed() throws IOException {

                // Nachricht vom Textfeld lesen und an Server vorbereiten
                message = jWrite.getText();
                
                // Tetfeld wieder leeren
                jWrite.setText("");
                
                //jOutput.append(message + "\n");
                if((message != null) || (!message.equals(""))) {
                    out.println(name + ": " + message.trim());
                }
            }
        });
    }

    private void run() throws IOException {
        
        name = JOptionPane.showInputDialog(
            this,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
        
        this.setTitle("Welcome to the ChatServer "+ name);
        //Verbindung mit Server erstellen
        Socket s = new Socket(host, port);
        
        // Output u InputStreams anlegen
        out = new PrintWriter(s.getOutputStream(), true);
        //String and der Server gesendet werden soll zusammenstellen
        String message = "GET / http/1.1\r\nHost: " +host + "\r\n";
        System.out.println(message);
        //Stream mit message fuellen
        out.println(message);

        //BufferedReader anlegen
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        // Process all messages from server, according to the protocol.
        String response="";
        while (!response.contains("exit")) {

            //Antwort vom Server bekommen
            response = in.readLine();

            // Antwort vom Server im Textbereich anhaengen mit append
            jOutput.append("Msg recieved: "+response+"\n");
            System.out.println("TEsting"+response);
        }
        
        // Streams und Socket schliessen
        s.close();
        in.close();
        out.close();
    }
}
