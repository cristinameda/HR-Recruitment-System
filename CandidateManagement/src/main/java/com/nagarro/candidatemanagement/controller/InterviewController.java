package com.nagarro.candidatemanagement.controller;

import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface InterviewController {

    /**
     * Schedules a new interview
     *
     * @param interviewDTO the interview
     * @return the interview
     */
    @Operation(summary = "Schedules a new interview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "A new interview was successfully scheduled!"),
            @ApiResponse(responseCode = "400", description = "Bad request!"),
            @ApiResponse(content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = InterviewDTO.class))})
    })
    @PostMapping("/interview")
    ResponseEntity<InterviewDTO> scheduleInterview(@Valid @RequestBody InterviewDTO interviewDTO);
}
