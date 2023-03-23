package com.nagarro.candidatemanagement.controller;

import com.nagarro.candidatemanagement.controller.dto.FeedbackDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RequestMapping(path = "/feedback")
@Validated
public interface FeedbackController {

    /**
     * Gives feedback to a candidate
     *
     * @param candidateId - the id of the candidate
     * @param feedbackDTO - the feedback
     */
    @Operation(summary = "Give feedback to a candidate", security = @SecurityRequirement(name = "bearerAuthentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assigned feedback to candidate!"),
            @ApiResponse(responseCode = "409", description = "User already gave feedback to this candidate!"),
            @ApiResponse(responseCode = "404", description = "User not found!"),
            @ApiResponse(responseCode = "400", description = "User not assigned to candidate!")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{candidateId}")
    ResponseEntity<FeedbackDTO> giveFeedbackToCandidate(@Positive @PathVariable long candidateId,
                                                        @Valid @RequestBody FeedbackDTO feedbackDTO);

}
