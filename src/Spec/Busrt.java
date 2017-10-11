/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Spec;

import JvnObject.Interfaces.JvnObject;
import Server.JvnServerImpl;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvn.JvnException;

/**
 *
 * @author scra
 */
public class Busrt implements Runnable {

    JvnObject jo;
    static int iter = 1;

    public Busrt() {
        try {
            // initialize 
            JvnServerImpl js = JvnServerImpl.jvnGetServer();

            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            this.jo = js.jvnLookupObject("IRC");
            //JvnObject jo = null;
            if (this.jo == null) {
                this.jo = js.jvnCreateObject((Serializable) new BurstSentence());
                // after creation, I have a write lock on the object
                js.jvnRegisterObject("IRC", jo);
                this.jo.jvnUnLock();
            }
        } catch (Exception e) {
            System.out.println("IRC problem : " + e);
        }
    }

    @Override
    public void run() {
        int i = new Random().nextInt(10);
        if (i % 2 == 0) {
            write();
        } else {
            read();
        }
    }

    void read() {
        for (int i = 0; i < iter; i++) {
            try {
                // lock the object in read mode
                this.jo.jvnLockRead();
                System.out.println("Read : " + Thread.currentThread().getName());

                // invoke the method
                String s = ((BurstSentence) (this.jo.jvnGetObjectState())).read();
                System.out.println(s);
                // unlock the object
                this.jo.jvnUnLock();
                System.out.println(s);
            } catch (JvnException ex) {
                Logger.getLogger(Busrt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void write() {
        for (int i = 0; i < iter; i++) {
            // get the value to be written from the buffer
            try {
                // lock the object in write mode
                this.jo.jvnLockWrite();
                System.out.println("Write : " + Thread.currentThread().getName());

                String s = i + '_' + Thread.currentThread().getName();
                // invoke the method
                ((BurstSentence) (this.jo.jvnGetObjectState())).write(s);
                System.out.println(((BurstSentence) jo.jvnGetObjectState()).read());
                // unlock the object
                this.jo.jvnUnLock();
            } catch (JvnException ex) {
                Logger.getLogger(Busrt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
