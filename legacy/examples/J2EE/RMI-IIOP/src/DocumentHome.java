import java.rmi.RemoteException;
import jakarta.ejb.CreateException;
import jakarta.ejb.EJBHome;

// This interface defines the methods to create a DocumentBean.
public interface DocumentHome extends EJBHome {
    DocumentRemote create() throws RemoteException, CreateException;
}
