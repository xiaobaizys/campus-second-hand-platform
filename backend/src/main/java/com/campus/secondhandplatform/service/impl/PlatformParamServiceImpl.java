package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.PlatformParam;
import com.campus.secondhandplatform.repository.PlatformParamRepository;
import com.campus.secondhandplatform.service.PlatformParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformParamServiceImpl implements PlatformParamService {

    @Autowired
    private PlatformParamRepository platformParamRepository;

    @Override
    public List<PlatformParam> getAllParams() {
        return platformParamRepository.findAll();
    }

    @Override
    public Optional<PlatformParam> getParamById(Long id) {
        return platformParamRepository.findById(id);
    }

    @Override
    public Optional<PlatformParam> getParamByKey(String key) {
        return platformParamRepository.findByParamKey(key);
    }

    @Override
    public PlatformParam saveParam(PlatformParam param) {
        return platformParamRepository.save(param);
    }

    @Override
    public void deleteParam(Long id) {
        platformParamRepository.deleteById(id);
    }

    @Override
    public void deleteParamByKey(String key) {
        platformParamRepository.deleteByParamKey(key);
    }

    @Override
    public String getParamValue(String key, String defaultValue) {
        return platformParamRepository.findByParamKey(key)
                .map(PlatformParam::getParamValue)
                .orElse(defaultValue);
    }
}