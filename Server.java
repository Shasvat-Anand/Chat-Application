import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;


 
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Server extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading =new JLabel("Server Area");
    private JTextArea messagArea=new JTextArea();
    private JTextField messageInput= new JTextField();
    private Font font =new Font("Roboto",Font.BOLD,20);

    // constructor

    public Server(){
        try{
            server=new ServerSocket(7777);
            System.out.println("server is ready for connection:");
            System.out.println("waiting...");
            socket=server.accept();

            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());

            
            creategui();
            handleEvents();

            startReading();
            // startWriting();
        }

        catch(Exception e){
            // e.printStackTrace();
        }
    }
    

    public void creategui(){
        this.setTitle("Server MessageEnd");
        this.setSize( 500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messagArea.setFont(font);
        messageInput.setFont(font);

        ImageIcon originalIcon =new ImageIcon("young-man.png");
        Image reszizImage=originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        heading.setIcon( new ImageIcon(reszizImage));


        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        messagArea.setEditable(false);

        this.setLayout( new BorderLayout());

        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messagArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);



    }

    public void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
             }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
             }

            @Override
            public void keyReleased(KeyEvent e) {
            
                if (e.getKeyCode()==10){
                    
                    String contenttosend=messageInput.getText();
                    messagArea.append("Me : "+ contenttosend+"\n");
                    messagArea.setCaretPosition(messagArea.getDocument().getLength());
                    out.println(contenttosend);
                    out.flush();
                    messageInput.setText( " ");
                    messageInput.requestFocus();
                }
            }
            
        });
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                out.println("exit"); // Send exit message to the server
                out.flush();
                closeChat();
            }

            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }
    
   
   
           
        
        
    public void startReading(){

        // use thread for reading data
        Runnable r1=()->{
            System.out.println("reader started....");
            try{
            while (true) {
               
                String msg= br.readLine();
                if(msg.equals("exit")){
                    System.out.println("Client terminate the chat.");
                    JOptionPane.showMessageDialog(this, "Client terimated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    System.exit(0);
                    break;
                }
                messagArea.append("Client : "+msg+"\n");
                messagArea.setCaretPosition(messagArea.getDocument().getLength());
                
            }}catch(Exception e){
                // e.printStackTrace();
                System.out.println("Reader is closed");
            }

        };
        new Thread(r1).start();

    }
    public void startWriting(){

        // use thread for writing data
        Runnable r2=()->{
            System.out.println("writer started...");
            try {
            while (!socket.isClosed()) {

                
                    BufferedReader br1= new BufferedReader(new InputStreamReader(System.in));
                    String content =br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        System.exit(0);
                        break;
                    }
            }        
                
            System.out.println("Writer is closed");
            
        } catch (Exception e) {
            e.printStackTrace();
       }

        };
        new Thread(r2).start();
    }


    private void closeChat() {
        try {
            socket.close(); // Close the socket
        } catch (Exception e) {
            System.out.println("Error while closing socket: " + e.getMessage());
        }
        System.exit(0); // Exit the application
    }




    public static void main(String[] args) {
        new Server();
    }
    
}
