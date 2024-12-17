package com.example.studentutilitysoftware.Models.Stack;

import java.util.LinkedList;

public class NewStack<T> {

    private LinkedList<T> stack;

    public NewStack() {
        stack = new LinkedList<>();
    }

    public synchronized void pushItem(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        stack.addFirst(item);
        System.out.println("Item added: " + item);
    }

    public synchronized T popItem() {
        if (stack.isEmpty()) {
            System.out.println("No items to remove.");
            return null;
        }
        T removedItem = stack.removeFirst();
        System.out.println("Item removed: " + removedItem);
        return removedItem;
    }

    public synchronized T peekItem() {
        if (stack.isEmpty()) {
            System.out.println("No items to display.");
            return null;
        }
        T topItem = stack.getFirst();
        System.out.println("Top item: " + topItem);
        return topItem;
    }

    public synchronized boolean isEmpty() {
        return stack.isEmpty();
    }

    public synchronized int getSize() {
        return stack.size();
    }

    public synchronized LinkedList<T> getStack() {
        return stack;
    }
}

