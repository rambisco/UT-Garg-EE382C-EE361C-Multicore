package queue;

import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeQueue implements MyQueue {
    AtomicStampedReference<Node> head;
    AtomicStampedReference<Node> tail;

    public LockFreeQueue() {
        Node node = new Node(null);
        head = new AtomicStampedReference<Node>(node, 0);
        tail = head;
        // implement your constructor here
    }

    public boolean enq(Integer value) {
        Node newNode = new Node(value);
        boolean success;
        AtomicStampedReference<Node> curTail;
        AtomicStampedReference<Node> curNext;
        while (true) {
            curTail = tail;
            curNext = curTail.getReference().next;
            if (curTail == tail) {
                if (curNext.getReference() == null) {
                    if (curTail.getReference().next.compareAndSet(curNext.getReference(), newNode, curNext.getStamp(), curNext.getStamp() + 1)) {
                        success = true;
                        break;
                    } else {
                        tail.compareAndSet(curTail.getReference(), curNext.getReference(), curTail.getStamp(), curTail.getStamp()+1);
                    }
                }
            }
        }
        tail.compareAndSet(curTail.getReference(), newNode, curTail.getStamp() , curTail.getStamp()+1);
        return success;
    }

    public Integer deq() {
        AtomicStampedReference<Node> curTail;
        AtomicStampedReference<Node> curHead;
        AtomicStampedReference<Node> curNext;
        while (true) {
            curTail = tail;
            curHead = head;
            curNext = head.getReference().next;
            if (curHead == head) {
                if (curHead.getReference() == curTail.getReference()) {
                    if (curNext.getReference() == null) {
                        return null;
                    }
                    tail.compareAndSet(curTail.getReference(), curNext.getReference(), curTail.getStamp(), curTail.getStamp()+1);
                } else {
                    Integer value = curNext.getReference().value;
                    if (head.compareAndSet(curHead.getReference(), curNext.getReference(), curHead.getStamp(), curHead.getStamp()+1)) {
                        return value;
                    }
                }
            }
        }
    }

    protected class Node {
        public Integer value;
        public AtomicStampedReference<Node> next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
