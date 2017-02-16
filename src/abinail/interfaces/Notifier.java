package abinail.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergii on 14.02.2017.
 */
public class Notifier<T>{
    private List<Listner<T>> listnersList = new ArrayList<>();

//    public void addEventListner(Listner<T> listner) {
//        listnersList.add(listner);
//    }
//
//    public void removeListner(Listner<T> listner) {
//        listnersList.remove(listner);
//    }

    public void raiseEvent(Object sender, T arg) {
        for (Listner<T> listner : listnersList) {
            listner.eventHandler(sender, arg);
        }
    }

    public Event<T> getEvent(){
        return new Event<T>() {
            @Override
            public void addEventListner(Listner<T> listner) {
                listnersList.add(listner);
            }

            @Override
            public void removeListner(Listner<T> listner) {
                listnersList.remove(listner);
            }
        };
    }
}
