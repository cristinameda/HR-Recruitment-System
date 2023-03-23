package com.nagarro.candidatemanagement.utils;

import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.InterestedPositionDTO;
import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import com.nagarro.candidatemanagement.model.File;
import com.nagarro.candidatemanagement.model.FileType;
import com.nagarro.candidatemanagement.model.InterestedPosition;
import com.nagarro.candidatemanagement.model.Interview;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class TestDataBuilder {
    private TestDataBuilder() {

    }

    public static CandidateDTO buildCandidateDTO(Long id, String name, String email) {
        CandidateDTO candidateDTO = new CandidateDTO(id, name, "0735456654", email, "Cluj", 4, "Computer Science", "Facebook", LocalDate.of(2001, 3, 3), CandidateStatus.NO_STATUS);
        candidateDTO.setInterestedPositionsDTO(generatePositionDTOList());
        candidateDTO.setAssignedUsers(List.of("User11", "User2", "User3"));
        return candidateDTO;
    }

    public static Candidate buildCandidate(String name, String email) {
        Candidate candidate = new Candidate(name, "0735456654", email, "Cluj", 4, "Computer Science", "Facebook", LocalDate.of(2001, 3, 3), CandidateStatus.HIRED);
        candidate.setInterestedPositions(generatePositionList());
        candidate.setAssignedUsers(List.of("User11", "User2", "User3"));
        candidate.setFiles(List.of(new File("test", FileType.CV, "hello".getBytes()), new File("test", FileType.GDPR, "hello".getBytes())));
        return candidate;
    }

    public static InterviewDTO buildInterviewDTO(long candidateId) {
        return new InterviewDTO(candidateId, LocalDateTime.now().plus(10, ChronoUnit.DAYS), "8 Broadway REDHILL RH2 4HT");
    }

    public static Interview buildInterview(long candidateId) {
        return new Interview(candidateId, LocalDateTime.now().plus(10, ChronoUnit.DAYS), "8 Broadway REDHILL RH2 4HT");
    }

    /**
     * email: test@yahoo.com,
     * password: pass1
     * role: HrRepresentative
     */
    public static String buildHrRepresentativeToken() {
        return "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHlhaG9vLmNvbSIsInJvbGUiOiJIclJlcHJlc2VudGF0aXZlIiwiZXhwIjoxNjU5NTQwODAwLCJpYXQiOjE2NTk1NDAyMDB9.f67JtQsj_K7ctkrr8dMyEXIkITg0d98Gv9qU1-3dyH9QQySDm3bfI5WcFtQcVPomzELecfrlH6GqP26ByBrYmw";
    }

    /**
     * email: thirdTest@yahoo.com,
     * password: pass3
     * role: PTE
     */
    public static String buildPTEToken() {
        return "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0aGlyZFRlc3RAeWFob28uY29tIiwicm9sZSI6IlBURSIsImV4cCI6MTY1OTU5OTEyMSwiaWF0IjoxNjU5NTk4NTIxfQ.jay62mK8D2CdXs2gSA32bWtcMzMVdIwMpVqPR1cFVoEcjyWh6kSVDY1YQOKlxy546A6J0p3ZcLRT3NFwU9KEvA";
    }

    /**
     * email: anotherTest@yahoo.com,
     * password: pass2
     * role: TechnicalInterviewer
     */
    public static String buildTechnicalInterviewerToken() {
        return "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbm90aGVyVGVzdEB5YWhvby5jb20iLCJyb2xlIjoiVGVjaG5pY2FsSW50ZXJ2aWV3ZXIiLCJleHAiOjE2NTk2MDYzNTAsImlhdCI6MTY1OTYwNTc1MH0.7WdOAxMDyLVmzlxOAKFG1wshXCR-0wbeIPRK_g5GM805k42NaRbkHFCIdj4Odnb48JPYQ9QG-3-iTGDSeD6QIQ";
    }

    public static UserDetails buildUserDetails(String email, String role) {
        return new UserDetails(email, role);
    }


    public static List<CandidateDTO> candidateList() {
        return List.of(buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com"),
                buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com"),
                buildCandidateDTO(3L, "Candidate3", "candidate3@yahoo.com"),
                buildCandidateDTO(4L, "Candidate4", "candidate4@yahoo.com"),
                buildCandidateDTO(5L, "Candidate5", "candidate5@yahoo.com"),
                buildCandidateDTO(6L, "Candidate6", "candidate6@yahoo.com"));
    }

    public static List<InterestedPositionDTO> generatePositionDTOList() {
        List<InterestedPositionDTO> interestedPositionDTOS = new ArrayList<>();
        interestedPositionDTOS.add(new InterestedPositionDTO(1L, "Back-end developer"));
        interestedPositionDTOS.add(new InterestedPositionDTO(2L, "Cloud system engineer"));
        interestedPositionDTOS.add(new InterestedPositionDTO(3L, "Cloud/software architect"));
        return interestedPositionDTOS;
    }

    public static List<InterestedPosition> generatePositionList() {
        List<InterestedPosition> interestedPosition = new ArrayList<>();
        interestedPosition.add(new InterestedPosition(1L, "Back-end developer"));
        interestedPosition.add(new InterestedPosition(2L, "Cloud system engineer"));
        interestedPosition.add(new InterestedPosition(3L, "Cloud/software architect"));
        return interestedPosition;
    }

    public static List<UserEmailRoleDTO> generateUserEmailRoleDTOListWithRequiredRoles() {
        List<UserEmailRoleDTO> usersToCandidateDTOS = new ArrayList<>();
        usersToCandidateDTOS.add(new UserEmailRoleDTO("marktwain@gmail.com", "HrRepresentative"));
        usersToCandidateDTOS.add(new UserEmailRoleDTO("janaustin@yahoo.com", "HrRepresentative"));
        usersToCandidateDTOS.add(new UserEmailRoleDTO("kateroe@gmail.com", "TechnicalInterviewer"));
        usersToCandidateDTOS.add(new UserEmailRoleDTO("bellahadid@gmail.com", "PTE"));
        return usersToCandidateDTOS;
    }

    public static UpdateCandidateStatusDTO generateRequiredCandidateAndStatus(String status) {
        CandidateDTO candidateDTO = buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        return new UpdateCandidateStatusDTO(candidateDTO.getEmail(), CandidateStatus.valueOf(status));
    }
}
