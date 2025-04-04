package com.scanner.scannerapp.components;

import com.scanner.scannerapp.entities.KeyPressEvent;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class EventSink {

    private final Queue<KeyPressEvent> eventQueue = new ConcurrentLinkedQueue<>();

    public void enqueue(KeyPressEvent event) {
        System.out.println("event recieved" + event);
        eventQueue.add(event); // thread-safe
    }

    public List<KeyPressEvent> drainEvents() {
        List<KeyPressEvent> drained = new ArrayList<>();
        int numberOfElements = eventQueue.size();
        int counter = 0;
        while (counter<numberOfElements) {
            drained.add(eventQueue.poll());
            counter++;
        }
        return drained;
    }

    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }

}
