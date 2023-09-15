package com.gsm.blabla.report.application;

import com.gsm.blabla.crew.dao.CrewReportAnalysisRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.content.dao.MemberContentDetailRepository;
import com.gsm.blabla.content.domain.MemberContentDetail;
import com.gsm.blabla.member.dao.MemberRepository;
import com.gsm.blabla.report.dto.HistoryReportResponseDto;
import com.gsm.blabla.report.dto.HistoryResponseDto;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final VoiceFileRepository voiceFileRepository;
    private final CrewReportAnalysisRepository crewReportAnalysisRepository;
    private final MemberContentDetailRepository memberContentRepository;
    private final MemberRepository memberRepository;

    public Map<String, List<HistoryResponseDto>> getHistory() {
        Long memberId = SecurityUtil.getMemberId();

        List<HistoryResponseDto> histories = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 1. 내가 참여한 크루 리포트 정보를 가져온다. (리포트 생성 날짜, 리포트 아이디, 크루 이름, 보이스룸 진행 시간)
        List<CrewReport> crewReports = voiceFileRepository.findAllByMemberId(memberId).stream()
            .map(VoiceFile::getCrewReport)
            .distinct()
            .toList();

        List<HistoryReportResponseDto> crewHistory = new ArrayList<>();
        for (CrewReport crewReport : crewReports) {
            Optional<CrewReportAnalysis> crewReportAnalysis = crewReportAnalysisRepository.findByCrewReport(crewReport);
            // 내가 참여한 크루 리포트 정보가 있을 경우 crewHistory 리스트에 삽입
            crewReportAnalysis.ifPresent(
                analysis -> crewHistory.add(getCrewHistoryReportResponseDto(crewReport, formatter))
            );
        }

        // 2. 내가 연습한 컨텐츠 정보를 가져온다. (생성 시각, 컨텐츠 아이디, 컨텐츠 주제, 컨텐츠 이름)
        List<MemberContentDetail> memberContentDetails = memberContentRepository.findAllByMemberId(memberId);
        List<HistoryReportResponseDto> personalHistory = new ArrayList<>();
        for (MemberContentDetail memberContentDetail : memberContentDetails) {
            personalHistory.add(getContentHistoryReportResponseDto(memberContentDetail, formatter));
        }

        // 3. 크루 리포트 정보와 연습 컨텐츠 정보를 하나로 합친다.
        List<HistoryReportResponseDto> historyReports = Stream.concat(crewHistory.stream(), personalHistory.stream())
            .toList();

        // 4. 날짜별로 묶어서 최신순으로 정렬한다.
        Map<String, List<HistoryReportResponseDto>> groupedReports = historyReports.stream()
            .collect(Collectors.groupingBy(HistoryReportResponseDto::getDateTime));

        for (Map.Entry<String, List<HistoryReportResponseDto>> entry : groupedReports.entrySet()) {
            List<HistoryReportResponseDto> reportsWithoutDatetime = entry.getValue().stream()
                    .map(report -> HistoryReportResponseDto.of(
                        report.getId(),
                        report.getType(),
                        report.getInfo()
                    ))
                    .toList();

            histories.add(
                HistoryResponseDto.builder()
                    .datetime(entry.getKey())
                    .reports(reportsWithoutDatetime)
                    .build()
            );
        }

        Collections.sort(histories, Comparator.comparing(
                HistoryResponseDto::getDatetime,
                Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed()
        );

        return Map.of("histories", histories);
    }

    private HistoryReportResponseDto getCrewHistoryReportResponseDto(CrewReport crewReport, DateTimeFormatter formatter) {
        String durationTime = "2000-01-01 00:00";
        if (!Objects.equals(crewReport.getEndAt(), LocalDateTime.of(2000, 1, 1, 0, 0, 0))) {
            Duration duration = Duration.between(crewReport.getStartedAt(), crewReport.getEndAt());
            durationTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
        }

        String datetime = crewReport.getStartedAt().format(formatter); // 보이스룸 생성 시각
        return HistoryReportResponseDto.builder()
            .id(crewReport.getId())
            .type("crew")
            .info(
                Map.of(
                    "title", getReportTitle(crewReport),
                    "subTitle", durationTime
                )
            )
            .dateTime(datetime)
            .build();
    }

    private HistoryReportResponseDto getContentHistoryReportResponseDto(MemberContentDetail memberContentDetail, DateTimeFormatter formatter) {
        String title = memberContentDetail.getContentDetail().getTitle();
        String subTitle = memberContentDetail.getContentDetail().getContent().getTitle();
        String datetime = memberContentDetail.getJoinedAt().format(formatter);

        return HistoryReportResponseDto.builder()
            .id(memberContentDetail.getContentDetail().getId())
            .type("personal")
            .info(
                Map.of(
                    "title", title,
                    "subTitle", subTitle
                )
            )
            .dateTime(datetime)
            .build();
    }

    private String getReportTitle(CrewReport crewReport) {
        String starter = crewReport.getMember().getNickname();

        int numberOfParticipant = crewReport.getVoiceFiles().size();

        return String.format("%s 외 %d명", starter, numberOfParticipant-1);
    }
}
