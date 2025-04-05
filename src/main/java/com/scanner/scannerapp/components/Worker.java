package com.scanner.scannerapp.components;

import com.scanner.scannerapp.entities.KeyPressEvent;
import com.scanner.scannerapp.utils.Color;
import com.scanner.scannerapp.websocket.CanvasWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;

@Component
public class Worker {

    private final EventSink eventSink;

    private final Canvas canvas;

    private final CanvasWebSocketHandler canvasWebSocketHandler;

    private boolean coloringRedRequired = false;

    @Autowired
    public Worker(EventSink eventSink, Canvas canvas, CanvasWebSocketHandler canvasWebSocketHandler) {
        this.eventSink = eventSink;
        this.canvas = canvas;
        this.canvasWebSocketHandler = canvasWebSocketHandler;
        startWorkerLoop();
    }

    /*
    1. Keep on checking whether eventSink has filled after each completion of operation1
    2. if it has elements perform Operation1
    3. else check whether it is right feasible to perform operation 2
    4. if yes then perform, else go back to checking again

    if already focussed do not do anything if steps=0
    Key presses were there but Did not move any step:
        if color is already green do not do anything
        if it is red also do not do anything as the microscope is already focussed
     */
    //TODO add 20ms wait time
    private void startWorkerLoop() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                if (!eventSink.isEmpty()) {
                    performOperation1();
                } else {
                    if(coloringRedRequired){
                        performOperation2();
                    }
                }
            }
        });
    }

    /*
    1. Drain the elements
    2. Process the elements
    3. After processing, calculate the sleep time required
    4. sleep and simulate work
    5. set the current position of the grid to this new position
    6. Color the current position as green
     */
    private void performOperation1() throws InterruptedException {
        System.out.println("[Op1] Started at " + System.currentTimeMillis());

        List<KeyPressEvent> bufferedEvents = eventSink.drainEvents();

        if (!bufferedEvents.isEmpty()) {
            System.out.println("[Op1] Dequeued and processing " + bufferedEvents.size() + " event(s)");
            System.out.println("[Op1] ");
            int stepsMoved = processBufferedEvents(bufferedEvents);
            System.out.println(stepsMoved);
            // if there is any movement then simulate moving
            if(stepsMoved>0){
                long sleepTime = getSleepTime(stepsMoved);
                Thread.sleep(sleepTime);
                canvas.colorCurrentCell(Color.GREEN);
                canvasWebSocketHandler.broadcastUpdate(
                        new CanvasUpdateEvent(
                                canvas.getCurrentPosition(),
                                Color.GREEN.getCode()
                        )
                );
                coloringRedRequired = true;
            }
            else {
                if(canvas.getCurrentPositionColor().equals(Color.GREEN)){
                    coloringRedRequired = true;
                }
            }
        }
    }

    private void performOperation2() throws InterruptedException {
        System.out.println("[Op2] Started at " + System.currentTimeMillis());
        Thread.sleep(2000);
        canvas.colorCurrentCell(Color.RED);
        canvasWebSocketHandler.broadcastUpdate(
                new CanvasUpdateEvent(
                        canvas.getCurrentPosition(),
                        Color.RED.getCode()
                )
        );
        coloringRedRequired = false;
    }

    //Process each element individually. For each element, calculate the new position

    private int processBufferedEvents(List<KeyPressEvent> events) {
        Position initialPosition = new Position(canvas.getCurrentPosition().getX(), canvas.getCurrentPosition().getY());
        for (KeyPressEvent event : events) {
            switch (event.getDirection()){
                case UP:
                    canvas.moveBySteps(0, -1);
                    break;
                case DOWN:
                    canvas.moveBySteps(0, 1);
                    break;
                case RIGHT:
                    canvas.moveBySteps(1, 0);
                    break;
                case LEFT:
                    canvas.moveBySteps(-1, 0);
                    break;
                default:
                    throw new IllegalStateException("Unexpected direction: " + event.getDirection());
            }
        }

        return Math.abs(canvas.getCurrentPosition().getX()-initialPosition.getX()) + Math.abs(canvas.getCurrentPosition().getY() - initialPosition.getY());
    }

    private long getSleepTime(int size){
        double time =  3*Math.sqrt(size);
        return (long) time*1000;
    }
}
