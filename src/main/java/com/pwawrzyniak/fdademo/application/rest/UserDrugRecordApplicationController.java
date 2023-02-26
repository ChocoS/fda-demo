package com.pwawrzyniak.fdademo.application.rest;

import com.pwawrzyniak.fdademo.application.UserDrugRecordApplicationOperations;
import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.CreateUserDrugRecordApplicationResponse;
import com.pwawrzyniak.fdademo.application.dto.ErrorResponse;
import com.pwawrzyniak.fdademo.application.dto.SearchUserDrugRecordApplicationRequest;
import com.pwawrzyniak.fdademo.application.dto.UserDrugRecordApplicationView;
import com.pwawrzyniak.fdademo.infrastructure.mongo.UserDrugRecordApplicationStorageServiceDuplicateKeyException;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/v1/user-dra")
class UserDrugRecordApplicationController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final UserDrugRecordApplicationOperations userDrugRecordApplicationOperations;

  public UserDrugRecordApplicationController(UserDrugRecordApplicationOperations userDrugRecordApplicationOperations) {
    this.userDrugRecordApplicationOperations = userDrugRecordApplicationOperations;
  }

  @Operation(summary = "Create user drug record application")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User drug record application created"),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "User drug record application already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public CreateUserDrugRecordApplicationResponse createUserDrugRecordApplication(@RequestBody @Valid CreateUserDrugRecordApplicationRequest createUserDrugRecordApplicationRequest) {
    logger.info("Creating user drug record application from request {}", createUserDrugRecordApplicationRequest);
    UserDrugRecordApplicationView userDrugRecordApplicationView = userDrugRecordApplicationOperations.createUserDrugRecordApplication(createUserDrugRecordApplicationRequest);
    logger.info("Created {}", userDrugRecordApplicationView);
    return new CreateUserDrugRecordApplicationResponse(userDrugRecordApplicationView.getApplicationNumber());
  }

  @Operation(summary = "Get user drug record application by application number")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User drug record application found", content = @Content(schema = @Schema(implementation = UserDrugRecordApplicationView.class))),
      @ApiResponse(responseCode = "404", description = "User drug record application not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @GetMapping(value = "/{applicationNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserDrugRecordApplication(@PathVariable("applicationNumber") String applicationNumber) {
    logger.info("Retrieving user drug record application by application number '{}'", applicationNumber);
    Optional<UserDrugRecordApplicationView> userDrugRecordApplicationViewOptional = userDrugRecordApplicationOperations.getUserDrugRecordApplication(applicationNumber);
    if (userDrugRecordApplicationViewOptional.isPresent()) {
      UserDrugRecordApplicationView userDrugRecordApplicationView = userDrugRecordApplicationViewOptional.get();
      logger.info("Found {}", userDrugRecordApplicationView);
      return ResponseEntity.ok(userDrugRecordApplicationView);
    }
    String notFoundMessage = "User drug record application was not found";
    logger.info(notFoundMessage);
    return new ResponseEntity<>(new ErrorResponse(notFoundMessage), HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Search for user drug record applications")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User drug record application search is successful")})
  @PostMapping(value = "/actions/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Page<UserDrugRecordApplicationView> searchUserDrugRecordApplication(@RequestBody SearchUserDrugRecordApplicationRequest searchUserDrugRecordApplicationRequest,
                                                                             @ParameterObject Pageable pageable) {
    logger.info("Searching user drug record application by {} and {}", searchUserDrugRecordApplicationRequest, pageable);
    Page<UserDrugRecordApplicationView> userDrugRecordApplicationViewPage = userDrugRecordApplicationOperations.searchUserDrugRecordApplication(searchUserDrugRecordApplicationRequest, pageable);
    logger.info("Found {} results", userDrugRecordApplicationViewPage.stream().count());
    return userDrugRecordApplicationViewPage;
  }

  @ExceptionHandler(UserDrugRecordApplicationStorageServiceDuplicateKeyException.class)
  public ResponseEntity<ErrorResponse> handleUserDrugRecordApplicationStorageServiceDuplicateKeyException(UserDrugRecordApplicationStorageServiceDuplicateKeyException exception) {
    logger.info("Exception occurred with message '{}'", exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse("User drug record application already exists"), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    logger.info("Exception occurred with message '{}'", exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse("Request is not valid"), HttpStatus.BAD_REQUEST);
  }
}