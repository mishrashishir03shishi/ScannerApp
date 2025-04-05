package com.scanner.scannerapp.components;

import com.scanner.scannerapp.utils.Color;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Canvas {

    private static final int LENGTH = 10;

    private static final int WIDTH = 15;

    int[][] grid;

    private final Position currentPosition;

    public Canvas() {
        this.grid = new int[LENGTH][WIDTH];
        for(int i=0; i<LENGTH; i++){
            for (int j=0; j<WIDTH; j++){
                grid[i][j] = -1;
            }
        }
        this.currentPosition = new Position();
    }



    public void moveBySteps(int xSteps, int ySteps){
        Position intialPosition = new Position(currentPosition.getX(), currentPosition.getY());
        System.out.println("[Op1] Initial position : " + intialPosition.toString());
        System.out.println("[Op1] Step movement : " + "x: " + xSteps + " y: " + ySteps);
        int xFinal = currentPosition.getX() + xSteps;
        int yFinal = currentPosition.getY() + ySteps;
        if(xFinal<0){
            xFinal = 0;
        } else if (xFinal>=WIDTH) {
            xFinal = WIDTH-1;
        }

        if(yFinal<0){
            yFinal = 0;
        }
        else if(yFinal>=LENGTH){
            yFinal = LENGTH-1;
        }
        this.currentPosition.moveToNewPosition(xFinal, yFinal);
    }



    public void colorCurrentCell(Color color){
        this.grid[currentPosition.getY()][this.currentPosition.getX()] = color.getCode();
    }

    public void clearCanvas(){
        //TODO replicate the constructor code
        for(int i=0; i<LENGTH; i++){
            for (int j=0; j<WIDTH; j++){
                grid[i][j] = -1;
            }
        }
        this.currentPosition.moveToNewPosition(0, 0);
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public Color getCurrentPositionColor(){
        return Color.fromCode(this.grid[currentPosition.getY()][this.currentPosition.getX()]);
    }


    //TODO add current cell while returning this canvas
    public ArrayList<ArrayList<Integer>> getCompleteCanvas(){
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        for(int i=0; i<LENGTH; i++){
            ArrayList<Integer> innerList = new ArrayList<>();
            for (int j=0; j<WIDTH; j++){
                innerList.add(grid[i][j]);
            }
            list.add(innerList);
        }
        return list;
    }
}
