// Clase de servidor que implementa el API de acceso remoto a un fichero

package dfs;
import java.rmi.*;
import java.rmi.server.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

/**
 * DFSFicheroServ: read, write, seek, close
 *
 * Servicio con estado: toda la funcionalidad en el servidor
 * DFSFicheroServ Estado: fichero abierto, modo y posición
 *      1 por cada open aunque sea del mismo fichero
 *
 * cada objeto DFSFicheroServ almacenaría internamente un objeto RandomAccessFile.
 *
 * Invocación de los métodos correspondientes del objeto RandomAccessFile que almacena internamente.
 */
public class DFSFicheroServImpl extends UnicastRemoteObject implements DFSFicheroServ {
    private static final long serialVersionUID = 1L;
    private static final String DFSDir = "DFSDir/";
    private RandomAccessFile fichero;
    private String name, mode;
    private DFSServicioImpl servicio;

    List<Double> readUser = new ArrayList<Double>();
    List<Double> writeUser = new ArrayList<Double>();
    List<DFSFicheroCallback> usingCache = new ArrayList<DFSFicheroCallback>();



    public DFSFicheroServImpl(String name, String mode, DFSServicioImpl servicio)
      throws RemoteException, FileNotFoundException,IOException {
        File file;
        if(!mode.contains("w")){
            file = new File(DFSDir + name);
            if(!file.exists())
                throw new IOException();
        }

        this.name = name;
        this.mode = mode;
        this.servicio = servicio;
        fichero = new RandomAccessFile(DFSDir + name, "rw");
        System.out.println("New file created");
    }
    public void addUser(Double user, String mode, DFSFicheroCallback callback) throws IOException{
        if(!mode.contains("w")) {
            if (writeUser.isEmpty()) {
                callback.useCache();
                addclient(callback);
            } else {
                for (DFSFicheroCallback c : usingCache) {
                    c.invalidCache();
                }
                usingCache.clear();
                callback.invalidCache();
            }
            readUser.add(user);
        } else{
            if(writeUser.isEmpty() && readUser.isEmpty()){
                callback.useCache();
                addclient(callback);
            }
            else{
                for(DFSFicheroCallback c : usingCache){
                    c.invalidCache();
                }
                usingCache.clear();
                callback.invalidCache();
            }
            writeUser.add(user);
        }
    }
    public void addclient(DFSFicheroCallback c) throws RemoteException{
        this.usingCache.add(c);
    }
    /**
    @Override
    public synchronized byte[] read(byte[] b) throws RemoteException, IOException {
        if (fichero.read(b) < 0) {
            System.out.println("Error in read");
            return null;
        }

        System.out.println(b.length + " bytes read");
        return b;
    }**/
    @Override
    public synchronized byte[] read(byte[] b , Double user) throws RemoteException, IOException {
        if (fichero.read(b) < 0) {
            System.out.println("Error in read");
            return null;
        }
        System.out.println(b.length + " bytes read");
        return b;
    }
    /**
    public synchronized void write(byte[] b) throws RemoteException, IOException {
        fichero.write(b);
        System.out.println(b.length + " bytes written");
    }**/
    @Override
    public synchronized void write(byte[] b, Double user) throws RemoteException, IOException {
        if(writeUser.contains(user)) {
            fichero.write(b);
            System.out.println(b.length + " bytes written");
        }
        else
            throw new IOException();
    }

    @Override
    public synchronized void seek(long p) throws RemoteException, IOException {
        fichero.seek(p);
        System.out.println("Pointer on " + String.valueOf(p));
    }
/*
    @Override
    public synchronized long close() throws RemoteException, IOException {
        servicio.removeFile(name);
        fichero.close();
        System.out.println("File closed");
        return getLastModified();
    }*/
    public synchronized long close(Double user) throws RemoteException, IOException {
        deleteUser(user);
        if(!existUser()) {
            servicio.removeFile(name);
            fichero.close();
        }
        System.out.println("File closed");
        return getLastModified();
    }
    public synchronized void deleteUser(Double user) throws RemoteException{
        if(writeUser.contains(user))
            writeUser.remove(user);
        else
            readUser.remove(user);
    }
    public synchronized boolean existUser() throws RemoteException{
        return !readUser.isEmpty() || !writeUser.isEmpty();
    }
    /**
     * Helper function to get the date of the last modification of a file.
     *
     * @return Date
     * @throws FileNotFoundException
     */
    public synchronized long getLastModified() throws FileNotFoundException {
        File file = new File(DFSDir + name);
        long lastModified = file.lastModified();
        System.out.println("last modified: " + String.valueOf(lastModified));
        return lastModified;
    }
}
