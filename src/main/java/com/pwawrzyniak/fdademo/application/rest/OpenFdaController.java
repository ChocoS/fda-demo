package com.pwawrzyniak.fdademo.application.rest;

import com.pwawrzyniak.fdademo.application.OpenFdaDrugRecordApplicationOperations;
import com.pwawrzyniak.fdademo.application.OpenFdaDrugRecordApplicationServiceException;
import com.pwawrzyniak.fdademo.application.OpenFdaDrugRecordApplicationServiceNotFoundException;
import com.pwawrzyniak.fdademo.application.dto.ErrorResponse;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFda;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/open-fda")
class OpenFdaController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final OpenFdaDrugRecordApplicationOperations openFdaDrugRecordApplicationOperations;

  public OpenFdaController(OpenFdaDrugRecordApplicationOperations openFdaDrugRecordApplicationOperations) {
    this.openFdaDrugRecordApplicationOperations = openFdaDrugRecordApplicationOperations;
  }

  @Operation(summary = "Get Drug Fda")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Drug Fda found"),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Drug Fda not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @GetMapping(value = "/drug-fda", produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<DrugFda> getDrugFda(@RequestParam("manufacturerName") String manufacturerName,
                                  @RequestParam(value = "brandName", required = false) String brandName,
                                  @ParameterObject Pageable pageable) {
    logger.info("Searching OpenFDA for DrugFDA with manufacturerName '{}', brandName '{}' and '{}'", manufacturerName, brandName, pageable);
    Page<DrugFda> results = openFdaDrugRecordApplicationOperations.search(manufacturerName, brandName, pageable);
    logger.info("Found {} results", results.stream().count());
    return results;
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
    logger.info("MissingServletRequestParameterException occurred with message '{}'", exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(OpenFdaDrugRecordApplicationServiceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleOpenFdaDrugRecordApplicationServiceNotFoundException(OpenFdaDrugRecordApplicationServiceNotFoundException exception) {
    logger.info("OpenFdaDrugRecordApplicationServiceNotFoundException occurred with message '{}'", exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse("No results were found"), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(OpenFdaDrugRecordApplicationServiceException.class)
  public ResponseEntity<ErrorResponse> handleOpenFdaDrugRecordApplicationServiceException(OpenFdaDrugRecordApplicationServiceException exception) {
    logger.error("OpenFdaDrugRecordApplicationServiceException occurred with message '{}'", exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}