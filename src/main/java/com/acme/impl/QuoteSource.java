package com.acme.impl;

import com.acme.publisher.Source;
import com.acme.util.Tuple;
import com.acme.util.Pair;


import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.ArrayList;

public class QuoteSource implements Source<String>  {

    private final String[][] quotes = new String[][] {
        new String [] { "Beware", "of", "bugs", "in", "the", "above", "code;", "I", "have", "only", "proved", "it", "correct,", "not", "tried", "it." },
        new String [] {"Any", "inaccuracies", "in", "this", "index", "may", "be", "explained", "by", "the", "fact", "that", "it", "has", "been", "sorted", "with", "the", "help", "of", "a", "computer"}};

    private ConcurrentHashMap<Integer, String> quoteMap1;
    private ConcurrentHashMap<Integer, String> quoteMap2;

    private Object lock1 = new Object();

    public QuoteSource() {

        quoteMap1 = PopulateQuoteSourceMap(0);
        quoteMap2 = PopulateQuoteSourceMap(1);
    }

    public void WriteQuoteMap()
    {
        quoteMap1.forEach( (i, j) -> System.out.print(i + ": " + j + "\n"));
    }

    private ConcurrentHashMap<Integer, String> PopulateQuoteSourceMap(Integer chosenQuote)
    {
        ConcurrentHashMap<Integer, String> quoteMap = new ConcurrentHashMap<>();
        for(Integer i = 0; i < quotes[chosenQuote].length; i++ )
            quoteMap.put(i, quotes[chosenQuote][i]);
        return quoteMap;
    }

    //First: change the pair into a Tuple, in order to identity that index order inside the quote.
    //Channel, word, index
    @Override
    public Tuple<String, String, Integer> getNext() {
        try {

            //System.out.println("Source gets next.");
            long sleepTime =(long)(Math.random()*2000);
            Thread.sleep(sleepTime);
            //Thread.sleep(1);


            //As soon as choose a random word, disregard in the the next time

            int chooseQuote = Math.random() < 0.5 ? 0 : 1;

            ConcurrentHashMap<Integer, String> quoteMap = (chooseQuote == 0) ? quoteMap1 : quoteMap2;

            //Here must enter the concurrency;
            synchronized (lock1) {
                int index = ((int) (Math.random() * quoteMap.size())) % quoteMap.size();

                int j = 0; //Position in the array.
                int key = 0; //key found;


                //Retrieved key and value
                String value = (new ArrayList<String>(quoteMap.values())).get(index);
                key = (new ArrayList<Integer>(quoteMap.keySet())).get(index);

                quoteMap.remove(key);

                return new Tuple<String, String, Integer>("Quote_" + (chooseQuote + 1), value, key);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Tuple<String, String, Integer>(null, null, 0);
    }

}
