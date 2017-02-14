package abinail.interfaces;

/**
 * Created by Sergii on 14.02.2017.
 */
public interface Listner<T> {
    void eventHandler(Object sender, T args);
}
