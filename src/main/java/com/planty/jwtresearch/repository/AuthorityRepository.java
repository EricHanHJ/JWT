package com.planty.jwtresearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planty.jwtresearch.entity.Authority;

// Authority 엔티티에 매핑되는 Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}