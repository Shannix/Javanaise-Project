
/**
 * *
 * Irc class : simple implementation of a chat using JAVANAISE Contact:
 *
 * Authors:
 */
import JvnObject.Interfaces.JvnObject;
import Server.JvnServerImpl;
import static Server.JvnServerImpl.js;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvn.JvnException;

public class Irc {

    public TextArea text;
    public TextField data;
    Frame frame;
    JvnObject sentence;

    /**
     * main method create a JVN object nammed IRC for representing the Chat
     * application
     *
     */
    public static void main(String argv[]) {
        try {
            // initialize 
            JvnServerImpl js = JvnServerImpl.jvnGetServer();

            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            JvnObject jo = js.jvnLookupObject("IRC");
            //JvnObject jo = null;

            if (jo == null) {
                jo = js.jvnCreateObject((Serializable) new Sentence());
                // after creation, I have a write lock on the object
                js.jvnRegisterObject("IRC", jo);
                jo.jvnUnLock();
            }
            // create the graphical part of the Chat application
            new Irc(jo);
            /*int i = 0;
            sleep(5000);
            while (i <= 10) {
                // lock the object in read mode
                jo.jvnLockRead();
                // invoke the method
                String s = ((Sentence) (jo.jvnGetObjectState())).read();
                System.out.println("READ " + s);
                //unlock the object
                jo.jvnUnLock();

                // get the value to be written from the buffer
                s = "Ecriture" + "_" + i++;
                System.out.println("WRITE " + s);
                // lock the object in write mode
                jo.jvnLockWrite();
                // invoke the method
                ((Sentence) (jo.jvnGetObjectState())).write(s);
                jo.jvnUnLock();
                sleep(2000);
            }*/
        } catch (Exception e) {
            System.out.println("IRC problem : " + e);
        }
    }

    /**
     * IRC Constructor
     *
     * @param jo the JVN object representing the Chat
     *
     */
    public Irc(JvnObject jo) {
        sentence = jo;
        frame = new Frame();
        frame.setLayout(new GridLayout(1, 1));
        text = new TextArea(10, 60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);
        data = new TextField(40);
        frame.add(data);

        Button read_button = new Button("read");
        read_button.addActionListener(new readListener(this));
        frame.add(read_button);

        Button write_button = new Button("write");
        write_button.addActionListener(new writeListener(this));
        frame.add(write_button);

        Button disconnect_button = new Button("disconnect");
        disconnect_button.addActionListener(new disconnectListener(this));
        frame.add(disconnect_button);

        Button unlock_button = new Button("unlock");
        unlock_button.addActionListener(new unlockListener(this));
        frame.add(unlock_button);

        frame.setSize(545, 201);
        text.setBackground(Color.black);
        frame.setVisible(true);
    }
}

class unlockListener implements ActionListener {

    Irc irc;

    public unlockListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     *
     */
    public void actionPerformed(ActionEvent e) {
        try {
            // get the value to be written from the buffer
            String s = irc.data.getText();

            // unlock the object
            irc.sentence.jvnUnLock();

            System.out.println("UNLOCK");
        } catch (JvnException je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }
    }
}

class disconnectListener implements ActionListener {

    Irc irc;

    public disconnectListener(Irc i) {
        irc = i;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            js.jvnTerminate();
        } catch (JvnException ex) {
            Logger.getLogger(disconnectListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/**
 * Internal class to manage user events (read) on the CHAT application
 *
 */
class readListener implements ActionListener {

    Irc irc;

    public readListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     *
     */
    public void actionPerformed(ActionEvent e) {
        try {
            // lock the object in read mode
            irc.sentence.jvnLockRead();
            // invoke the method
            String s = ((Sentence) (irc.sentence.jvnGetObjectState())).read();

            //unlock the object
            //irc.sentence.jvnUnLock();
            // display the read value
            irc.data.setText(s);
            irc.text.append(s + "\n");
        } catch (JvnException je) {
            System.out.println("IRC problem : " + je.getMessage());
        }
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 *
 */
class writeListener implements ActionListener {

    Irc irc;

    public writeListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     *
     */
    public void actionPerformed(ActionEvent e) {
        try {
            // get the value to be written from the buffer
            String s = irc.data.getText();

            // lock the object in write mode
            irc.sentence.jvnLockWrite();
            // invoke the method
            ((Sentence) (irc.sentence.jvnGetObjectState())).write(s);

            // unlock the object
            //irc.sentence.jvnUnLock();
        } catch (JvnException je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }
    }
}
