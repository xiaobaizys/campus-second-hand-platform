package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.PickupPoint;

import java.util.List;
import java.util.Optional;

public interface PickupPointService {
    List<PickupPoint> getAllPickupPoints();

    List<PickupPoint> getActivePickupPoints();

    Optional<PickupPoint> getPickupPointById(Long id);

    PickupPoint savePickupPoint(PickupPoint pickupPoint);

    void deletePickupPoint(Long id);

    PickupPoint updatePickupPointStatus(Long id, Boolean isActive);
}