// Esta clase representa información de un fichero.
// El enunciado explica más en detalle el posible uso de esta clase.
// Al ser serializable, puede usarse en las transferencias entre cliente
// y servidor.

package dfs;
import java.io.*;

/**
 * TODO: Fase 2 Etapa 2
 * esquema para asegurar la coherencia entre sesiones:
 *      Al cerrar el fichero, la clase DFSFicheroCliente obtiene la fecha de última modificación del mismo y fija ese
 *      valor como fecha asociada a la caché.
 *      Al abrir un fichero, se obtiene la fecha de modificación actual del fichero y si es posterior a la almacenada
 *      en la caché, se borra el contenido de la misma.
 *
 * Dado que en la apertura la clase DFSFicheroCliente debe obtener la fecha de última modificación además de la
 * referencia remota a un objeto DFSFicheroServ, se puede modificar el método de fabricación de DFSServicio para
 * que devuelva un objeto de la clase FicheroInfo que contenga ambos valores.
 */
public class FicheroInfo implements Serializable {
    private DFSFicheroServ ficheroServ;
    private long date;

    public FicheroInfo(DFSFicheroServ ficheroServ, long date){
        this.ficheroServ = ficheroServ;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public DFSFicheroServ getFicheroServ() {
        return ficheroServ;
    }

    public void setFicheroServ(DFSFicheroServ ficheroServ) {
        this.ficheroServ = ficheroServ;
    }
}
