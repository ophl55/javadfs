// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.*;
import java.nio.ByteOrder;
import java.rmi.*;

/**
 * DFSFicheroCliente: sus ops sólo llaman servicio remoto asociado
 *  Cuidado con read
 *
 * Solicitud a DFSServicio de una referencia remota de tipo DFSFicheroServ en su constructor e
 * invocación de las llamadas correspondientes usando dicha referencia remota.
 * En esta clase debe incorporarse el código necesario para solventar el problema específico de la operación read
 * explicado en el enunciado de la practica.
 */
public class DFSFicheroCliente  {
    DFSFicheroServ ficheroServ;

    public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
      throws RemoteException, IOException, FileNotFoundException {
        System.out.println("Create new file " + nom);
        ficheroServ = dfs.getSrv().iniciar(nom, modo);
    }

    /**
     * reads b.length bytes from remote file.
     *
     * @param b Buffer to be filled by this function
     * @return number of bytes read
     * @throws RemoteException
     * @throws IOException
     */
    public int read(byte[] b) throws RemoteException, IOException {
        byte [] res = new byte[b.length];
        res = ficheroServ.read(b);
        System.arraycopy(res, 0, b, 0, b.length);
        return res.length;
    }

    /**
     *
     * @param b
     * @throws RemoteException
     * @throws IOException
     */
    public void write(byte[] b) throws RemoteException, IOException {
        ficheroServ.write(b);
    }

    /**
     *
     * @param p
     * @throws RemoteException
     * @throws IOException
     */
    public void seek(long p) throws RemoteException, IOException {
        ficheroServ.seek(p);
    }

    /**
     *
     * @throws RemoteException
     * @throws IOException
     */
    public void close() throws RemoteException, IOException {
        ficheroServ.close();
    }
}
