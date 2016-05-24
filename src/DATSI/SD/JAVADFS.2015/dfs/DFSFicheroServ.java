// Interfaz del API de acceso remoto a un fichero

package dfs;
import java.io.IOException;
import java.rmi.*;

public interface DFSFicheroServ extends Remote  {
    byte[] read(byte[] b) throws RemoteException, IOException;
    void write(byte[] b,Double user) throws RemoteException, IOException;
    void write(byte[] b) throws RemoteException, IOException;
    void seek(long p) throws RemoteException, IOException;
    long close() throws RemoteException, IOException;
    void addUser(Double user, String mode, DFSFicheroCallback callback) throws IOException;
}
