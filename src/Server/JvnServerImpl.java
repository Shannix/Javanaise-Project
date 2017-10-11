package Server;

/**
 * *
 * JAVANAISE Implementation JvnServerImpl class Contact:
 *
 * Authors:
 */
import Coordinator.Interfaces.JvnRemoteCoord;
import JvnObject.Interfaces.JvnObject;
import JvnObject.Interfaces.JvnObject.Lock;
import JvnObject.JvnObjectImpl;
import Server.Interfaces.JvnLocalServer;
import Server.Interfaces.JvnRemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvn.JvnException;

public class JvnServerImpl extends UnicastRemoteObject
        implements JvnLocalServer, JvnRemoteServer {

    // A JVN server is managed as a singleton 
    public static JvnServerImpl js = null;
    public static Registry reg;
    public JvnRemoteCoord look_up;
    static JvnObjectImpl cache;

    /**
     * Default constructor
     *
     * @throws JvnException
     *
     */
    private JvnServerImpl() throws Exception {
        super();
        reg = LocateRegistry.getRegistry("localhost", 1099);
        look_up = (JvnRemoteCoord) reg.lookup("Coordinator");
        cache = null;
    }

    /**
     * Static method allowing an application to get a reference to a JVN server
     * instance
     *
     * @return
     *
     */
    public static JvnServerImpl jvnGetServer() {
        if (js == null) {
            try {
                js = new JvnServerImpl();
            } catch (Exception e) {
                System.err.println(e);
                return null;
            }
        }
        return js;
    }

    /**
     * The JVN service is not used anymore
     *
     * @throws JvnException
     *
     */
    @Override
    public void jvnTerminate() throws jvn.JvnException {
        try {
            look_up.jvnTerminate(js);
        } catch (RemoteException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * creation of a JVN object
     *
     * @param o : the JVN object state
     * @throws JvnException
     *
     */
    @Override
    public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException {
        try {
            int id = look_up.jvnGetObjectId();
            cache = new JvnObjectImpl(o, id);
        } catch (RemoteException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cache;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo : the JVN object
     * @throws JvnException
     *
     */
    @Override
    public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
        try {
            look_up.jvnRegisterObject(jon, jo, js);
        } catch (RemoteException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Provide the reference of a JVN object beeing given its symbolic name
     *
     * @param jon : the JVN object name
     * @return the JVN object
     * @throws JvnException
     *
     */
    @Override
    public JvnObject jvnLookupObject(String jon) {
        try {
            cache = (JvnObjectImpl) look_up.jvnLookupObject(jon, js);
            if (cache != null) {
                cache.setState(Lock.NL);
            }
        } catch (RemoteException | JvnException ex) {
            System.err.println(ex);
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cache;
    }

    /**
     * Get a Read lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     *
     */
    @Override
    public Serializable jvnLockRead(int joi) throws JvnException {
        Serializable jvnObject = null;
        try {
            jvnObject = look_up.jvnLockRead(joi, js);
        } catch (RemoteException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jvnObject;
    }

    /**
     * Get a Write lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     *
     */
    @Override
    public Serializable jvnLockWrite(int joi) throws JvnException {
        Serializable jvnObject = null;
        try {
            jvnObject = look_up.jvnLockWrite(joi, js);
        } catch (RemoteException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jvnObject;
    }

    /**
     * ******************************* CALL BY COORD
     * *************************************
     */
    /**
     * Invalidate the Read lock of the JVN object identified by id called by the
     * JvnCoord
     *
     * @param joi : the JVN object id
     * @return void
     * @throws java.rmi.RemoteException,JvnException
     * @throws jvn.JvnException
     *
     */
    @Override
    public void jvnInvalidateReader(int joi) {
        try {
            cache.jvnInvalidateReader();
        } catch (JvnException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Invalidate the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     * @throws jvn.JvnException
     *
     */
    @Override
    public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        try {
            cache.jvnInvalidateWriter();
        } catch (JvnException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cache;
    }

    /**
     * Reduce the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     * @throws jvn.JvnException
     *
     */
    @Override
    public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
        try {
            cache.jvnInvalidateWriterForReader();
        } catch (JvnException ex) {
            Logger.getLogger(JvnServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cache;
    }
}
