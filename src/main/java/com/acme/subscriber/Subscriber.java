package com.acme.subscriber;


import com.acme.publisher.DataItem;
import com.acme.publisher.Listener;
import com.acme.publisher.Publisher;
import com.acme.publisher.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Subscriber<E> implements Listener<E> {

    String name;
    private ArrayList<DataItem<String>> joinedList;
    private Object lock1 = new Object();


    public Subscriber(String name, String channel, Publisher<E> publisher) {
        this.name = name;
        joinedList = new ArrayList<>(10);

        publisher.subscribe(channel, this);
    }

    @Override
    public void onEvent(final String channel, final E e) {

        //Deserialize
        DataItem<String> dataItem = new DataItem<>(e.toString());
        boolean terminate = (dataItem.getIndex() < 0);

        if(terminate) {
            int index = dataItem.getIndex();
            if (index == Integer.MIN_VALUE)
                index = 0;
            dataItem.setIndex((index < 0) ? -index : index);

        }
        synchronized (lock1) {
            joinedList.add( dataItem);
        }
        //System.out.println(name + ": received an event: " + e.toString());

        if(terminate)
        {
            //Order the DataItem list
            Collections.sort( joinedList, (a, b) -> a.compareTo(b));

            //System.out.print("..... Terminated ....");

            List<DataItem<String>> list = this.joinedList;
            /*for(a : list.listIterator())
            {
                //System.out.print()
            }
            */

            for (int i = 0; i < list.size(); i++) {
                DataItem<String> item = list.get(i);
                System.out.print(item.getValue() + " ");
            }

            System.out.println();
        }

    }

    public void terminate( E e)
    {
       // DataItem<String> dataItem = e.t
    }
}
