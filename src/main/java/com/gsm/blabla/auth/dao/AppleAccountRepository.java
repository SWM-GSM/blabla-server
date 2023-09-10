package com.gsm.blabla.auth.dao;

import com.gsm.blabla.auth.domain.AppleAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleAccountRepository extends JpaRepository<AppleAccount, String> {

    Optional<AppleAccount> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
