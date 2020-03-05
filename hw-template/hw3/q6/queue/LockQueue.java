package queue;

import stack.EmptyStack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    ReentrantLock enq, deq;
    Node head;
    Node tail;
    AtomicInteger count;
    // you are free to add members

    public LockQueue() {
        head = new Node(null);
        tail = head;
        enq = new ReentrantLock();
        deq = new ReentrantLock();
        count = new AtomicInteger(1);
        // implement your constructor here
    }

    public boolean enq(Integer value) {
        try {
            if (value == null) {
                throw new NullPointerException();
            }
            enq.lock();
            Node newNode = new Node(value);
            tail.next = newNode;
            tail = newNode;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            enq.unlock();
            return true;
        }
    }

    public Integer deq() {
        Integer result = null;
        deq.lock();
        try {

            if (count.get() == 1) {
                throw new EmptyStack();
            }
            result = head.next.value;
            head = head.next;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            deq.unlock();

        }
        // implement your deq method here
        return result;
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
