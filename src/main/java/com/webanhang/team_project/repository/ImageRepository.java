package com.webanhang.team_project.repository;


import com.webanhang.team_project.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    public List<Image> findByProductId(int productId);
}
