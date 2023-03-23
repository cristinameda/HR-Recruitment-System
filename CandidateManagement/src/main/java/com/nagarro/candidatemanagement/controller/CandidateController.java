package com.nagarro.candidatemanagement.controller;

import com.nagarro.candidatemanagement.annotation.NotEmptyFile;
import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.model.Candidate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RequestMapping(path = "/candidates")
@Validated
public interface CandidateController {
    /**
     * Stores a new candidate in the database.
     *
     * @param candidate the candidate to inserted
     * @return the stored candidate
     */
    @Operation(summary = "Create a new candidate", security = @SecurityRequirement(name = "bearerAuthentication"))
    @ApiResponse(responseCode = "201", description = "Created candidate",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Candidate.class))})
    @PostMapping
    ResponseEntity<CandidateDTO> save(@Valid @RequestPart CandidateDTO candidate, @NotEmptyFile @RequestPart(value = "CV") MultipartFile cv, @NotEmptyFile @RequestPart("GDPR") MultipartFile gdpr);

    /**
     * Deletes from the database a candidate by ID.
     *
     * @param id the candidate's id
     */
    @Operation(summary = "Delete a candidate by ID", security = @SecurityRequirement(name = "bearerAuthentication"))
    @ApiResponse(responseCode = "204", description = "Deleted candidate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@Positive @PathVariable long id);

    /**
     * Retrieve candidates.
     *
     * @param pageNo      page number
     * @param pageSize    page size
     * @param field       field to filter by
     * @param filterValue value to be matched by field
     * @return a list with all database candidates
     */
    @Operation(summary = "Get candidates", security = @SecurityRequirement(name = "bearerAuthentication"))
    @ApiResponse(responseCode = "200", description = "Candidates received!",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CandidateDTO.class))})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<CandidateDTO>> findAll(@RequestParam(required = false, defaultValue = "1") int pageNo, @RequestParam(required = false, defaultValue = "6") int pageSize,
                                               @RequestParam(required = false) String field, @RequestParam(required = false) String filterValue);

    /**
     * Retrieves a candidate by ID
     *
     * @param candidateId id of requested candidate
     * @return requested candidate
     */
    @Operation(summary = "Get a candidate by id", security = @SecurityRequirement(name = "bearerAuthentication"))
    @ApiResponse(responseCode = "200", description = "Candidate was found!",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CandidateDTO.class))})
    @GetMapping("/{candidateId}")
    ResponseEntity<CandidateDTO> findById(@Positive @PathVariable long candidateId);

    /**
     * Assigns users to a candidate.
     *
     * @param id                          - the candidate's id
     * @param assignedUsersToCandidateDTO - the users to be assigned to the candidate
     */
    @Operation(summary = "Assign users to a candidate", security = @SecurityRequirement(name = "bearerAuthentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assigned users to candidate!"),
            @ApiResponse(responseCode = "400", description = "Users are not valid/ not of required role!"),
            @ApiResponse(responseCode = "404", description = "User not found!")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    ResponseEntity<Void> assignUsersToCandidate(@Positive @PathVariable long id,
                                                @Valid @RequestBody List<UserEmailRoleDTO> assignedUsersToCandidateDTO);

    /**
     * Assigns  status to a candidate
     *
     * @param updateCandidateStatusDTO - the email and status
     */
    @Operation(summary = "Users can assign a status to a candidate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assigned status to candidate!"),
            @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/status")
    ResponseEntity<Void> updateCandidateStatus(@Valid @RequestBody UpdateCandidateStatusDTO updateCandidateStatusDTO);

}
