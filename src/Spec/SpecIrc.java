package Spec;

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

import java.io.*;
import static java.lang.Thread.sleep;

public class SpecIrc implements Runnable {

    public TextArea text;
    public TextField data;
    Frame frame;
    JvnObject sentence;

    /**
     * main method create a JVN object nammed IRC for representing the Chat
     * application
     *
     */
    @Override
    public void run() {
        try {
            // initialize 
            JvnServerImpl js = JvnServerImpl.jvnGetServer();

            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            JvnObject jo = js.jvnLookupObject("IRC");
            //JvnObject jo = null;

            if (jo == null) {
                jo = js.jvnCreateObject((Serializable) new BurstSentence());
                // after creation, I have a write lock on the object
                js.jvnRegisterObject("IRC", jo);
                jo.jvnUnLock();
            }
            // create the graphical part of the Chat application
            //new Irc(jo);
            int i = 0;
            sleep(5000);
            while (i <= 10) {
                // lock the object in read mode
                jo.jvnLockRead();
                // invoke the method
                String s = ((BurstSentence) (jo.jvnGetObjectState())).read();
                System.out.println("READ " + s);
                //unlock the object
                jo.jvnUnLock();

                // get the value to be written from the buffer
                s = "Ecriture" + "_" + i++;
                System.out.println("WRITE " + s);
                // lock the object in write mode
                jo.jvnLockWrite();
                // invoke the method
                ((BurstSentence) (jo.jvnGetObjectState())).write(s);
                jo.jvnUnLock();
                sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("IRC problem : " + e);
        }
    }
}
