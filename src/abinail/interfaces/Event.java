package abinail.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergii on 14.02.2017.
 */
public class Event<T> {
    private List<Listner<T>> listnersList = new ArrayList<>();

    public void addEventListner(Listner<T> listner) {
        listnersList.add(listner);
    }

    public void removeListner(Listner<T> listner) {
        listnersList.remove(listner);
    }

    public void fireEvent(Object sender, T arg) {
        for (Listner<T> listner : listnersList) {
            listner.eventHandler(sender, arg);
        }
    }
}
