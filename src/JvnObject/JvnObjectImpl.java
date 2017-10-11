package JvnObject;

import JvnObject.Interfaces.JvnObject;
import JvnObject.Interfaces.JvnObject.Lock;
import static Server.JvnServerImpl.js;
import Spec.BurstSentence;
import java.io.Serializable;
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
        switch (state) {
            case NL:
                System.out.println("NL");
                JvnObjectImpl jo = (JvnObjectImpl) js.jvnLockRead(id);
                System.out.println("READ : " + Thread.currentThread().getName() + "_" + state);
                objectRemote = jo.getObjectRemote();
                System.out.println("Get object");
                System.out.println(objectRemote);

                state = Lock.RLT;
                break;
            case RLC:
                System.out.println("RLC");
                state = Lock.RLT;
                break;
            case WLC:
                System.out.println("WLC");
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
                System.out.println("RLC ou NL");
                JvnObjectImpl jo = (JvnObjectImpl) js.jvnLockWrite(id);
                System.out.println("WRITE : " + Thread.currentThread().getName() + "_" + state);
                objectRemote = jo.getObjectRemote();
                state = Lock.WLT;
                break;
            case WLC:
                System.out.println("WLC");
                state = Lock.WLT;
                break;
            default:
                throw new JvnException("Write lock has a problem => " + state);
        }
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        System.out.println("UNLOCK : " + Thread.currentThread().getName() + "_" + state);
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
                System.err.println(ex);
            }
        }
        System.out.println("INVALIDATE READER");

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
        System.out.println("INVALIDATE WRITER");

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
        System.out.println("INVALIDATE WRITER FOR READER");

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
