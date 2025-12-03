package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.PlatformParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformParamRepository extends JpaRepository<PlatformParam, Long> {
    Optional<PlatformParam> findByParamKey(String paramKey);

    void deleteByParamKey(String paramKey);
}