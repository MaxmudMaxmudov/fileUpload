package com.company.repository;

import com.company.entity.AttachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AttachRepository extends JpaRepository<AttachEntity,Integer>, JpaSpecificationExecutor<AttachEntity> {

    Optional<AttachEntity> findByKey(String key);
}
