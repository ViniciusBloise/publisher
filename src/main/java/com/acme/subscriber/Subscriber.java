package com.acme.subscriber;


import com.acme.publisher.DataItem;
import com.acme.publisher.Listener;
import com.acme.publisher.Publisher;
import com.acme.publisher.Source;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Subscriber<E> implements Listener<E> {

    String name;
    private ArrayList<DataItem<String>> joinedList;
    private final Object lock1 = new Object();

    private Publisher<E> publisher;
    private String channel;

    //use persistent to write state before crash
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    private boolean persistent = false;


    public Subscriber( String name,  String channel,  Publisher<E> publisher) {
        this.name = name;
        joinedList = new ArrayList<>(10);

        this.publisher = publisher;
        this.channel = channel;
        publisher.subscribe(channel, this);
    }

    @Override
    public void onEvent(final String channel, final E e) {

        //Deserialize
        DataItem<String> dataItem = new DataItem<>(e.toString());
        boolean terminate = (dataItem.getIndex() < 0);

        if (terminate) {
            int index = dataItem.getIndex();
            if (index == Integer.MIN_VALUE)
                index = 0;
            dataItem.setIndex((index < 0) ? -index : index);

        }
        synchronized (lock1) {
            joinedList.add(dataItem);
        }
        //System.out.println(name + ": received an event: " + e.toString());

        if(persistent)
            onPersist(dataItem);

        if(terminate)
            onCompleted();
    }

    /**
     * Code to persist data and retrieve from where it stopped
     * @param dataItem
     */
    private void onPersist(DataItem<String> dataItem) {
        //write a file named ".subscriber.{name}.{channel}.txt" with all the serialized joinedList
        String fileName = String.format("/subscriber.%s.%s.txt", this.name, this.channel);

        File pathFile = new File(new File(System.getProperty("user.home")), fileName);

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(pathFile, true))) {
            bf.write(dataItem.toString() + "\n");
        } catch (IOException e) {

            e.printStackTrace();
        }
        finally {

        }
    }

    private void onCompleted() {
        //Order the DataItem list
        Collections.sort(joinedList, (a, b) -> a.compareTo(b));

        //System.out.print("..... Terminated ....");

        System.out.print( String.format("%s: ", this.name));
        List<DataItem<String>> list = this.joinedList;

        for (int i = 0; i < list.size(); i++) {
            DataItem<String> item = list.get(i);
            System.out.print(item.getValue() + " ");
        }
        System.out.println();

        this.publisher.unsubscribe(channel, this );
    }
}
