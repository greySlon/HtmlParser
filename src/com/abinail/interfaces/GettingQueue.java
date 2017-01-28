package com.abinail.interfaces;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 26.01.2017.
 */
public interface GettingQueue<T> {
    Queue<T> getQueue();
}
