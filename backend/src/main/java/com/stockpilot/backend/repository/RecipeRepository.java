package com.stockpilot.backend.repository;

import com.stockpilot.backend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
