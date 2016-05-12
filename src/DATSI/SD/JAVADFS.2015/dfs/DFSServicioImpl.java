// Clase de servidor que implementa el servicio DFS

package dfs;

import java.rmi.*;
import java.rmi.server.*;

/**
 * DFSServicio: crea/abre fich. y genera ref remota para su acceso
 *
 * TODO: Definición del método para generar referencias remotas DFSFicheroServ.
 */
public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
    public DFSServicioImpl() throws RemoteException {
    }
}
