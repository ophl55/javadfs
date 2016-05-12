// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.*;
import java.rmi.*;

/**
 * DFSFicheroCliente: sus ops sólo llaman servicio remoto asociado
 *  Cuidado con read
 *
 * TODO:
 * Solicitud a DFSServicio de una referencia remota de tipo DFSFicheroServ en su constructor e
 * invocación de las llamadas correspondientes usando dicha referencia remota.
 * En esta clase debe incorporarse el código necesario para solventar el problema específico de la operación read
 * explicado previamente.
 */
public class DFSFicheroCliente  {
    public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
      throws RemoteException, IOException, FileNotFoundException {
    }
    public int read(byte[] b) throws RemoteException, IOException {
 	 return 0;
    }
    public void write(byte[] b) throws RemoteException, IOException {
    }
    public void seek(long p) throws RemoteException, IOException {
    }
    public void close() throws RemoteException, IOException {
    }
}
