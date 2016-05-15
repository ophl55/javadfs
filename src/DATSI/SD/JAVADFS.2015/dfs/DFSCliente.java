// Clase de cliente que proporciona acceso al servicio DFS

package dfs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * DFSCliente: encapsula acceso a rmiregistry y a variables entorno
 * Host y puerto del rmiregistry en variables de entorno
 *
 * TODO Fase 1:
 * Acceso a las variables de entorno y operación de lookup en el rmiregistry.
 *
 * TODO Fase 2 Etapa 1:
 * mantenga algún tipo de contenedor para almacenar las cachés de todos los cacheFicheros accedidos por la aplicación
 * hasta ese instante. Cada vez que se cree un objeto de la clase DFSFicheroCliente,
 * éste deberá comprobar si ya hay una caché para ese fichero. Si la hay, la utilizará. En caso contrario,
 * la creará y la incluirá en el contenedor para sucesivos accesos.
 *
 */
public class DFSCliente {
    private int tamBloque, tamCache;
    private DFSServicio srv;
    private Map<String, Cache> cacheFicheros;

    public DFSCliente(int tamBloque, int tamCache) {

        this.tamBloque = tamBloque;
        this.tamCache = tamCache;
        this.cacheFicheros = new HashMap<String, Cache>();

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

    public Map<String, Cache> getCacheFicheros() {
        return cacheFicheros;
    }

    public void setCacheFicheros(Map<String, Cache> cacheFicheros) {
        this.cacheFicheros = cacheFicheros;
    }

    public int getTamBloque() {
        return tamBloque;
    }

    public void setTamBloque(int tamBloque) {
        this.tamBloque = tamBloque;
    }

    public int getTamCache() {
        return tamCache;
    }

    public void setTamCache(int tamCache) {
        this.tamCache = tamCache;
    }
}

