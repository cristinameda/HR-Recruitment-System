package com.nagarro.candidatemanagement.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nagarro.candidatemanagement.annotation.Adult;
import com.nagarro.candidatemanagement.annotation.UniqueEmail;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

public class CandidateDTO {
    @Id
    private long id;
    @NotBlank(message = "Name is mandatory!")
    private String name;
    @NotBlank(message = "Phone number is mandatory!")
    @Pattern(regexp = "(([+]|[0][0])*[0-9]){10,15}", message = "Phone number is invalid!")
    private String phoneNumber;
    @NotNull(message = "Email is mandatory!")
    @Email(regexp = "^([a-zA-Z]+[0-9]*[-_.]*)+[a-zA-Z]+[0-9]*" + // [email name]
            "@([a-zA-Z]+[0-9]*[.-]*)+[a-zA-Z]+[0-9]*" + // [app]
            "[.][a-zA-Z]{2,4}", // [domain]
            message = "Email is invalid!")
    @UniqueEmail(message = "This email is already taken!")
    private String email;
    @NotBlank(message = "City is mandatory!")
    private String city;
    @PositiveOrZero(message = "Experience years must be a number greater or equal than 0!")
    private int experienceYears;
    @NotBlank(message = "Faculty is mandatory!")
    private String faculty;
    @NotBlank(message = "Recruitment channel is mandatory!")
    private String recruitmentChannel;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Adult(message = "You must be an adult!")
    private LocalDate birthDate;
    @JsonProperty("interestedPositions")
    @NotEmpty(message = "Interested positions are mandatory!")
    private List<InterestedPositionDTO> interestedPositionsDTO;
    @JsonIgnore
    private List<FileDTO> files;
    private List<String> assignedUsers;
    private List<FeedbackDTO> feedback;
    private CandidateStatus status;

    public CandidateDTO() {

    }

    public CandidateDTO(long id, String name, String phoneNumber, String email, String city, int experienceYears, String faculty, String recruitmentChannel, LocalDate birthDate, CandidateStatus status) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.city = city;
        this.experienceYears = experienceYears;
        this.faculty = faculty;
        this.recruitmentChannel = recruitmentChannel;
        this.birthDate = birthDate;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getRecruitmentChannel() {
        return recruitmentChannel;
    }

    public void setRecruitmentChannel(String recruitmentChannel) {
        this.recruitmentChannel = recruitmentChannel;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<InterestedPositionDTO> getInterestedPositionsDTO() {
        return interestedPositionsDTO;
    }

    public void setInterestedPositionsDTO(List<InterestedPositionDTO> interestedPositionsDTO) {
        this.interestedPositionsDTO = interestedPositionsDTO;
    }

    public List<FileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

    public List<String> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<String> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public List<FeedbackDTO> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<FeedbackDTO> feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CandidateDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .add("email='" + email + "'")
                .add("city='" + city + "'")
                .add("experienceYears=" + experienceYears)
                .add("faculty='" + faculty + "'")
                .add("recruitmentChannel='" + recruitmentChannel + "'")
                .add("birthDate=" + birthDate)
                .add("interestedPositionsDTO=" + interestedPositionsDTO)
                .add("files=" + files)
                .add("status=" + status)
                .toString();
    }
}
