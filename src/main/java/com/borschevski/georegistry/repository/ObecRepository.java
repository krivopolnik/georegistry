package com.borschevski.georegistry.repository;

import com.borschevski.georegistry.entity.Obec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObecRepository extends JpaRepository<Obec, Integer> {
}