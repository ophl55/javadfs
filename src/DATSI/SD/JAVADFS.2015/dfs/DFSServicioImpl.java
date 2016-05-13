// Clase de servidor que implementa el servicio DFS

package dfs;

import java.io.FileNotFoundException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DFSServicio: crea/abre fich. y genera ref remota para su acceso
 *
 * TODO: Definición del método para generar referencias remotas DFSFicheroServ.
 */
public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
    private Map<String,DFSFicheroServ> ficheros;

    public DFSServicioImpl() throws RemoteException {
        ficheros = new HashMap<String, DFSFicheroServ>();
    }

    @Override
    public DFSFicheroServ iniciar(String name, String mode) throws RemoteException {
        DFSFicheroServImpl fichero = null;

        if (ficheros.containsKey(name))
            return ficheros.get(name);

        try {
            fichero = new DFSFicheroServImpl(name, mode);
            System.out.println("New file: " + name);
            ficheros.put(name, fichero);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fichero;
    }
}
