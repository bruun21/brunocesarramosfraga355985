package com.bruno.artistalbum.repository;

import com.bruno.artistalbum.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalRepository extends JpaRepository<Regional, Long> {
    java.util.List<com.bruno.artistalbum.model.Regional> findByAtivoTrue();

    java.util.Optional<com.bruno.artistalbum.model.Regional> findByIdAndAtivoTrue(Integer id);
}
