package q5;

import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet implements ListSet {
    // you are free to add members
    Node head;
    Node tail;

    public FineGrainedListSet() {
        head = new Node(null);
        tail = head;
        // implement your constructor here
    }

    public boolean add(int value) {
        Node node = new Node(value);
        Node current = head;
        Node previous;
        current.lock.lock();
        while(current.next != null){
            current.next.lock.lock();
            if(current.next.value == value){
                current.next.lock.unlock();
                current.lock.unlock();
                return false;
            }
            if(current.next.value > value){
                Node future = current.next;
                current.next = node;
                node.next = future;
                future.lock.unlock();
                current.lock.unlock();
                return true;
            }
            previous = current;
            current = current.next;
            previous.lock.unlock();
        }
        current.next = node;
        current.lock.unlock();
        return true;
        // implement your add method here
    }

    public boolean remove(int value) {
        Node current = head;
        Node previous;
        current.lock.lock();
        while(current.next != null){
            current.next.lock.lock();
            if(current.next.value == value){
                previous = current.next;
                current.next = current.next.next;
                previous.lock.unlock();
                current.lock.unlock();
                return true;
            }
            previous = current;
            current = current.next;
            previous.lock.unlock();
        }
        current.lock.unlock();
        return false;
    }

    public boolean contains(int value) {
//        head.lock.lock();
//        if (head.next == null) {
//            head.lock.unlock();
//            return false;
//        }
//        Node current = head.next;
        Node current = head;
        Node previous;
        current.lock.lock();
//        if (current.value == value) {
//            current.lock.unlock();
//            head.lock.unlock();
//            return true;
//        }
        while(current.next != null){
            current.next.lock.lock();
            if(current.next.value == value){
                current.next.lock.unlock();
                current.lock.unlock();
//                head.lock.unlock();
                return true;
            }
            previous = current;
            current = current.next;
            if(previous == head) continue;
            previous.lock.unlock();
        }
        current.lock.unlock();
        //head.lock.unlock();
        return false;
    }

    protected class Node {
        public Integer value;
        public Node next;
        public ReentrantLock lock;

        public Node(Integer x) {
            lock = new ReentrantLock();
            value = x;
            next = null;
        }
    }

    /*
      return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
      check simpleTest for more info
    */
    public String toString() {
        Node current = head;
        StringBuilder sb = new StringBuilder();

        while (current.next != null) {
            sb.append(current.next.value).append(",");
            current = current.next;
        }
        return sb.toString();
    }
}
