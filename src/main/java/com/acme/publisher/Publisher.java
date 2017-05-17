package com.acme.publisher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.acme.util.Tuple;

/**
 * Publisher, first version - checked subscribe and unsubscribe
 * @param <E>
 */
public class Publisher<E> implements Runnable {

    private final Source<String> source;
    //Channel, listener
    //Here I'm using a Set instead of List to avoid duplicate listeners (same object)
    private final ConcurrentHashMap<String, Set<Listener<E>>> hashofListeners = new ConcurrentHashMap<>(10);

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public Publisher(Source<String> source) {
        this.source = source;
    }

    /**
     * Subscribe a listener of quotes to a certain channel. There can be multiple listeners subscribing to that same channel
     * Channel is bound to a quote. When the quote finishes to the listener, it can publish it in a correct order
     * @param channel
     * @param listener
     */
    public void subscribe(String channel, Listener<E> listener) {
        //Starts a thread safe zone to create the hash and hashSet of listeners
        synchronized (lock1) {
            //If there's already a channel, get the channel and add the listener to the others in a hashSet (thread safe)
            if (hashofListeners.containsKey(channel)) {
                Set<Listener<E>> setofListeners = hashofListeners.get(channel);
                if (setofListeners == null) {
                    setofListeners = new HashSet<>();
                }
                setofListeners.add(listener);
            } //If there isn't a channel, create the channel and the new hashSet (thread safe List)
            else {
                Set<Listener<E>> setofListeners2 = new HashSet<>();
                setofListeners2.add(listener);

                hashofListeners.put(channel, setofListeners2);
            }
        }
    }

    /**
     * Unsubscribe a listener of quotes
     * @param channel
     * @param listener
     */
     public void unsubscribe(String channel, Listener<E> listener) {

         //Starts the same thread safe area as in the subscribe method
         synchronized (lock1)
         {
             if(hashofListeners.containsKey(channel)) {
                 Set<Listener<E>> setofListeners = hashofListeners.get(channel);
                 setofListeners.remove(listener);
             }
         }
     }

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
                            String format = String.format("{%s|%s}", next.third(), next.second());

                            listener3.onEvent(next.first(), (E) format );
                        }
                    }
                }
            } catch(Throwable t) {
                //ignoring it
            }
        }
    }
}
