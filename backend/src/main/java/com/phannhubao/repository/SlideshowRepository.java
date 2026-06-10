package com.phannhubao.repository;

import com.phannhubao.entity.Slideshow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideshowRepository extends JpaRepository<Slideshow, java.util.UUID> {
}
