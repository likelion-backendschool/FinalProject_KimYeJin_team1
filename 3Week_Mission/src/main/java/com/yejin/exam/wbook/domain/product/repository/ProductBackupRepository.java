package com.yejin.exam.wbook.domain.product.repository;

import com.yejin.exam.wbook.domain.product.entity.ProductBackup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductBackupRepository extends JpaRepository<ProductBackup, Long> {
    Optional<ProductBackup> findByProductId(Long id);
}