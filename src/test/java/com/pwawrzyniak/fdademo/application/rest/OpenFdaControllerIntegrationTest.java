package com.pwawrzyniak.fdademo.application.rest;

import com.pwawrzyniak.fdademo.IntegrationTest;
import com.pwawrzyniak.fdademo.application.OpenFdaDrugRecordApplicationOperations;
import com.pwawrzyniak.fdademo.application.OpenFdaDrugRecordApplicationServiceException;
import com.pwawrzyniak.fdademo.application.OpenFdaDrugRecordApplicationServiceNotFoundException;
import com.pwawrzyniak.fdademo.application.dto.ErrorResponse;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@Import(JacksonConfiguration.class)
class OpenFdaControllerIntegrationTest extends IntegrationTest {

  private static final String MANUFACTURER_NAME = "test manufacturer name";

  private static final String BRAND_NAME = "test brand name";

  @MockBean
  private OpenFdaDrugRecordApplicationOperations openFdaDrugRecordApplicationOperations;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void shouldReturnDrugFdaPage() {
    // given
    mockOpenFdaDrugRecordApplicationOperationsSearch();

    // when
    ResponseEntity<Page<DrugFda>> response = callToGetDrugFdaPage();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertDrugFdaPage(response.getBody());
    verifyOpenFdaDrugRecordApplicationOperationsSearchMock();
  }

  @Test
  void shouldReturn400WhenManufacturerNameIsMissing() {
    // given
    mockOpenFdaDrugRecordApplicationOperationsSearchNotFoundException();

    // when
    ResponseEntity<ErrorResponse> response = callToGetDrugFdaPageWithoutManufacturerNameAndExpectError();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new ErrorResponse("Required request parameter 'manufacturerName' for method parameter type String is not present"));
    verifyNoInteractionsWithOpenFdaDrugRecordApplicationOperationsMock();
  }

  @Test
  void shouldReturn404WhenSearchThrowsNotFoundException() {
    // given
    mockOpenFdaDrugRecordApplicationOperationsSearchNotFoundException();

    // when
    ResponseEntity<ErrorResponse> response = callToGetDrugFdaPageAndExpectError();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new ErrorResponse("No results were found"));
    verifyOpenFdaDrugRecordApplicationOperationsSearchMock();
  }

  @Test
  void shouldReturn404WhenSearchThrowsException() {
    // given
    mockOpenFdaDrugRecordApplicationOperationsSearchException();

    // when
    ResponseEntity<ErrorResponse> response = callToGetDrugFdaPageAndExpectError();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .isEqualTo(new ErrorResponse("General error"));
    verifyOpenFdaDrugRecordApplicationOperationsSearchMock();
  }

  private void assertDrugFdaPage(Page<DrugFda> drugFdaPage) {
    assertThat(drugFdaPage).isNotNull();
    assertThat(drugFdaPage.getTotalElements()).isEqualTo(8);
    assertThat(drugFdaPage.getPageable())
        .usingRecursiveComparison()
        .isEqualTo(PageRequest.of(3, 2));
    assertDrugFdaPageContent(drugFdaPage.getContent());
  }

  private void assertDrugFdaPageContent(List<DrugFda> drugFdaList) {
    assertThat(drugFdaList)
        .usingRecursiveComparison()
        .isEqualTo(List.of(new DrugFda("applicationNumber1", null, null),
            new DrugFda("applicationNumber2", null, null)));
  }

  private ResponseEntity<Page<DrugFda>> callToGetDrugFdaPage() {
    return testRestTemplate.exchange("/v1/open-fda/drug-fda?manufacturerName={manufacturerName}&brandName={brandName}&page={page}&size={size}",
        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        }, MANUFACTURER_NAME, BRAND_NAME, 3, 2);
  }

  private ResponseEntity<ErrorResponse> callToGetDrugFdaPageAndExpectError() {
    return testRestTemplate.exchange("/v1/open-fda/drug-fda?manufacturerName={manufacturerName}&brandName={brandName}&page={page}&size={size}",
        HttpMethod.GET, null, ErrorResponse.class, MANUFACTURER_NAME, BRAND_NAME, 3, 2);
  }

  private ResponseEntity<ErrorResponse> callToGetDrugFdaPageWithoutManufacturerNameAndExpectError() {
    return testRestTemplate.exchange("/v1/open-fda/drug-fda?brandName={brandName}&page={page}&size={size}",
        HttpMethod.GET, null, ErrorResponse.class, BRAND_NAME, 3, 2);
  }

  private void mockOpenFdaDrugRecordApplicationOperationsSearch() {
    when(openFdaDrugRecordApplicationOperations.search(anyString(), anyString(), any()))
        .thenReturn(givenDrugFdaPage());
  }

  private void mockOpenFdaDrugRecordApplicationOperationsSearchNotFoundException() {
    when(openFdaDrugRecordApplicationOperations.search(anyString(), anyString(), any()))
        .thenThrow(new OpenFdaDrugRecordApplicationServiceNotFoundException("Not found"));
  }

  private void mockOpenFdaDrugRecordApplicationOperationsSearchException() {
    when(openFdaDrugRecordApplicationOperations.search(anyString(), anyString(), any()))
        .thenThrow(new OpenFdaDrugRecordApplicationServiceException("General error"));
  }

  private Page<DrugFda> givenDrugFdaPage() {
    return new PageImpl<>(List.of(new DrugFda("applicationNumber1", null, null),
        new DrugFda("applicationNumber2", null, null)),
        PageRequest.of(3, 2), 8);
  }

  private void verifyOpenFdaDrugRecordApplicationOperationsSearchMock() {
    verify(openFdaDrugRecordApplicationOperations).search(MANUFACTURER_NAME, BRAND_NAME, PageRequest.of(3, 2));
  }

  private void verifyNoInteractionsWithOpenFdaDrugRecordApplicationOperationsMock() {
    verifyNoInteractions(openFdaDrugRecordApplicationOperations);
  }
}