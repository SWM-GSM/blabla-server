package com.gsm.blabla.jwt.application;

import com.gsm.blabla.jwt.TokenProvider;
import com.gsm.blabla.jwt.dao.JwtRepository;
import com.gsm.blabla.jwt.domain.Jwt;
import com.gsm.blabla.jwt.dto.JwtDto;
import com.gsm.blabla.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {
    private final TokenProvider tokenProvider;
    private final JwtRepository jwtRepository;

    public JwtDto issueJwt(Member member) {
        jwtRepository.findOneByMemberId(member.getId()).ifPresent(jwtRepository::delete);
        jwtRepository.flush();
        JwtDto jwtDto = tokenProvider.generateJwt(member);
        Jwt jwt = Jwt.builder()
            .member(member)
            .refreshToken(jwtDto.getRefreshToken()).build();
        jwtRepository.save(jwt);

        return jwtDto;
    }
}
