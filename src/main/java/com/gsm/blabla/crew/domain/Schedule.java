package com.gsm.blabla.crew.domain;

import com.gsm.blabla.global.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Schedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime meetingTime;
    private String title;

    @OneToMany(mappedBy = "schedule")
    List<MemberSchedule> memberSchedules;

    @Builder
    public Schedule(LocalDateTime meetingTime, String title) {
        this.meetingTime = meetingTime;
        this.title = title;
    }
}
