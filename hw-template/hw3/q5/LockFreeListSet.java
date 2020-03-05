package q5;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeListSet implements ListSet {
    AtomicReference<Node> head;

    public LockFreeListSet() {
        head = new AtomicReference<>(new Node(null));
        // implement your constructor here
    }

    public boolean add(int value) {

        Node node = new Node(value);
        AtomicReference<Node> current = head;
        while(current != null){
            if(current.get().next.get() == null){
                if(!current.get().next.compareAndSet(null, node)) return add(value);
                //put it here. at the end.
                return true;
            }
            if(current.get().next.get().value == value){
                //value already here
                return false;
            }
            if(current.get().next.get().value > value){
                //found place to put
                Node next = current.get().next.get();
                node.next = new AtomicReference<>(next); //set next of new node
                if(current.get().deleted.get()) return add(value); //check if deleted. if so, restart.
                if(!current.get().next.compareAndSet(next, node)) return add(value);
                //atomic operation. old next should be what we found, new next should be node. if fails, restart.
                return true;
            }
            current = current.get().next;
        }
        return false;
    }

    public boolean remove(int value) {
        // implement your remove method here
        AtomicReference<Node> current = head;
        AtomicReference<Node> previous = head;
        while(current.get().next.get() != null){
            if(current.get().next.get().value == value){
                Node toDelete = current.get().next.get();
                Node newNext = current.get().next.get().next.get();
                if(newNext != null){
                    if(newNext.deleted.get()) return remove(value); //make sure new next not being deleted, restart
                }
                //found place to remove.
                current.get().next.get().deleted.compareAndSet(false, true);
                if(!current.get().next.compareAndSet(toDelete, newNext)) return remove(value);
                    //actually set current.next to current.next.next
                    //if failed, retry?
                return true;
            }
            previous = current;
            current = current.get().next;
        }
        if(current.get().value == value){
            current.get().deleted.set(true);
            if(!previous.get().next.compareAndSet(current.get(), null)) return remove(value);
            //set second to last node next to null. if fails, restart, because that node not pointing at last node anymore.
            return true;
        }
        return false;
    }

    public boolean contains(int value) {
        // implement your contains method here
        return false;
    }

    protected class Node {
        public Integer value;
        public AtomicReference<Node> next;
        public AtomicBoolean deleted;
        public Node(Integer x) {
            value = x;
            next = null;
            deleted = new AtomicBoolean(false);
            next = new AtomicReference<Node>(null);
        }
    }

    /*
      return the string of list, if: 1 -> 2 -> 3, then return "1,2,3,"
      check simpleTest for more info
    */
    public String toString() {
        Node current = head.get();
        StringBuilder sb = new StringBuilder();

        while (current.next.get() != null) {
            sb.append(current.next.get().value).append(",");
            current = current.next.get();
        }
        return sb.toString();
    }
}
