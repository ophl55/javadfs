// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.*;
import java.net.Authenticator;
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
    private DFSCliente dfs;
    private DFSFicheroServ ficheroServ;
    private Cache cache;
    private long pointer;

    public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
      throws RemoteException, IOException, FileNotFoundException {
        this.dfs = dfs;
        this.ficheroServ = dfs.getSrv().iniciar(nom, modo);
        this.pointer = 0;

        if (dfs.getCacheFicheros().containsKey(nom)){
            cache = dfs.getCacheFicheros().get(nom);
        } else {
            cache = new Cache(dfs.getTamCache()/dfs.getTamBloque());
            dfs.getCacheFicheros().put(nom, cache);
        }
    }

    /**
     * reads b.length bytes from remote file.
     *
     * TODO Fase 2 Etapa 1:
     * En cuanto a la operación de lectura, debe descomponer el acceso en bloques y por cada bloque comprobar si está
     * en la caché. En caso negativo, lo solicita al servidor y, una vez obtenido, lo incluye en la caché
     * (nótese que no hace falta comprobar si esa inclusión expulsa algún bloque puesto que asumimos en esta etapa que
     * la caché no se llena). Una vez el bloque en la caché, ya sea porque estaba inicialmente o porque se ha traído
     * del servidor, se copia al buffer especificado por la aplicación.
     *
     * @param b Buffer to be filled by this function
     * @return number of bytes read
     * @throws RemoteException
     * @throws IOException
     */
    public int read(byte[] b) throws RemoteException, IOException {

        for (int i = 0; i < b.length/dfs.getTamBloque(); i++){
            if(cache.getBloque(pointer) != null){
                // Block found in cache. Now copy it to buffer.
                System.out.println("Found block in cache");
                System.arraycopy(cache.getBloque(pointer).obtenerContenido(), 0, b, i*dfs.getTamBloque(), dfs.getTamBloque());
            }
            else {
                // Block not found in cache. Look it up in the remote file.
                System.out.println("Request block from server");
                byte [] readBlock = new byte[dfs.getTamBloque()];
                ficheroServ.seek(pointer);  // Remote pointer has to be adjusted.
                readBlock = ficheroServ.read(readBlock);
                if (readBlock == null)
                    return -1;

                // Store looked up block in the cache.
                cache.putBloque(new Bloque(pointer, readBlock));

                // Copy block to the buffer.
                System.arraycopy(readBlock, 0, b, i*dfs.getTamBloque(), dfs.getTamBloque());
            }
            pointer += dfs.getTamBloque();
        }

        //res = ficheroServ.read(b);
        //System.arraycopy(res, 0, b, 0, b.length);
        return b.length;
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
        //ficheroServ.seek(p);
        pointer = p;
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
