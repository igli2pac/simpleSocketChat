
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Bjorna
 */
public class chatServer {

    // Zur Verwaltung aller Outputstreams, damit jeder Client die
    // Nachrichten erhaelt
    ArrayList<PrintWriter> clientOutputStreams;
    PrintWriter writer = null;

    public class ClientHandler implements Runnable {

        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message = "";
            try {
                while (!message.contains("exit")) {

                    try {
                        // Die Nachricht des Clients empfangen und speichern
                        message = reader.readLine();
                        
                        // Alle Clients mit der Nachricht versorgen, die
                        // in der Liste gespeichert sind
                        // Schleife plus Stream schicken
                        int nr = 0;
                        for(int i=1;i<=clientOutputStreams.size();i++){
                            //System.out.println(clientOutputStreams.size());
                            System.out.println(message);
                            if(!message.equals("")){
                            clientOutputStreams.get(i).println(message);
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(chatServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
           
            // fehlt ein catch-Zweig, wird erst benoetigt, wenn
            // Inhalt der while-Schleife vorhanden ist
            } finally {
                clientOutputStreams.remove(writer);
                try {
                    sock.close();
                    System.out.println("Client disconnected!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }

    }

    public static void main(String[] args) {

        try {
            new chatServer().go();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void go() throws IOException {
        clientOutputStreams = new ArrayList();
        ServerSocket serverSock = null;
        try {
            // ServerSocket auf Port 5000 oeffnen
            serverSock = new ServerSocket(5000);
            
            System.out.println("Server running!");
            
            // Auf Clients warten
            while (true) {
                Socket clientSocket = serverSock.accept();
                System.out.println("Client connected!");
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                // Liste mit OutputStreams fuellen
                clientOutputStreams.add(writer);

                // jeden Client in einem Thread laufen lassen
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            serverSock.close();
            System.out.println("Server closed!");
        }
    }
}
