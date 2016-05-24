// Clase de servidor que implementa el servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DFSServicio: crea/abre fich. y genera ref remota para su acceso
 *
 * Definición del método para generar referencias remotas DFSFicheroServ.
 */
public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
    private Map<String,DFSFicheroServImpl> ficheros;
    private static final long serialVersionUID = 1L;

    public DFSServicioImpl() throws RemoteException {
        ficheros = new HashMap<String, DFSFicheroServImpl>();
    }

    @Override
    public synchronized FicheroInfo iniciar(String name, String mode) throws IOException, FileNotFoundException{
        DFSFicheroServImpl fichero = null;
        long date = -1;

        if (ficheros.containsKey(name)) {
            // look up in map
            fichero = ficheros.get(name);
        }
        else {
            // create new file
            fichero = new DFSFicheroServImpl(name, mode, this);
            ficheros.put(name, fichero);
            System.out.println("New file: " + name);
        }

        date = fichero.getLastModified();

        return new FicheroInfo(fichero, date);
    }

    /**
     * Removes the file from the internal storage.
     *
     * @param name Filename
     */
    public synchronized void removeFile(String name){
        ficheros.remove(name);
    }
}
