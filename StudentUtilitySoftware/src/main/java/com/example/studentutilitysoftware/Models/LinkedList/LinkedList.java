package com.example.studentutilitysoftware.Models.LinkedList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T>{
    private Node<T> head;

    public LinkedList() {
        this.head = null;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    public void insert(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
    }

    public void delete(T data) {
        if (head == null) return;

        if (head.data.equals(data)) {
            head = head.next;
            return;
        }

        Node<T> temp = head;
        while (temp.next != null && !temp.next.data.equals(data)) {
            temp = temp.next;
        }

        if (temp.next != null) {
            temp.next = temp.next.next;
        }
    }

    public void display() {
        Node<T> temp = head;
        while (temp != null) {
            System.out.print(temp.data + " -> ");
            temp = temp.next;
        }
        System.out.println("null");
    }

    public boolean search(T data) {
        Node<T> temp = head;
        while (temp != null) {
            if (temp.data.equals(data)) return true;
            temp = temp.next;
        }
        return false;
    }

    public List<T> getExpenses() {
        List<T> expenses = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            expenses.add(current.data);
            current = current.next;
        }
        return expenses;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void clear() {
        head = null;
    }

    public void mergeSort(String attributeName) {
        head = mergeSortRecursive(head, attributeName);
    }

    private Node<T> mergeSortRecursive(Node<T> node, String attributeName) {
        if (node == null || node.next == null) {
            return node;
        }

        Node<T> middle = getMiddle(node);
        Node<T> nextOfMiddle = middle.next;

        middle.next = null;

        Node<T> left = mergeSortRecursive(node, attributeName);
        Node<T> right = mergeSortRecursive(nextOfMiddle, attributeName);

        return merge(left, right, attributeName);
    }

    private Node<T> merge(Node<T> left, Node<T> right, String attributeName) {
        Node<T> result;

        if (left == null) return right;
        if (right == null) return left;

        try {
            Field fieldLeft = left.data.getClass().getDeclaredField(attributeName);
            fieldLeft.setAccessible(true);
            Field fieldRight = right.data.getClass().getDeclaredField(attributeName);
            fieldRight.setAccessible(true);

            Object leftValue = fieldLeft.get(left.data);
            Object rightValue = fieldRight.get(right.data);

            if (leftValue instanceof javafx.beans.property.Property) {
                leftValue = ((javafx.beans.property.Property<?>) leftValue).getValue();
            }
            if (rightValue instanceof javafx.beans.property.Property) {
                rightValue = ((javafx.beans.property.Property<?>) rightValue).getValue();
            }

            if (!(leftValue instanceof Comparable) || !(rightValue instanceof Comparable)) {
                throw new IllegalArgumentException(
                        "Field value '" + attributeName + "' must implement Comparable. Found: " +
                                leftValue.getClass() + " and " + rightValue.getClass()
                );
            }

            Comparable comparableLeft = (Comparable) leftValue;
            Comparable comparableRight = (Comparable) rightValue;

            if (comparableLeft.compareTo(comparableRight) <= 0) {
                result = left;
                result.next = merge(left.next, right, attributeName);
            } else {
                result = right;
                result.next = merge(left, right.next, attributeName);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error accessing field for sorting: " + attributeName, e);
        }

        return result;
    }

    private Node<T> getMiddle(Node<T> node) {
        if (node == null) return node;

        Node<T> slow = node;
        Node<T> fast = node.next;

        while (fast != null) {
            fast = fast.next;
            if (fast != null) {
                slow = slow.next;
                fast = fast.next;
            }
        }

        return slow;
    }

    public int size() {
        int size = 0;
        Node<T> temp = head;
        while (temp != null) {
            size++;
            temp = temp.next;
        }
        return size;
    }

}
