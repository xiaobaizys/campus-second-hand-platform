package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class StatisticsController {

    private final UserService userService;

    @Autowired
    public StatisticsController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户增长分布统计
     */
    @GetMapping("/growth-distribution")
    public ResponseEntity<Map<String, Object>> getGrowthDistribution() {
        Map<String, Object> growthDistribution = userService.getGrowthDistribution();
        return ResponseEntity.ok(growthDistribution);
    }
}
