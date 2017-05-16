package com.acme.publisher;


import com.acme.util.Tuple;
import com.acme.util.Pair;


public interface Source<E> {

    //Chanel, word, position
    Tuple<String, E, Integer> getNext();

}

/*
public interface Source<E> {
    Pair<String, E> getNext();
}
*/
