package abinail.interfaces;

/**
 * Created by Sergii on 15.02.2017.
 */
public interface Event<T> {
    void addEventListner(Listner<T> listner);
    void removeListner(Listner<T> listner);
}
