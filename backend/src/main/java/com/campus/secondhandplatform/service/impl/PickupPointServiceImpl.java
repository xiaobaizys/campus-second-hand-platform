package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.PickupPoint;
import com.campus.secondhandplatform.repository.PickupPointRepository;
import com.campus.secondhandplatform.service.PickupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PickupPointServiceImpl implements PickupPointService {

    @Autowired
    private PickupPointRepository pickupPointRepository;

    @Override
    public List<PickupPoint> getAllPickupPoints() {
        return pickupPointRepository.findAll();
    }

    @Override
    public List<PickupPoint> getActivePickupPoints() {
        return pickupPointRepository.findAll().stream()
                .filter(PickupPoint::getIsActive)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PickupPoint> getPickupPointById(Long id) {
        return pickupPointRepository.findById(id);
    }

    @Override
    public PickupPoint savePickupPoint(PickupPoint pickupPoint) {
        return pickupPointRepository.save(pickupPoint);
    }

    @Override
    public void deletePickupPoint(Long id) {
        pickupPointRepository.deleteById(id);
    }

    @Override
    public PickupPoint updatePickupPointStatus(Long id, Boolean isActive) {
        PickupPoint pickupPoint = pickupPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("自提点不存在"));
        pickupPoint.setIsActive(isActive);
        return pickupPointRepository.save(pickupPoint);
    }
}