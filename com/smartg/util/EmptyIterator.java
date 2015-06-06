package com.smartg.util;

import java.util.Iterator;

public class EmptyIterator<E> implements Iterator<E> {

    public boolean hasNext() {
	return false;
    }

    public E next() {
	return null;
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }

}
