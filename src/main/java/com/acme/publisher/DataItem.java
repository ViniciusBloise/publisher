package com.acme.publisher;

import com.acme.util.Pair;

/**
 * Created by viniciusbloise on 16/05/17.
 */
public class DataItem<E> {

    private Integer index;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    private E value;


    void DataItem( Integer i, E value)
    {
        this.index = i;
        this.value = value;
    }

    public Pair<Integer, E> getItem()
    {
        return new Pair<Integer, E>(this.index, this.value);
    }

    @Override
    public String toString() {
        return String.format("{index=%s|value=%s}", this.index, this.value);
    }

    public DataItem( String serialized)
    {
        String first;
        String last;

        if(serialized.contains("|"))
        {
            first = serialized.substring(1, serialized.indexOf("|"));
            last = serialized.substring(serialized.indexOf("|") + 1, serialized.length() -1 );

            this.index = Integer.parseInt(first);
            this.value = (E) last;
        }
    }

    public Integer compareTo(DataItem<E> e)
    {
        return (e.getIndex() > this.getIndex()) ? -1:1;
    }
}
