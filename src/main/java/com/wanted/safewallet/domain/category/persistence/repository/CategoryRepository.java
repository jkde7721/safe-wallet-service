package com.wanted.safewallet.domain.category.persistence.repository;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
