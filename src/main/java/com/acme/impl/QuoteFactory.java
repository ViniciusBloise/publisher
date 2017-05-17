package com.acme.impl;

/**
 * Created by viniciusbloise on 16/05/17.
 * Thread-safe Singleton pattern that creates its instance at the first call
 */
public class QuoteFactory {
    private static QuoteFactory ourInstance = new QuoteFactory();


    public static QuoteFactory getInstance() {
        return ourInstance;
    }

    private QuoteFactory() {
    }

    public String[] loadQuotes(String path)
    {
        String[] quotes = ResourceLoader.load(path);
        return quotes;
    }
}
