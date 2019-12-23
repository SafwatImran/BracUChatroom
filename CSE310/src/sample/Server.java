package sample;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    //constructor
    public Server (){
        super("Safwat's ChatApp");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
                });
        add(userText, BorderLayout.NORTH);
        chatWindow=new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(400,200);
        setVisible(true);
    }

    //setup and run the server
    public void startRunning(){
        try{
            server = new ServerSocket(6789, 100);
            while(true){
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eofException){
                    showMessage("\n Server connection ended.");
                }finally{
                    closeAll();
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //wait for connection, then display connection information
    private void waitForConnection() throws IOException {
        showMessage("waiting for someone to connect....\n");
        connection = server.accept();
        showMessage("Connection established with :" + connection.getInetAddress().getHostName());
    }
        //get stream to send and receive data
    private void setupStreams() throws IOException{
    output = new ObjectOutputStream(connection.getOutputStream());
    output.flush();
    input = new ObjectInputStream(connection.getInputStream());
    showMessage("\n Streams are now setup! \n");
    }

    //during conversation
    private void whileChatting() throws IOException{
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n"+ message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n Unknown type of object \n");
            }
        }while(!message.equals("CLIENT - END"));
    }

    //close streams and sockets
    private void closeAll(){
        showMessage("\n Closing connections... \n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //sends messages to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n Error: Can't send message ");
        }
    }

    //updates chatWindow
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    @Override
                    public void run() {
                        chatWindow.append(text);
                    }
                }
        );
    }

    //allows user to type
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable(){
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }

}

