package com.acme.impl;

import com.acme.publisher.Source;
import com.acme.util.Tuple;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by viniciusbloise on 17/05/17.
 */
public class QuoteUnit implements Source<String> {
    private ConcurrentHashMap<Integer, String> quoteMap;

    private String channel;
    private Object lock1 = new Object();

    public QuoteUnit(String channel, String quote) {
        this.channel = channel;
        this.quoteMap = new ConcurrentHashMap<>(10);

        PopulateQuoteMap(quote);
    }

    private void PopulateQuoteMap(String quote)
    {
        String[] words = quote.split("\\s", 0);
        for(int i = 0; i < words.length; i++)
        {
            this.quoteMap.put(i, words[i]);
        }
    }

    @Override
    public Tuple<String, String, Integer> getNext() {
        //Here must enter the concurrency;
        synchronized (lock1) {
            int index = ((int) (Math.random() * quoteMap.size())) % quoteMap.size();

            //Retrieved key and value
            String value = (new ArrayList<String>(quoteMap.values())).get(index);
            int key = (new ArrayList<Integer>(quoteMap.keySet())).get(index);

            quoteMap.remove(key);

            //Implement a trick. If key sent is negative, I consider the end of the subscription
            //If it is zero, I'll send Integer.MIN_VALUE
            if (quoteMap.size() == 0) {
                key = (key == 0) ? Integer.MIN_VALUE : -key;
            }
            return new Tuple<String, String, Integer>(channel, value, key);
        }
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
