package q5;

import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet implements ListSet {

    ReentrantLock lock = new ReentrantLock();
    Node head;


    public CoarseGrainedListSet() {
        head = new Node(null);
        // implement your constructor here
    }

    public boolean add(int value) {
        lock.lock();
        if(contains(value)){
            lock.unlock();
            return false;
        }
        Node node = new Node(value);
        Node current = head;
        while(current != null){
            if(current.next == null){
                current.next = node;
                //put it here. at the end.
                lock.unlock();
                return true;
            }
            if(current.next.value > value){
                Node previous = current.next;
                current.next = node;
                node.next = previous;
                lock.unlock();
                return true;
            }
            current = current.next;
        }
        lock.unlock();
        return false;
    }

    public boolean remove(int value) {
        lock.lock();
        if(!contains(value)){
            lock.unlock();
            return false;
        }
        Node current = head;
        Node previous = head;
        while(current.next != null){
            if(current.next.value == value){
                current.next = current.next.next;
                lock.unlock();
                return true;
            }
            previous = current;
            current = current.next;
        }
        if(current.value == value){
            previous.next = null;
            lock.unlock();
            return true;
        }
        lock.unlock();
        return false;
    }

    public boolean contains(int value) {
        // implement your contains method here
        boolean alreadyHeld = false;
        if(!lock.isHeldByCurrentThread()){
            lock.lock();
        }
        else{
            alreadyHeld = true;
        }
        Node current = head;
        while(current.next != null){
            if(current.next.value == value){
                if(!alreadyHeld) lock.unlock();
                return true;
            }
            current = current.next;
        }
        if(!alreadyHeld) lock.unlock();
        return false;
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
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
