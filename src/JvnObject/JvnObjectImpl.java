package JvnObject;

import JvnObject.Interfaces.JvnObject;
import JvnObject.Interfaces.JvnObject.Lock;
import static Server.JvnServerImpl.js;
import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvn.JvnException;

public class JvnObjectImpl implements Serializable, JvnObject {

    Serializable objectRemote;
    int id;
    transient Lock state;

    public JvnObjectImpl() {
    }

    public JvnObjectImpl(Serializable objectRemote, int id) {
        this.objectRemote = objectRemote;
        this.id = id;
        this.state = Lock.WLT;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(JvnObjectImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (state) {
            case NL:
                JvnObjectImpl jo = (JvnObjectImpl) js.jvnLockRead(id);
                objectRemote = jo.getObjectRemote();
                state = Lock.RLT;
                break;
            case RLC:
                state = Lock.RLT;
                break;
            case WLC:
                state = Lock.RLT_WLC;
                break;
            default:
                throw new JvnException("Read lock has a problem => " + state);
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        switch (state) {
            case NL:
            case RLC:
                JvnObjectImpl jo = (JvnObjectImpl) js.jvnLockWrite(id);
                objectRemote = jo.getObjectRemote();
                state = Lock.WLT;
                break;
            case WLC:
                state = Lock.WLT;
                break;
            default:
                throw new JvnException("Write lock has a problem => " + state);
        }
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        if (state == Lock.WLT) {
            state = Lock.WLC;
        } else if (state == Lock.RLT) {
            state = Lock.RLC;
            notifyAll();
        } else if (state == Lock.RLT_WLC) {
            state = Lock.WLC;
            notifyAll();
        }
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetObjectState() throws JvnException {
        return objectRemote;
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
        while (state == Lock.RLT || state == Lock.RLT_WLC) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(JvnObjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        state = Lock.NL;
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        while (state == Lock.WLT) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }

        state = Lock.NL;
        return objectRemote;
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {

        while (state == Lock.WLT) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
        
        state = Lock.RLC;
        return objectRemote;
    }

    public Serializable getObjectRemote() {
        return objectRemote;
    }

    public void setObjectRemote(Serializable objectRemote) {
        this.objectRemote = objectRemote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lock getState() {
        return state;
    }

    public void setState(Lock state) {
        this.state = state;
    }
}
