package com.acme.publisher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import com.acme.util.Tuple;

public class Publisher<E> implements Runnable {

    private final Source<String> source;
    //Channel, listener
    private final ConcurrentHashMap<String, Set<Listener<E>>> hashofListeners = new ConcurrentHashMap<>(10);

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public Publisher(Source<String> source) {
        this.source = source;
    }


    public void subscribe(String channel, Listener<E> listener)
    {
        //If there's already a channel
        if(hashofListeners.containsKey(channel)) {
            synchronized (lock1) {
                Set<Listener<E>> setofListeners = hashofListeners.get(channel);
                if(setofListeners == null) {
                    setofListeners = new HashSet<>();
                }
                setofListeners.add( listener );
            }
        }
        else {
            Set<Listener<E>> setofListeners2 = new HashSet<>();
            setofListeners2.add( listener );

            synchronized (lock1) {
                hashofListeners.put(channel, setofListeners2);
            }
        }

    }
 /*   public synchronized void subscribe(String channel, Listener<E> listener) {
        listeners.get(channel).add(listener);
    }*/
/*
    public synchronized void unsubscribe(String channel, Listener<E> listener) {
        List<Listener<E>> lst = listeners.get(channel);
        lst.remove(listener);
    }
*/
    @Override
    public void run() {
        System.out.println("Thread started running. 1");

        while(!Thread.interrupted()) {
            try {
                //Channel, word, position
                Tuple<String, String, Integer> next = source.getNext();

                synchronized (lock2) {
                    //System.out.println("Channel: " + next.first());

                    if(hashofListeners.containsKey(next.first()))
                    {
                        //System.out.println("Contains...");
                        //Get the channel
                        Set<Listener<E>> setoflistener3 = hashofListeners.get(next.first());

                        for(Listener<E> listener3 : setoflistener3)
                        {

                            //dataItem.setIndex( next.third());
                            //dataItem.setValue( next.second());
                            String format = String.format("{%s|%s}", next.third(), next.second());

                            listener3.onEvent(next.first(), (E) format );
                        }
                    /*for(Listener<E> listener: listeners.get(next.first())) {
                        listener.onEvent(next.first(), next.second() );*/
                    }
                }
            } catch(Throwable t) {
                //ignoring it
            }
        }
    }

}
