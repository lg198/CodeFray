package com.github.lg198.codefray.util;

import java.util.*;
import java.util.function.Consumer;

public class AssociatedPriorityQueue<T> implements Iterable<T> {

    private Map<T, Float> priority = new HashMap<>();

    private LinkedList<T> items = new LinkedList<>();

    public void add(T t, float p) {
        System.out.println("Adding " + t + ". Exists?: " + priority.containsKey(t) + ". Priority: " + p);
        priority.put(t, p);
        boolean found = false;
        for (int i = 0; i < items.size(); i++) {
            if (p < priority.get(items.get(i))) {
                items.add(i, t);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Not found, whoops!");
            items.add(t);
        }
    }

    public void remove(int i) {
        T t = items.remove(i);
        priority.remove(t);
    }

    public T get(int i) {
        return items.get(i);
    }

    public T getRem() {
        return items.remove(0);
    }

    public int size() {
        return items.size();
    }

    public void clear() {
        priority.clear();
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Spliterator<T> spliterator() {
        return items.spliterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        items.forEach(action);
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
}
