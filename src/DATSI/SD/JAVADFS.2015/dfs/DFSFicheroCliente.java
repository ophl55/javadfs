// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.*;
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
    private FicheroInfo ficheroInfo;
    private DFSCliente dfs;
    private DFSFicheroServ ficheroServ;
    private Cache cache;
    private long pointer;
    private String modo;

    private boolean opened;//ndicate that file is open and used at the moment
    private String name;
    private Double user;
    private DFSFicheroCallback usingCache;

    public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
      throws RemoteException, IOException, FileNotFoundException {
        this.dfs = dfs;
        this.ficheroInfo = dfs.getSrv().iniciar(nom, modo);
        this.ficheroServ = ficheroInfo.getFicheroServ();
        this.pointer = 0;
        this.modo = modo;
        this.opened = true;
        this.name = nom;
        this.user = Math.random();
        this.usingCache = new DFSFicheroCallbackImpl(this);
        ficheroServ.addUser(user,modo,usingCache);
        System.out.println("ENTRANCE IN DFSFICHEROCLIENTE");

        // Check if file consists in cache
        if (dfs.getCacheFicheros().containsKey(nom)){
            System.out.println("Cache found");
            cache = dfs.getCacheFicheros().get(nom);
        } else {
            System.out.println("Cache created");
            cache = new Cache(dfs.getTamCache());
            dfs.getCacheFicheros().put(nom, cache);
        }

        // If the remote file is newer than the cache -> clear the cache
        if (cache.obtenerFecha() < ficheroInfo.getDate()){
            cache.vaciar();
            System.out.println("Cache cleaned");
        }


    }

    /**
     *
     * @throws RemoteException
     */
    public synchronized void useCache() throws RemoteException{
        this.cache = this.dfs.getCachebyFilename(name);
        if(cache.obtenerFecha() < ficheroInfo.getDate())
            cache.vaciar();


    }

    /**
     *
     * @throws RemoteException
     * @throws IOException
     */
    public synchronized void invalidCache() throws RemoteException, IOException{
        if(cache != null){
            if (modo.contains("w"))
                overthrowCache();


        }

    }

    /**
     *
     * @throws RemoteException
     * @throws IOException
     */
    private void overthrowCache() throws RemoteException, IOException{
        cache.vaciar();
        for(Bloque blo : cache.listaMod()){
            //overwrite(blo.obtenerContenido(), blo.obtenerId() * blocksize);
            cache.desactivarMod(blo);
        }

    }

    /*
    private void overwrite(byte[] info, long startp) throws IOException{
        ficheroServ.seek(startp);
        ficheroServ.write(info,user);
    }*/



    /**
     * reads b.length bytes from remote file.
     *
     * Fase 2 Etapa 1:
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
        Bloque expulsadoBlock;

        if(!isUsed())
            throw new IOException();

        for (int i = 0; i < b.length/dfs.getTamBloque(); i++){
           if(cache.getBloque(pointer/dfs.getTamBloque()) != null){
                // Block found in cache. Now copy it to buffer.
                System.out.println("Found block in cache");
                System.arraycopy(cache.getBloque(pointer/dfs.getTamBloque()).obtenerContenido(), 0, b, i*dfs.getTamBloque(), dfs.getTamBloque());
            }
            else {
                // Block not found in cache. Look it up in the remote file.
                System.out.println("Request block from server");
                byte [] readBlock = new byte[dfs.getTamBloque()];
                ficheroServ.seek(pointer);  // Remote pointer has to be adjusted.
                readBlock = ficheroServ.read(readBlock,user);
                if (readBlock == null)
                    return i * dfs.getTamBloque();

                // Store looked up block in the cache.
                expulsadoBlock = cache.putBloque(new Bloque(pointer/dfs.getTamBloque(), readBlock));
                if (expulsadoBlock != null)
                    writeBlock(expulsadoBlock);

                // Copy block to the buffer.
                System.arraycopy(readBlock, 0, b, i*dfs.getTamBloque(), dfs.getTamBloque());
            }
            pointer += dfs.getTamBloque();
        }

        return b.length;
    }

    /**
     * Fase 2 Etapa 2
     * En cuanto a la propia operación de escritura, deberá descomponer el acceso en bloques e incorporar cada bloque
     * a la caché (nótese que la restricción de usar sólo operaciones con bloques completos elimina la necesidad de
     * consultar la caché para saber si el bloque estaba previamente en la misma), marcándolo como modificado.
     * Cada vez que se incorpora un bloque a la caché, hay que comprobar si otro bloque ha sido expulsado y,
     * en caso afirmativo, si dicho bloque estaba modificado, puesto que en ese caso hay que enviarlo al servidor y
     * ponerlo como no modificado. Nótese que habría que hacer lo mismo al incorporar bloques en la caché como parte
     * de la operación de lectura, descrita en la etapa previa.
     *
     * Fase 2 Etapa 2
     * Obsérvese que con este esquema de escritura diferida, si una aplicación abre un fichero sólo para leer y
     * escribe en el mismo, el error sólo se detectará cuando se vuelque un bloque al servidor, ya sea por expulsión o
     * en el cierre del fichero. Para conseguir un comportamiento tal que el error se detecte en la primera operación
     * de escritura, que es el que va a requerir este proyecto práctico, la clase DFSFicheroCliente debe encargarse
     * de detectar este error y generar una excepción (IOException).
     *
     * Fase 2 Etapa 2
     * Como se comentó en la etapa previa, es válida una solución que envíe al servidor los bloques a escribir de
     * forma individual, aunque sería más eficiente una alternativa que permitiera escribir múltiples bloques con una
     * única operación.
     *
     * @param b
     * @throws RemoteException
     * @throws IOException
     */
    public void write(byte[] b) throws RemoteException, IOException {
        if(!isUsed() || !canWrite())
            throw new IOException();

        Bloque newBlock, expulsadoBlock;
        if (modo.equals("r"))
            throw new IOException();

        for (int i = 0; i < b.length/dfs.getTamBloque(); i++) {
            byte [] content = new byte[dfs.getTamBloque()];
            System.arraycopy(b, i*dfs.getTamBloque(), content, 0, dfs.getTamBloque());
            newBlock = new Bloque(pointer/dfs.getTamBloque(), content);
            expulsadoBlock = cache.putBloque(newBlock);
            cache.activarMod(newBlock);

            // treat the block that was replaced from cache
            if (expulsadoBlock != null)
                writeBlock(expulsadoBlock);

            pointer += dfs.getTamBloque();
        }
     }

    /**
     * Helper function to write a block in the remote file to the right position.
     * Only writes the block, if the block is marked as modified in the cache.
     *
     * @param b Block to write
     * @throws RemoteException
     * @throws IOException
     */
    private void writeBlock(Bloque b) throws RemoteException, IOException {
        /*if (modo.equals("r"))
            return;
          */  //throw new IOException();

        if(cache.preguntarYDesactivarMod(b)){
            ficheroServ.seek(b.obtenerId() * dfs.getTamBloque());
            ficheroServ.write(b.obtenerContenido(),user);
        }
    }

    /**
     *
     * @param p
     * @throws RemoteException
     * @throws IOException
     */
    public void seek(long p) throws RemoteException, IOException {
        //ficheroServ.seek(p);
        if(!isUsed())
            throw new IOException();

        pointer = p;
    }

    /**
     * Fase 2 Etapa 2
     * deberá enviar todos los bloques modificados al servidor para que los escriba en el fichero, quedando todos
     * ellos como no modificados en la caché.
     *
     * @throws RemoteException
     * @throws IOException
     */
    public void close() throws RemoteException, IOException {
        if(!isUsed())
            throw new IOException();
        // write all modified marked blocks to remote file
        for (Bloque b : cache.listaMod()){
            writeBlock(b);
        }
        cache.vaciarListaMod(); // clears the list that holds modified blocks
        cache.fijarFecha(ficheroServ.close(user)); // store the lastModified date of the remote file for coherence issues
        this.opened = false;
    }

    /**
     *
     * @return true if file is used, false otherwise
     */
    private boolean isUsed(){
        return this.opened;
    }

    /**
     *
     * @return true if client is allowed to write, false otherwise
     */
    private boolean canWrite(){
        return modo.contains("w");
    }
}
