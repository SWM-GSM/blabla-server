package com.gsm.blabla.jwt.dao;

import com.gsm.blabla.jwt.domain.Jwt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {
    Optional<Jwt> findOneByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
