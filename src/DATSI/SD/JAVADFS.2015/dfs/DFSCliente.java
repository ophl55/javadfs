// Clase de cliente que proporciona acceso al servicio DFS

package dfs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * DFSCliente: encapsula acceso a rmiregistry y a variables entorno
 * Host y puerto del rmiregistry en variables de entorno
 *
 * TODO: Acceso a las variables de entorno y operación de lookup en el rmiregistry.
 */
public class DFSCliente {
    private int tamBloque, tamCache;
    private DFSServicio srv;

    public DFSCliente(int tamBloque, int tamCache) {

        this.tamBloque = tamBloque;
        this.tamCache = tamCache;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            srv = (DFSServicio) Naming.lookup("//" + System.getenv("SERVIDOR") + ":"
                    + System.getenv("PUERTO") + "/DFS");
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public DFSServicio getSrv() {
        return srv;
    }

    public void setSrv(DFSServicio srv) {
        this.srv = srv;
    }
}

