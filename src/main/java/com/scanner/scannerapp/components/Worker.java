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
     */
    private void startWorkerLoop() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                if (!eventSink.isEmpty()) {
                    performOperation1();
                } else {
                    if(coloringRedRequired){
                        performOperation2();
                    }
                    else{
                        try{
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e){
                            System.out.println(e.getMessage());
                        }
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
            int stepsMoved = processBufferedEvents(bufferedEvents);
            System.out.println(stepsMoved);
            // if there is any movement then simulate moving
            if(stepsMoved>0){
                long sleepTime = getSleepTime(stepsMoved);
                Thread.sleep(sleepTime);
            }
            if(isColouringGreenRequired()){
                canvas.colorCurrentCell(Color.GREEN);
                canvasWebSocketHandler.broadcastUpdate(
                        new CanvasUpdateEvent(
                                canvas.getCurrentPosition(),
                                Color.GREEN.getCode()
                        )
                );
                //TODO publish event for making the dot green at canvas.getCurrentPosition
            }
            coloringRedRequired = true;
        }
    }

    private void performOperation2() throws InterruptedException {
        System.out.println("[Op2] Started at " + System.currentTimeMillis());
        Thread.sleep(2000);
        canvas.colorCurrentCell(Color.RED);
        //TODO publish event to color the current cell RED
        canvasWebSocketHandler.broadcastUpdate(
                new CanvasUpdateEvent(
                        canvas.getCurrentPosition(),
                        Color.RED.getCode()
                )
        );
        coloringRedRequired = false;
    }

    private int processBufferedEvents(List<KeyPressEvent> events) {
        int xSteps = 0, ySteps = 0;
        for (KeyPressEvent event : events) {
            switch (event.getDirection()){
                case UP:
                    ySteps--;
                    break;
                case DOWN:
                    ySteps++;
                    break;
                case RIGHT:
                    xSteps++;
                    break;
                case LEFT:
                    xSteps--;
                    break;
                default:
                    throw new IllegalStateException("Unexpected direction: " + event.getDirection());
            }
        }
        return canvas.moveBySteps(xSteps, ySteps);
    }

    private boolean isColouringGreenRequired(){
        return !canvas.getCurrentPositionColor().equals(Color.GREEN);
    }

    /*
    What if the cell is already red?
    //TODO think about this case if anything could go wrong
     */
    private boolean isColouringRedRequired(){
        return !canvas.getCurrentPositionColor().equals(Color.RED);
    }

    private long getSleepTime(int size){
        double time =  3*Math.sqrt(size);
        return (long) time*1000;
    }
}
