package com.nagarro.candidatemanagement.service.impl;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.InterestedPositionDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.exception.AssignUsersException;
import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.gateway.UsersValidator;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import com.nagarro.candidatemanagement.model.File;
import com.nagarro.candidatemanagement.model.FileType;
import com.nagarro.candidatemanagement.model.InterestedPosition;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import com.nagarro.candidatemanagement.repository.exception.RepositoryException;
import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UsersValidator usersValidator;
    @Mock
    private BearerTokenWrapper bearerTokenWrapper;
    @Mock
    private TokenManager tokenManager;
    @InjectMocks
    private CandidateServiceImpl candidateService;
    @Captor
    private ArgumentCaptor<Candidate> candidateArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    public void testAddCandidate() {
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        File cvFile = new File("test", FileType.CV, "Hello".getBytes());
        File gdprFile = new File("test", FileType.GDPR, "Hello".getBytes());
        candidate.setFiles(List.of(cvFile, gdprFile));
        MockMultipartFile cv = new MockMultipartFile("Test", "Hello".getBytes());
        MockMultipartFile gdpr = new MockMultipartFile("Test", "Hello".getBytes());
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        when(feedbackRepository.findByCandidateId(candidate.getId())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(candidateDTO);
        when(modelMapper.map(candidateDTO, Candidate.class)).thenReturn(candidate);
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        CandidateDTO returnedCandidateDTO = candidateService.save(candidateDTO, cv, gdpr);

        verify(candidateRepository).save(candidateArgumentCaptor.capture());
        assertNotNull(returnedCandidateDTO);
        assertThat(candidateArgumentCaptor.getValue().getId()).isGreaterThanOrEqualTo(0L);
        assertThat(candidateArgumentCaptor.getValue().getEmail()).isEqualTo(candidateDTO.getEmail());
        assertThat(candidateArgumentCaptor.getValue().getFiles().get(0)).isEqualTo(cvFile);
        assertThat(candidateArgumentCaptor.getValue().getFiles().get(1)).isEqualTo(gdprFile);

    }

    @Test
    public void testFindAll_whenHrRepresentativeAndNoFilter_shouldReturnAllCandidates() {
        Candidate candidate1 = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        Candidate candidate2 = TestDataBuilder.buildCandidate("Candidate2", "candidate2@yahoo.com");
        CandidateDTO candidateDTO1 = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO2 = TestDataBuilder.buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com");

        when(candidateRepository.findAll(anyInt(), anyInt())).thenReturn(List.of(candidate1, candidate2));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate1, CandidateDTO.class)).thenReturn(candidateDTO1);
        when(modelMapper.map(candidate2, CandidateDTO.class)).thenReturn(candidateDTO2);
        when(bearerTokenWrapper.getToken()).thenReturn("HrRepresentativeToken");
        when(tokenManager.getUserDetailsFromToken("HrRepresentativeToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "HrRepresentative"));

        List<CandidateDTO> candidateDTOList = candidateService.findAll(1, 2, "", "");

        verify(candidateRepository).findAll(integerArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertTrue(integerArgumentCaptor.getAllValues().containsAll(List.of(1, 2)));
        assertEquals(2, candidateDTOList.size());
        assertTrue(candidateDTOList.containsAll(List.of(candidateDTO1, candidateDTO2)));

        List<InterestedPositionDTO> expectedPositions = candidateDTOList.get(1).getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = candidate1.getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    public void testFindAll_whenPTEAndNoFilter_shouldReturnAllCandidates() {
        Candidate candidate1 = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        Candidate candidate2 = TestDataBuilder.buildCandidate("Candidate2", "candidate2@yahoo.com");
        CandidateDTO candidateDTO1 = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO2 = TestDataBuilder.buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com");

        when(candidateRepository.findAll(anyInt(), anyInt())).thenReturn(List.of(candidate1, candidate2));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate1, CandidateDTO.class)).thenReturn(candidateDTO1);
        when(modelMapper.map(candidate2, CandidateDTO.class)).thenReturn(candidateDTO2);
        when(bearerTokenWrapper.getToken()).thenReturn("PTEToken");
        when(tokenManager.getUserDetailsFromToken("PTEToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "PTE"));

        List<CandidateDTO> candidateDTOList = candidateService.findAll(1, 2, "", "");

        verify(candidateRepository).findAll(integerArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertTrue(integerArgumentCaptor.getAllValues().containsAll(List.of(1, 2)));
        assertEquals(2, candidateDTOList.size());
        assertTrue(candidateDTOList.containsAll(List.of(candidateDTO1, candidateDTO2)));

        List<InterestedPositionDTO> expectedPositions = candidateDTOList.get(1).getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = candidate1.getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    public void testFindAll_whenPTEAndFilter_shouldReturnFilteredCandidates() {
        Candidate candidate1 = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        Candidate candidate2 = TestDataBuilder.buildCandidate("Candidate2", "candidate2@yahoo.com");
        CandidateDTO candidateDTO1 = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO2 = TestDataBuilder.buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com");

        when(candidateRepository.findAllByField(1, 2, "email", "ate")).thenReturn(List.of(candidate1, candidate2));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate1, CandidateDTO.class)).thenReturn(candidateDTO1);
        when(modelMapper.map(candidate2, CandidateDTO.class)).thenReturn(candidateDTO2);
        when(bearerTokenWrapper.getToken()).thenReturn("PTEToken");
        when(tokenManager.getUserDetailsFromToken("PTEToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "PTE"));

        List<CandidateDTO> candidateDTOList = candidateService.findAll(1, 2, "email", "ate");

        verify(candidateRepository).findAllByField(integerArgumentCaptor.capture(), integerArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertTrue(integerArgumentCaptor.getAllValues().containsAll(List.of(1, 2)));
        assertTrue(stringArgumentCaptor.getAllValues().containsAll(List.of("email", "ate")));
        assertEquals(2, candidateDTOList.size());
        assertTrue(candidateDTOList.containsAll(List.of(candidateDTO1, candidateDTO2)));

        List<InterestedPositionDTO> expectedPositions = candidateDTOList.get(1).getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = candidate1.getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    public void findAll_whenTechInterviewer_shouldReturnAssignedCandidates() {
        Candidate candidate1 = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        Candidate candidate2 = TestDataBuilder.buildCandidate("Candidate2", "candidate2@yahoo.com");
        CandidateDTO candidateDTO1 = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO2 = TestDataBuilder.buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com");

        when(candidateRepository.findAllByAssignedUser(1, 2, "email")).thenReturn(List.of(candidate1, candidate2));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate1, CandidateDTO.class)).thenReturn(candidateDTO1);
        when(modelMapper.map(candidate2, CandidateDTO.class)).thenReturn(candidateDTO2);
        when(bearerTokenWrapper.getToken()).thenReturn("TechInterviewerToken");
        when(tokenManager.getUserDetailsFromToken("TechInterviewerToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "TechnicalInterviewer"));

        List<CandidateDTO> candidateDTOList = candidateService.findAll(1, 2, "", "");

        verify(candidateRepository).findAllByAssignedUser(integerArgumentCaptor.capture(), integerArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertTrue(integerArgumentCaptor.getAllValues().containsAll(List.of(1, 2)));
        assertEquals("email", stringArgumentCaptor.getValue());
        assertEquals(2, candidateDTOList.size());
        assertTrue(candidateDTOList.containsAll(List.of(candidateDTO1, candidateDTO2)));

        List<InterestedPositionDTO> expectedPositions = candidateDTOList.get(1).getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = candidate1.getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    public void findAll_whenHrRepresentative_shouldReturnEmptyList() {
        when(candidateRepository.findAll(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        when(bearerTokenWrapper.getToken()).thenReturn("HrRepresentativeToken");
        when(tokenManager.getUserDetailsFromToken("HrRepresentativeToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "HrRepresentative"));

        List<CandidateDTO> candidateDTOList = candidateService.findAll(1, 2, "", "");

        verify(candidateRepository).findAll(integerArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertTrue(integerArgumentCaptor.getAllValues().containsAll(List.of(1, 2)));
        assertEquals(0, candidateDTOList.size());
    }

    @Test
    public void findAll_whenInvalidRole_shouldThrowHeaderAuthorizationException() {
        when(bearerTokenWrapper.getToken()).thenReturn("someToken");
        when(tokenManager.getUserDetailsFromToken("someToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "Admin or something else"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> candidateService.findAll(1, 2, "", ""));
        assertEquals("Role 'Admin or something else' is unauthorized!", exception.getMessage());
    }

    @Test
    public void findAll_whenInvalidField_shouldThrowIllegalArgumentException() {
        when(candidateRepository.findAllByField(1, 2, "invalid_field", "ate")).thenThrow(RepositoryException.class);
        when(bearerTokenWrapper.getToken()).thenReturn("PTEToken");
        when(tokenManager.getUserDetailsFromToken("PTEToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "PTE"));

        assertThrows(IllegalArgumentException.class,
                () -> candidateService.findAll(1, 2, "invalid_field", "ate"));
    }

    @Test
    public void findAll_whenValidFieldAndNullFilterValue_shouldThrowIllegalArgumentException() {
        when(bearerTokenWrapper.getToken()).thenReturn("PTEToken");
        when(tokenManager.getUserDetailsFromToken("PTEToken"))
                .thenReturn(TestDataBuilder.buildUserDetails("email", "PTE"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> candidateService.findAll(1, 2, "email", null));
        assertEquals("Filter value cannot be null when filtering!", exception.getMessage());
    }

    @Test
    public void testFindById_shouldReturnRequestedCandidate() {
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        CandidateDTO expectedCandidate = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(expectedCandidate);

        CandidateDTO actualCandidate = candidateService.findById(1L);

        verify(candidateRepository).findById(longArgumentCaptor.capture());
        assertEquals(1L, longArgumentCaptor.getValue());
        assertEquals(expectedCandidate, actualCandidate);
        assertEquals(expectedCandidate.getInterestedPositionsDTO().size(), actualCandidate.getInterestedPositionsDTO().size());
    }

    @Test
    public void testFindById_shouldThrowNotFoundException() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CandidateNotFoundException.class, () -> candidateService.findById(1L));
    }

    @Test
    public void testFindByEmail_shouldReturnRequestedCandidate() {
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        CandidateDTO expectedCandidate = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        when(candidateRepository.findByEmail("candidate1@yahoo.com")).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(expectedCandidate);

        CandidateDTO actualCandidate = candidateService.findByEmail("candidate1@yahoo.com");

        verify(candidateRepository).findByEmail(stringArgumentCaptor.capture());
        assertEquals("candidate1@yahoo.com", stringArgumentCaptor.getValue());
        assertEquals(expectedCandidate, actualCandidate);
        assertEquals(expectedCandidate.getInterestedPositionsDTO().size(), actualCandidate.getInterestedPositionsDTO().size());
    }

    @Test
    public void testFindByEmail_shouldThrowNotFoundException() {
        when(candidateRepository.findByEmail("candidate1@yahoo.com")).thenReturn(Optional.empty());

        assertThrows(CandidateNotFoundException.class, () -> candidateService.findByEmail("candidate1@yahoo.com"));
    }

    @Test
    public void testDeleteCandidate() {
        long candidateId = 1L;
        when(candidateRepository.delete(candidateId)).thenReturn(true);

        candidateService.delete(candidateId);

        verify(candidateRepository).delete(candidateId);
    }

    @Test
    public void testDeleteCandidate_shouldThrowNotFoundException() {
        long candidateId = 224L;
        when(candidateRepository.delete(candidateId)).thenReturn(false);

        assertThrows(CandidateNotFoundException.class, () -> candidateService.delete(224L));
    }

    @Test
    public void testAssignUsersToCandidate() {
        List<UserEmailRoleDTO> users = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");

        doNothing().when(candidateRepository).updateCandidateAssignedUsers(candidateArgumentCaptor.capture());
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(candidateDTO);
        when(modelMapper.map(candidateDTO, Candidate.class)).thenReturn(candidate);
        when(usersValidator.areUsersValid(users)).thenReturn(true);
        when(usersValidator.areRequestedRolesValid(users)).thenReturn(true);

        candidateService.assignUsersToCandidate(1L, users);

        verify(candidateRepository).findById(1L);
        verify(modelMapper).map(candidate, CandidateDTO.class);
        verify(usersValidator).areUsersValid(users);
        verify(usersValidator).areRequestedRolesValid(users);
        verify(candidateRepository).updateCandidateAssignedUsers(candidate);

        assertEquals(candidate, candidateArgumentCaptor.getValue());
    }

    @Test
    public void testAssignUsersToCandidate_whenNotRequestedRoles_shouldThrowAssignUsersException() {
        List<UserEmailRoleDTO> users = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(candidateDTO);
        when(usersValidator.areRequestedRolesValid(users)).thenReturn(false);

        AssignUsersException exception = assertThrows(AssignUsersException.class, () -> candidateService.assignUsersToCandidate(1L, users));
        assertEquals("Invalid number of requested roles!", exception.getMessage());
        verify(candidateRepository).findById(1L);
        verify(modelMapper).map(candidate, CandidateDTO.class);
        verify(usersValidator).areRequestedRolesValid(users);
    }

    @Test
    public void testAssignUsersToCandidate_whenUsersNotValid_shouldThrowAssignUsersException() {
        List<UserEmailRoleDTO> users = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(candidateDTO);
        when(usersValidator.areRequestedRolesValid(users)).thenReturn(true);
        when(usersValidator.areUsersValid(users)).thenReturn(false);

        AssignUsersException exception = assertThrows(AssignUsersException.class, () -> candidateService.assignUsersToCandidate(1L, users));
        assertEquals("Users are not valid!", exception.getMessage());
        verify(candidateRepository).findById(1L);
        verify(modelMapper).map(candidate, CandidateDTO.class);
        verify(usersValidator).areRequestedRolesValid(users);
        verify(usersValidator).areUsersValid(users);
    }

    @Test
    void testAssignStatusToCandidate_shouldUpdateStatus() {
        UpdateCandidateStatusDTO updateCandidateStatusDTO = TestDataBuilder.generateRequiredCandidateAndStatus("HIRED");
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(1L, "Candidate1", "candidate1@yahoo.com");
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");

        when(modelMapper.map(candidateDTO, Candidate.class)).thenReturn(candidate);
        when(feedbackRepository.findByCandidateId(anyLong())).thenReturn(new ArrayList<>());
        when(modelMapper.map(candidate, CandidateDTO.class)).thenReturn(candidateDTO);
        when(candidateRepository.findByEmail("candidate1@yahoo.com")).thenReturn(Optional.of(candidate));


        candidateService.updateCandidateStatus(updateCandidateStatusDTO);

        assertEquals(CandidateStatus.HIRED, candidate.getStatus());

        verify(candidateRepository).findByEmail(updateCandidateStatusDTO.getEmail());
        verify(modelMapper).map(candidateDTO, Candidate.class);
        verify(modelMapper).map(candidate, CandidateDTO.class);
    }
}