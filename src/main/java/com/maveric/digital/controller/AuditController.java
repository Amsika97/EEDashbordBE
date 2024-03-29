package com.maveric.digital.controller;

import com.maveric.digital.model.Audit;
import com.maveric.digital.responsedto.AuditDto;
import com.maveric.digital.service.AuditService;
import com.maveric.digital.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/v1"})
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;
    private final ConversationService conversationService;

    @Operation(
            summary = "Get Audits by operation context"
    )
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "Return Audits for operation context"
    ), @ApiResponse(
            responseCode = "200",
            description = "Audits not found"
    )})
    @GetMapping("/audit/{fromDate}/{toDate}/{operationContext}")
    public ResponseEntity< List<AuditDto>> getAuditsForOperation(@PathVariable Long fromDate,
                                                          @PathVariable Long toDate,@PathVariable String operationContext){
        log.debug("AuditController: getAuditsForOperation -started ");
        log.debug("Request Data for Getting Audits For Operation - fromDate :{},toDate :{} ,operationContext :{}",fromDate,toDate,operationContext);
        List<AuditDto> auditDtoList= auditService.getAuditsForOperation(fromDate,toDate,operationContext);
        log.debug("AuditController: getAuditsForOperation -ended ");
        return ResponseEntity.ok().body(auditDtoList);
    }
    @Operation(
        summary = "Get Audits by updatedById"
    )
    @ApiResponses(value = {@ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = AuditDto.class))
    )})
    @GetMapping("/audit/updatedById/{updatedById}")
    public ResponseEntity<List<AuditDto>> getAuditsByUpdatedBy(@PathVariable String updatedById) {
        log.debug("AuditController: getAuditsByUpdatedBy() - stared ");
        log.debug("AuditController::getAuditsByUpdatedBy()::Retrieving audits by updatedById Id {}",
            updatedById);
        List<AuditDto> auditDtoList= auditService.getAuditsByUpdatedById(updatedById);
        log.debug("AuditController: getAuditsByUpdatedBy() - ended ");
        return ResponseEntity.ok().body(auditDtoList);

        }
    @Operation(
        summary = "Saves the Audit"
    )
    @ApiResponses(value = {@ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = Audit.class))
    )})
    @PostMapping("/audit/save")
    public ResponseEntity<Audit> logActivity(@RequestBody Audit audit) {
        log.debug("AuditController::logActivity() - started");
        Audit response=auditService.logActivity(audit);
        log.debug("AuditController::logActivity() - ended");
        return ResponseEntity.ok(response);
    }
}
