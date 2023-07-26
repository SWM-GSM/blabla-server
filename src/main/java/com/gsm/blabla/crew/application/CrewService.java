package com.gsm.blabla.crew.application;

import com.gsm.blabla.crew.dao.ApplyMessageRepository;
import com.gsm.blabla.crew.dao.CrewMemberRepository;
import com.gsm.blabla.crew.dao.CrewRepository;
import com.gsm.blabla.crew.dao.CrewTagRepository;
import com.gsm.blabla.crew.domain.ApplyMessage;
import com.gsm.blabla.crew.domain.ApplyMessageStatus;
import com.gsm.blabla.crew.domain.Crew;
import com.gsm.blabla.crew.domain.CrewMember;
import com.gsm.blabla.crew.domain.CrewMemberRole;
import com.gsm.blabla.crew.domain.CrewMemberStatus;
import com.gsm.blabla.crew.domain.CrewTag;
import com.gsm.blabla.crew.dto.CrewRequestDto;
import com.gsm.blabla.crew.dto.CrewResponseDto;
import com.gsm.blabla.crew.dto.MessageRequestDto;
import com.gsm.blabla.global.exception.GeneralException;
import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.member.dto.MemberResponseDto;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewTagRepository crewTagRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final ApplyMessageRepository applyMessageRepository;

    public Map<String, Long> create(CrewRequestDto crewRequestDto) {
        Crew crew = crewRepository.save(crewRequestDto.toEntity());

        crewRequestDto.getTags().forEach(tag ->
                crewTagRepository.save(CrewTag.builder()
                    .crew(crew)
                    .tag(tag)
                    .build()
                )
        );

        crewMemberRepository.save(CrewMember.builder()
            .member(memberRepository.findById(SecurityUtil.getMemberId()).orElseThrow(
                () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
            ))
            .crew(crew)
            .status(CrewMemberStatus.JOINED)
            .role(CrewMemberRole.LEADER)
            .build()
        );

        return Collections.singletonMap("crewId", crew.getId());
    }

    @Transactional(readOnly = true)
    public CrewResponseDto get(String language, Long crewId) {
        Long memberId = SecurityUtil.getMemberId();
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );

        String status = "";
        Optional<CrewMember> crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Optional<ApplyMessage> applyMessage = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isPresent()) {
            status = crewMember.get().getStatus().toString();
        } else if (applyMessage.isPresent()) {
            status = applyMessage.get().getStatus().toString();
        } else {
            status = "NOTHING";
        }

        return CrewResponseDto.crewResponse(language, crew, status, crewMemberRepository);
    }

    // TODO: n + 1 문제 최적화
    @Transactional(readOnly = true)
    public Page<CrewResponseDto> getAll(String language, Pageable pageable) {
        return crewRepository.findAll(pageable).map(crew ->
            CrewResponseDto.crewListResponse(language, crew, crewMemberRepository));
    }

    // TODO: n + 1 문제 최적화
    public List<CrewResponseDto> getMyCrews() {
        Long memberId = SecurityUtil.getMemberId();
        List<CrewMember> crewMembers = crewMemberRepository.getByMemberIdAndStatus(memberId, CrewMemberStatus.JOINED);

        return crewMembers.stream()
            .map(crewMember -> CrewResponseDto.myCrewListResponse(crewMember.getCrew(), crewMemberRepository))
            .toList();
    }

    public Map<String, String> joinCrew(Long crewId, MessageRequestDto messageRequestDto) {
        Long memberId = SecurityUtil.getMemberId();
        Crew crew = crewRepository.findById(crewId).orElseThrow(
            () -> new GeneralException(Code.CREW_NOT_FOUND, "존재하지 않는 크루입니다.")
        );
        String message = "";

        boolean isJoined = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId).isPresent();
        if (isJoined) {
            throw new GeneralException(Code.CREW_ALREADY_JOINED, "이미 가입한 크루입니다.");
        }

        boolean isApplied = applyMessageRepository.getByCrewIdAndMemberId(crewId, memberId).isPresent();
        if (isApplied) {
            throw new GeneralException(Code.CREW_ALREADY_APPLIED, "이미 신청한 크루입니다.");
        }

        boolean isAutoApproval = crew.getAutoApproval();
        if (isAutoApproval) {
            crewMemberRepository.save(
                CrewMember.builder()
                    .member(memberRepository.findById(memberId).orElseThrow(
                    () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")
                ))
                    .crew(crew)
                    .role(CrewMemberRole.MEMBER)
                    .build()
            );
            message = "가입이 완료되었습니다.";
        } else {
            if (messageRequestDto == null) {
                throw new GeneralException(Code.APPLY_WITHOUT_MESSAGE, "크루에 가입하기 위해서는 참여 신청 문구가 필요합니다.");
            }

            applyMessageRepository.save(
                ApplyMessage.builder()
                    .message(messageRequestDto.getMessage())
                    .crew(crew)
                    .member(memberRepository.findById(memberId).orElseThrow(
                        () -> new GeneralException(Code.MEMBER_NOT_FOUND, "존재하지 않는 유저입니다.")))
                    .build()
            );

            message = "신청이 완료되었습니다.";
        }

        return Collections.singletonMap("message", message);
    }

    public Map<String, List<MemberResponseDto>> getWaitingList(Long crewId) {

        List<MemberResponseDto> members = applyMessageRepository.getByCrewIdAndStatus(crewId, ApplyMessageStatus.WAITING).stream()
            .map(ApplyMessage::getMember)
            .map(member -> MemberResponseDto.waitingListResponse(crewId, member, applyMessageRepository))
            .toList();

        return Collections.singletonMap("members", members);
    }
}
