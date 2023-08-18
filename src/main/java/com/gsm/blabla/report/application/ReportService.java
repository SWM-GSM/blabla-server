package com.gsm.blabla.report.application;

import com.gsm.blabla.crew.dao.CrewReportAnalysisRepository;
import com.gsm.blabla.crew.dao.VoiceFileRepository;
import com.gsm.blabla.crew.domain.CrewReport;
import com.gsm.blabla.crew.domain.CrewReportAnalysis;
import com.gsm.blabla.crew.domain.VoiceFile;
import com.gsm.blabla.global.util.SecurityUtil;
import com.gsm.blabla.practice.dao.MemberContentRepository;
import com.gsm.blabla.practice.domain.MemberContent;
import com.gsm.blabla.report.dto.HistoryReportResponseDto;
import com.gsm.blabla.report.dto.HistoryResponseDto;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private final MemberContentRepository memberContentRepository;

    public Map<String, List<HistoryResponseDto>> getHistory() {
        Long memberId = SecurityUtil.getMemberId();

        List<HistoryResponseDto> histories = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 1. 내가 이제까지 참여한 크루 리포트 정보를 가져온다. (리포트 생성 날짜, 리포트 아이디, 크루 이름, 보이스룸 진행 시간)
        List<CrewReport> crewReports = voiceFileRepository.findAllByMemberId(memberId).stream()
            .map(VoiceFile::getCrewReport)
            .distinct()
            .toList();

        List<HistoryReportResponseDto> crewHistory = new ArrayList<>();
        for (CrewReport crewReport : crewReports) {
            Optional<CrewReportAnalysis> crewReportAnalysis = crewReportAnalysisRepository.findByCrewReport(crewReport);
            crewReportAnalysis.ifPresent(
                analysis -> {
                    Duration duration = Duration.between(crewReport.getStartedAt(), analysis.getEndAt());
                    String durationTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());

                    String datetime = analysis.getCreatedAt().format(formatter);
                    crewHistory.add(
                        HistoryReportResponseDto.builder()
                            .id(crewReport.getCrew().getId())
                            .type("crew")
                            .info(
                                Map.of(
                                    "title", crewReport.getCrew().getName(),
                                    "subTitle", durationTime
                                )
                            )
                            .dateTime(datetime)
                            .build()
                    );
                }
            );
        }

        // 2. 내가 이제까지 연습한 컨텐츠 정보를 가져온다. (생성 시각, 컨텐츠 아이디, 컨텐츠 주제, 컨텐츠 이름)
        List<MemberContent> memberContents = memberContentRepository.findAllByMemberId(memberId);
        List<HistoryReportResponseDto> personalHistory = new ArrayList<>();
        for (MemberContent memberContent : memberContents) {
            String subTitle = memberContent.getContent().getGenre() + " - " + memberContent.getContent().getContentName();
            String datetime = memberContent.getJoinedAt().format(formatter);
            personalHistory.add(
                HistoryReportResponseDto.builder()
                    .id(memberContent.getContent().getId())
                    .type("personal")
                    .info(
                        Map.of(
                            "title", memberContent.getContent().getTopic(),
                            "subTitle", subTitle
                        )
                    )
                    .dateTime(datetime)
                    .build()

            );
        }

        // 3. 날짜별로 묶어서 최신순으로 정렬한다.
        List<HistoryReportResponseDto> historyReports = Stream.concat(crewHistory.stream(), personalHistory.stream())
            .toList();

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
        ).reversed());

        return Map.of("histories", histories);
    }
}
