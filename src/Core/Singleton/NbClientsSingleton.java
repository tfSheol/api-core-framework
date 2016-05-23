package Core.Singleton;

/**
 * Created by teddy on 04/05/2016.
 */
public class NbClientsSingleton {
    private int nbClients = 0;

    private NbClientsSingleton() {
    }

    private static class NbClientsSingletonHolder {
        private static NbClientsSingleton instance = new NbClientsSingleton();
    }

    public static NbClientsSingleton getInstance() {
        return NbClientsSingletonHolder.instance;
    }

    public int getNbClients() {
        return nbClients;
    }

    public void addClient() {
        nbClients++;
    }

    public void delClient() {
        nbClients--;
    }

    public void razClient() {
        nbClients = 0;
    }
}
