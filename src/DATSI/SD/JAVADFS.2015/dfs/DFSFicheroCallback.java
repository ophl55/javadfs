// Interfaz del servicio de callback de DFS

package dfs;
import java.io.IOException;
import java.rmi.*;

public interface DFSFicheroCallback extends Remote  {

    /**
     * uses Cache 
     * @throws RemoteException
     */
    public void useCache() throws RemoteException;

    /**
     * invalid Cache
     * @throws RemoteException
     * @throws IOException
     */
    public void invalidCache() throws RemoteException, IOException;



}
