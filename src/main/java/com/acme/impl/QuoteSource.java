package com.acme.impl;

import com.acme.publisher.Source;
import com.acme.util.Tuple;
import com.acme.util.Pair;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuoteSource implements Source<String>  {

    private Object lock1 = new Object();
    private Map<String, QuoteUnit> setOfQuotes = new HashMap<>();

    public void addQuoteUnit(QuoteUnit quoteUnit)
    {
        String quoteRef = quoteUnit.getChannel();
        setOfQuotes.put(quoteRef, quoteUnit);
    }

    //First: change the pair into a Tuple, in order to identity that index order inside the quote.
    //Channel, word, index
    @Override
    public Tuple<String, String, Integer> getNext() {
        try {

            //System.out.println("Source gets next.");
            long sleepTime =(long)(Math.random()*2000);
            //Thread.sleep(sleepTime);
            Thread.sleep(1);

            //As soon as choose a random word, disregard in the the next time
            int chooseQuote = Math.random() < 0.5 ? 0 : 1;

            String quoteRef = "Quote_" + (chooseQuote + 1);

            synchronized (lock1) {
                QuoteUnit quoteUnit = setOfQuotes.get(quoteRef);

                return quoteUnit.getNext();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Tuple<String, String, Integer>(null, null, 0);
    }

}
