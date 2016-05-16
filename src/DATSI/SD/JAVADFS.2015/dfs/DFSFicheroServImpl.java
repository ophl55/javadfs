// Clase de servidor que implementa el API de acceso remoto a un fichero

package dfs;
import java.rmi.*;
import java.rmi.server.*;

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
    private static final String DFSDir = "DFSDir/";
    private RandomAccessFile fichero;
    private String name, mode;
    private DFSServicioImpl servicio;

    public DFSFicheroServImpl(String name, String mode, DFSServicioImpl servicio)
      throws RemoteException, FileNotFoundException {
        this.name = name;
        this.mode = mode;
        this.servicio = servicio;
        fichero = new RandomAccessFile(DFSDir + name, mode);
        System.out.println("New file created");
    }

    @Override
    public byte[] read(byte[] b) throws RemoteException, IOException {
        if (fichero.read(b) < 0) {
            System.out.println("Error in read");
            return null;
        }

        System.out.println(b.length + " bytes read");
        return b;
    }

    @Override
    public void write(byte[] b) throws RemoteException, IOException {
        fichero.write(b);
        System.out.println(b.length + " bytes written");
    }

    @Override
    public void seek(long p) throws RemoteException, IOException {
        fichero.seek(p);
        System.out.println("Pointer on " + String.valueOf(p));
    }

    @Override
    public long close() throws RemoteException, IOException {
        servicio.removeFile(name);
        fichero.close();
        System.out.println("File closed");
        return getLastModified();
    }

    /**
     * Helper function to get the date of the last modification of a file.
     *
     * @return Date
     * @throws FileNotFoundException
     */
    public long getLastModified() throws FileNotFoundException {
        File file = new File(DFSDir + name);
        long lastModified = file.lastModified();
        System.out.println("last modified: " + String.valueOf(lastModified));
        return lastModified;
    }
}
