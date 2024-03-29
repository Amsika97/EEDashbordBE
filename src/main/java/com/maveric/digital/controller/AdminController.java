package com.maveric.digital.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.maveric.digital.responsedto.AccountDto;
import com.maveric.digital.service.AdminService;
import com.maveric.digital.service.ConversationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

	private final ConversationService conversationService;
	private final AdminService adminService;

	@Operation(summary = "Get Accounts by BusinessUnitId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AccountDto.class))),
			@ApiResponse(responseCode = "204", description = "No accounts found for the given BusinessUnitId") })
	@PostMapping(path = "/admin/import/metadata")
	void importMetaData(@RequestParam("file") MultipartFile multipartFile) throws IOException {

		this.adminService.importCsvMetaData(
				this.conversationService.parseToCsvMetaDataDto(new InputStreamReader(multipartFile.getInputStream())));

	}

}
