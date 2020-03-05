package stack;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack implements MyStack {
    AtomicReference<Node> top = new AtomicReference<>(null);
    // you are free to add members

    public LockFreeStack() {
        //top  = ;
        // implement your constructor here
    }

    public boolean push(Integer value) {
        // implement your push method here
        Node newNode = new Node(value);
        while(true) {
            Node oldTop = top.get();
            newNode.next = oldTop;
            if (top.compareAndSet(oldTop, newNode)) {
//                System.out.println("finished pushing for thread: " + Thread.currentThread().getId());
                return true;
            }
            else Thread.yield();
        }
    }

    public Integer pop() throws EmptyStack {
        while(true) {
            Node oldTop = top.get();
            if (oldTop == null) throw new EmptyStack();
            else if (top.compareAndSet(oldTop, oldTop.next)) return oldTop.value;
            else Thread.yield();
        }
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }

    public String toString() {
        Node current = top.get();
        StringBuilder sb = new StringBuilder();

        while (current != null) {
            sb.append(current.value).append(",");
            current = current.next;
        }
        return sb.toString();
    }
}


