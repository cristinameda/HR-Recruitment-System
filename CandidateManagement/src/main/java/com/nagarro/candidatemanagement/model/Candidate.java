package com.nagarro.candidatemanagement.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Candidate {
    private long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String city;
    private int experienceYears;
    private String faculty;
    private String recruitmentChannel;
    private LocalDate birthDate;
    private List<InterestedPosition> interestedPositions;
    private List<File> files;
    private List<String> assignedUsers;
    private CandidateStatus status;
    private List<Feedback> feedback;

    public Candidate() {

    }

    public Candidate(String name, String phoneNumber, String email, String city, int experienceYears, String faculty, String recruitmentChannel, LocalDate birthDate, CandidateStatus status) {
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

    public List<InterestedPosition> getInterestedPositions() {
        return interestedPositions;
    }

    public void setInterestedPositions(List<InterestedPosition> interestedPositions) {
        this.interestedPositions = interestedPositions;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
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

    public List<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<Feedback> feedback) {
        this.feedback = feedback;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Candidate.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .add("email='" + email + "'")
                .add("city='" + city + "'")
                .add("experienceYears=" + experienceYears)
                .add("faculty='" + faculty + "'")
                .add("recruitmentChannel='" + recruitmentChannel + "'")
                .add("birthDate=" + birthDate)
                .add("interestedPositions=" + interestedPositions)
                .add("files=" + files)
                .add("status=" + status)
                .toString();
    }
}
