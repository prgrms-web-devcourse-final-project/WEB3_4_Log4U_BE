package com.example.log4u.domain.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByNickname(String nickname);

	Boolean existsByNickname(String nickname);

	Optional<User> findByProviderId(String providerId);

	@Query(value = """
		SELECT u FROM User u
		LEFT JOIN (SELECT f.targetId, COUNT(f) as followerCount 
		          FROM Follow f 
		          GROUP BY f.targetId) fc 
		ON u.userId = fc.targetId
		WHERE (:nickname IS NULL OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :nickname, '%')))
		AND u.userId < :cursorId
		ORDER BY fc.followerCount DESC NULLS LAST, u.userId DESC
		""")
	Slice<User> searchUsersByCursor(
		@Param("nickname") String nickname,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);
}
