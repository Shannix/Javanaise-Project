/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Coordinator;

import JvnObject.JvnObjectImpl;
import Server.Interfaces.JvnRemoteServer;
import java.util.ArrayList;

/**
 *
 * @author scra
 */
public class ObjectManager {

    JvnObjectImpl jvnObjectImpl;
    ArrayList<JvnRemoteServer> readerServers;
    JvnRemoteServer writerServer;

    public ObjectManager(JvnObjectImpl jvnObjectImpl, JvnRemoteServer writerServer) {
        this.jvnObjectImpl = jvnObjectImpl;
        this.readerServers = new ArrayList<JvnRemoteServer>();
        this.writerServer = writerServer;
    }

    public JvnObjectImpl getJvnObjectImpl() {
        return jvnObjectImpl;
    }

    public void setJvnObjectImpl(JvnObjectImpl jvnObjectImpl) {
        this.jvnObjectImpl = jvnObjectImpl;
    }

    public ArrayList<JvnRemoteServer> getReaderServers() {
        return readerServers;
    }

    public void setReaderServers(ArrayList<JvnRemoteServer> readerServers) {
        this.readerServers = readerServers;
    }

    public JvnRemoteServer getWriterServer() {
        return writerServer;
    }

    public void setWriterServer(JvnRemoteServer writerServer) {
        this.writerServer = writerServer;
    }

    public void removeReaderServers() {
        readerServers.clear();
    }

    public void removeWriterServer() {
        writerServer = null;
    }
}
