package com.gsm.blabla.member.application;

import com.gsm.blabla.member.dao.MemberRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public Map<String, Boolean> isNicknameDuplicated(String nickname) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("isDuplicated", memberRepository.findByNickname(nickname).isPresent());

        return result;
    }
}
