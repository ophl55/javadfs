// Interfaz del servicio DFS

package dfs;
import java.rmi.*;

public interface DFSServicio extends Remote {
    DFSFicheroServ iniciar(String name, String mode) throws RemoteException;
}       
