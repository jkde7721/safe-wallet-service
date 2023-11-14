package com.wanted.safewallet.domain.category.persistence.repository;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.wanted.safewallet.domain.category.persistence.entity.Category;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    default Map<Long, Category> findAllMap() {
        List<Category> categoryList = findAll();
        return categoryList.stream().collect(toMap(Category::getId, identity()));
    }
}
