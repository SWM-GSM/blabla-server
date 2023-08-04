package com.gsm.blabla.jwt.dao;

import com.gsm.blabla.jwt.domain.AppleAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleAccountRepository extends JpaRepository<AppleAccount, String> {

    Optional<AppleAccount> findByMemberId(Long memberId);
}
