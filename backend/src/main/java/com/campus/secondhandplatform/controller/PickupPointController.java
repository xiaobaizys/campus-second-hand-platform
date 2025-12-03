package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.PickupPoint;
import com.campus.secondhandplatform.service.PickupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickup-points")
public class PickupPointController {

    @Autowired
    private PickupPointService pickupPointService;

    @GetMapping
    public ResponseEntity<List<PickupPoint>> getAllPickupPoints() {
        List<PickupPoint> pickupPoints = pickupPointService.getAllPickupPoints();
        return ResponseEntity.ok(pickupPoints);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PickupPoint>> getActivePickupPoints() {
        List<PickupPoint> pickupPoints = pickupPointService.getActivePickupPoints();
        return ResponseEntity.ok(pickupPoints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickupPoint> getPickupPointById(@PathVariable Long id) {
        return pickupPointService.getPickupPointById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PickupPoint> createPickupPoint(@RequestBody PickupPoint pickupPoint) {
        PickupPoint savedPickupPoint = pickupPointService.savePickupPoint(pickupPoint);
        return ResponseEntity.ok(savedPickupPoint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PickupPoint> updatePickupPoint(@PathVariable Long id, @RequestBody PickupPoint pickupPoint) {
        return pickupPointService.getPickupPointById(id)
                .map(existingPoint -> {
                    pickupPoint.setId(id);
                    PickupPoint updatedPoint = pickupPointService.savePickupPoint(pickupPoint);
                    return ResponseEntity.ok(updatedPoint);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePickupPoint(@PathVariable Long id) {
        pickupPointService.deletePickupPoint(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PickupPoint> updatePickupPointStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        PickupPoint updatedPoint = pickupPointService.updatePickupPointStatus(id, isActive);
        return ResponseEntity.ok(updatedPoint);
    }
}