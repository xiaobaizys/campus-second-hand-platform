package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.PlatformParam;

import java.util.List;
import java.util.Optional;

public interface PlatformParamService {
    List<PlatformParam> getAllParams();

    Optional<PlatformParam> getParamById(Long id);

    Optional<PlatformParam> getParamByKey(String key);

    PlatformParam saveParam(PlatformParam param);

    void deleteParam(Long id);

    void deleteParamByKey(String key);

    String getParamValue(String key, String defaultValue);
}