package com.scanner.scannerapp.controller;


import com.scanner.scannerapp.components.Canvas;
import com.scanner.scannerapp.components.EventSink;
import com.scanner.scannerapp.entities.KeyPressEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/canvas")
public class CanvasController {

    private final Canvas canvas;

    @Autowired
    public CanvasController(Canvas canvas) {
        this.canvas = canvas;
    }

    @GetMapping("/grid")
    public Map<String, Object> getCanvasGrid() {
        Map<String, Object> response = new HashMap<>();
        response.put("grid", canvas.getCompleteCanvas());
        response.put("currentPosition", canvas.getCurrentPosition());
        return response;
    }

    @GetMapping("/clear")
    public ResponseEntity<String> clearCanvas() {
        canvas.clearCanvas();
        return ResponseEntity.ok("Canvas cleared.");
    }

}
