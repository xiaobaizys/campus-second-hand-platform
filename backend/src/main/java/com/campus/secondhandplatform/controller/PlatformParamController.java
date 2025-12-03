package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.PlatformParam;
import com.campus.secondhandplatform.service.PlatformParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform-params")
public class PlatformParamController {

    @Autowired
    private PlatformParamService platformParamService;

    @GetMapping
    public ResponseEntity<List<PlatformParam>> getAllParams() {
        List<PlatformParam> params = platformParamService.getAllParams();
        return ResponseEntity.ok(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformParam> getParamById(@PathVariable Long id) {
        return platformParamService.getParamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<PlatformParam> getParamByKey(@PathVariable String key) {
        return platformParamService.getParamByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlatformParam> createParam(@RequestBody PlatformParam param) {
        PlatformParam savedParam = platformParamService.saveParam(param);
        return ResponseEntity.ok(savedParam);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformParam> updateParam(@PathVariable Long id, @RequestBody PlatformParam param) {
        return platformParamService.getParamById(id)
                .map(existingParam -> {
                    param.setId(id);
                    PlatformParam updatedParam = platformParamService.saveParam(param);
                    return ResponseEntity.ok(updatedParam);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable Long id) {
        platformParamService.deleteParam(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/key/{key}")
    public ResponseEntity<Void> deleteParamByKey(@PathVariable String key) {
        platformParamService.deleteParamByKey(key);
        return ResponseEntity.noContent().build();
    }
}