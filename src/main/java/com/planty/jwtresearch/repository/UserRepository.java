package com.planty.jwtresearch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.planty.jwtresearch.entity.User;

// User 엔티티에 매핑되는 Repository
public interface UserRepository extends JpaRepository<User, Long> { // JpaRepository : findAll(), save()
	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesByUsername(String username); // username 기준으로 회원정보 조회
}