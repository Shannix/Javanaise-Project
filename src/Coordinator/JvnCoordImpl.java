/**
 * *
 * JAVANAISE Implementation JvnServerImpl class Contact:
 *
 * Authors:
 */
package Coordinator;

import Coordinator.Interfaces.JvnRemoteCoord;
import JvnObject.Interfaces.JvnObject;
import JvnObject.Interfaces.JvnObject.Lock;
import JvnObject.JvnObjectImpl;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import jvn.JvnException;
import Server.Interfaces.JvnRemoteServer;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

    public JvnRemoteServer look_up;
    public Map<String, ObjectManager> store;
    public AtomicInteger counter_object;

    /**
     * Default constructor
     *
     * @throws JvnException
     *
     */
    private JvnCoordImpl() throws Exception {
        store = new HashMap();
        counter_object = new AtomicInteger(0);
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a newly created JVN
     * object)
     *
     * @return
     * @throws java.rmi.RemoteException,JvnException
     * @throws jvn.JvnException
     *
     */
    @Override
    public int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {
        return counter_object.getAndIncrement();
    }

    /**
     * Associate a symbolic name with a JVN object counter_object
     *
     * @param jon : the JVN object name
     * @param jo : the JVN object
     * @param joi : the JVN object identification
     * @param js : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     *
     */
    @Override
    public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {
        JvnObjectImpl joImpl = (JvnObjectImpl) jo;
        joImpl.setState(Lock.WLT);

        ObjectManager objM = new ObjectManager(joImpl, js);
        objM.getReaderServers().add(js);
        store.put(jon, objM);
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js : the remote reference of the JVNServer
     * @return
     * @throws java.rmi.RemoteException,JvnException
     *
     */
    @Override
    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {
        JvnObjectImpl jvnObjImpl = null;

        if (store.containsKey(jon)) {
            jvnObjImpl = store.get(jon).getJvnObjectImpl();
            ObjectManager joM = store.get(jon);
            joM.getReaderServers().add(js);
            store.put(jon, joM);
        }

        return jvnObjImpl;
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     * @throws jvn.JvnException
     *
     */
    @Override
    public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        ObjectManager joM = getObjectManagerById(joi);
        JvnObjectImpl jo = joM.getJvnObjectImpl();
        joM.getJvnObjectImpl().setState(Lock.NL);
        
        Serializable joSer = joM.getWriterServer().jvnInvalidateWriter(joi);
        jvnUnlock(joi, joSer);

        joM.getWriterServer().jvnInvalidateWriterForReader(joi);
        joM.getReaderServers().add(js);
        store.put(getSymbolById(joi), joM);

        return jo.getObjectRemote();
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     *
     */
    @Override
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        ObjectManager joM = getObjectManagerById(joi);
        JvnObjectImpl jo = joM.getJvnObjectImpl();

        Serializable joSer = joM.getWriterServer().jvnInvalidateWriter(joi);
        jvnUnlock(joi, joSer);
        joM.setWriterServer(js);

        joM.getReaderServers().stream().forEach(server -> invalidateReader(server, joi));
        joM.removeReaderServers();
        joM.getReaderServers().add(js);

        joM.getJvnObjectImpl().setState(Lock.WLT);
        return jo.getObjectRemote();
    }

    private synchronized void jvnUnlock(int joi, Serializable objectRemote) throws RemoteException, JvnException {
        ObjectManager joM = getObjectManagerById(joi);
        JvnObjectImpl jo = joM.getJvnObjectImpl();
        jo.setState(Lock.NL);
        jo.setObjectRemote(objectRemote);
        store.put(getSymbolById(joi), joM);
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     * @throws jvn.JvnException
     *
     */
    @Override
    public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        store.entrySet().stream()
                .map(keyValue -> keyValue.getValue()
                        .getReaderServers().remove(js));

        store.entrySet().stream()
                .filter(keyValue -> keyValue.getValue().getWriterServer() == js)
                .map(server -> server.setValue(null));
    }

    /**
     * ****************************************************************************************
     */
    private void invalidateReader(JvnRemoteServer js, int joi) {
        try {
            js.jvnInvalidateReader(joi);
        } catch (RemoteException ex) {
            Logger.getLogger(JvnCoordImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JvnException ex) {
            Logger.getLogger(JvnCoordImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ObjectManager getObjectManagerById(int objId) {
        return store.entrySet().stream()
                .filter(keyValue -> keyValue.getValue().getJvnObjectImpl().getId() == objId)
                .findFirst().get().getValue();
    }

    private String getSymbolById(int id) {
        return store.entrySet().stream()
                .filter(keyValue -> keyValue.getValue().getJvnObjectImpl().getId() == id)
                .findFirst().get().getKey();
    }

    public static void main(String[] args) {
        try {
            JvnCoordImpl coord = new JvnCoordImpl();
            Registry registry = LocateRegistry.createRegistry(2020);
            registry.bind("Coordinator", coord);
            System.err.println("Coordinator ready on " + registry);
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
        } catch (AlreadyBoundException ex) {
            Logger.getLogger(JvnCoordImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JvnCoordImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
