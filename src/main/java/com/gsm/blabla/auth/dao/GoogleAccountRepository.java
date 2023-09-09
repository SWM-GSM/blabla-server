package com.gsm.blabla.auth.dao;

import com.gsm.blabla.auth.domain.GoogleAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleAccountRepository extends JpaRepository<GoogleAccount, String> {
    Optional<GoogleAccount> findById(String id);

    void deleteByMemberId(Long memberId);
}
