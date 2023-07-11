package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.CrewTagRepository;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewTag;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.global.application.S3UploaderService;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class CrewService {

    private final S3UploaderService s3UploaderService;
    private final CrewRepository crewRepository;
    private final CrewTagRepository crewTagRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;

    public Map<String, Long> create(CrewRequestDto crewRequestDto, MultipartFile coverImage) {
        String defaultCoverUrl = "https://blabla-temp.s3.ap-northeast-2.amazonaws.com/cover/defalut-crew-cover.png";
        String coverUrl = coverImage == null ? defaultCoverUrl
            : s3UploaderService.uploadImage(coverImage, "cover");

        Crew crew = crewRepository.save(crewRequestDto.toEntity(coverUrl));

        crewRequestDto.getTags().forEach(tag ->
                crewTagRepository.save(CrewTag.builder()
                    .crew(crew)
                    .tag(tag)
                    .build()
                )
        );

        crewMemberRepository.save(CrewMember.builder()
            .member(memberRepository.findById(SecurityUtil.getMemberId()).orElse(null))
            .crew(crew)
            .build()
        );

        return Collections.singletonMap("crewId", crew.getId());
    }
}
