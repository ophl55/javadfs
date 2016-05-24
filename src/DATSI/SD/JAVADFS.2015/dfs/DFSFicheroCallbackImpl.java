// Clase de cliente que implementa el servicio de callback de DFS

package dfs;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;

public class DFSFicheroCallbackImpl extends UnicastRemoteObject implements DFSFicheroCallback {

    private static final long serialVersionUID = 1L;
    DFSFicheroCliente client;

    public DFSFicheroCallbackImpl(DFSFicheroCliente cl)
      throws RemoteException {
        this.client = cl;
    }

    public void useCache() throws RemoteException {
        client.useCache();
    }

    public void invalidCache() throws RemoteException, IOException {
        client.invalidCache();
    }
}
