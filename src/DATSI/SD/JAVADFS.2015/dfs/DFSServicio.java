// Interfaz del servicio DFS

package dfs;
import java.io.IOException;
import java.rmi.*;

public interface DFSServicio extends Remote {
    FicheroInfo iniciar(String name, String mode) throws IOException;
}       
