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
 * TODO: Invocación de los métodos correspondientes del objeto RandomAccessFile que almacena internamente.
 */
public class DFSFicheroServImpl extends UnicastRemoteObject implements DFSFicheroServ {
    private static final String DFSDir = "DFSDir/";

    public DFSFicheroServImpl()
      throws RemoteException, FileNotFoundException {
    }
}
