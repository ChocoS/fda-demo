package com.pwawrzyniak.fdademo.application;

import com.pwawrzyniak.fdademo.UnitTest;
import com.pwawrzyniak.fdademo.infrastructure.openfda.DrugFdaRequestQueryMap;
import com.pwawrzyniak.fdademo.infrastructure.openfda.OpenFdaFeignClient;
import com.pwawrzyniak.fdademo.infrastructure.openfda.OpenFdaFeignClientException;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFda;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFdaResponse;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.Meta;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.MetaResults;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenFdaDrugRecordApplicationServiceUnitTest extends UnitTest {

  private static final String GIVEN_MANUFACTURER_NAME = "test manufacturer name";

  private static final String GIVEN_BRAND_NAME = "test brand name";

  private static final Pageable GIVEN_PAGE_REQUEST = PageRequest.of(3, 2);

  @Mock
  private OpenFdaFeignClient openFdaFeignClient;

  @InjectMocks
  private OpenFdaDrugRecordApplicationService openFdaDrugRecordApplicationService;

  @Test
  void shouldFindDrugFdaPage() throws OpenFdaFeignClientException {
    // given
    when(openFdaFeignClient.search(any()))
        .thenReturn(givenDrugFdaResponse());

    // when
    Page<DrugFda> result = openFdaDrugRecordApplicationService.search(GIVEN_MANUFACTURER_NAME, GIVEN_BRAND_NAME, GIVEN_PAGE_REQUEST);

    // then
    assertDrugFdaPage(result);
    verifyOpenFdaFeignClientMock();
  }

  @Test
  void shouldThrowNotFoundExceptionWhenClientCallFailsWith404() throws OpenFdaFeignClientException {
    // given
    when(openFdaFeignClient.search(any()))
        .thenThrow(new OpenFdaFeignClientException("Not found", HttpStatus.NOT_FOUND));

    // when - then
    assertThatThrownBy(() -> openFdaDrugRecordApplicationService.search(GIVEN_MANUFACTURER_NAME, GIVEN_BRAND_NAME, GIVEN_PAGE_REQUEST))
        .isExactlyInstanceOf(OpenFdaDrugRecordApplicationServiceNotFoundException.class)
        .hasMessage("Open FDA feign client call failed with error message: 'Not found'");
    verifyOpenFdaFeignClientMock();
  }

  @Test
  void shouldThrowExceptionWhenClientCallFails() throws OpenFdaFeignClientException {
    // given
    when(openFdaFeignClient.search(any()))
        .thenThrow(new OpenFdaFeignClientException("General exception", HttpStatus.INTERNAL_SERVER_ERROR));

    // when - then
    assertThatThrownBy(() -> openFdaDrugRecordApplicationService.search(GIVEN_MANUFACTURER_NAME, GIVEN_BRAND_NAME, GIVEN_PAGE_REQUEST))
        .isExactlyInstanceOf(OpenFdaDrugRecordApplicationServiceException.class)
        .hasMessage("Open FDA feign client call failed with error message: 'General exception'");
    verifyOpenFdaFeignClientMock();
  }

  @Test
  void shouldThrowExceptionWhenClientCallResponseIsMissingTotalNumberOfRecords() throws OpenFdaFeignClientException {
    // given
    when(openFdaFeignClient.search(any()))
        .thenReturn(givenDrugFdaResponseWithoutTotalNumberOfRecords());

    // when - then
    assertThatThrownBy(() -> openFdaDrugRecordApplicationService.search(GIVEN_MANUFACTURER_NAME, GIVEN_BRAND_NAME, GIVEN_PAGE_REQUEST))
        .isExactlyInstanceOf(OpenFdaDrugRecordApplicationServiceException.class)
        .hasMessage("Missing total number of found records");
    verifyOpenFdaFeignClientMock();
  }

  private void verifyOpenFdaFeignClientMock() throws OpenFdaFeignClientException {
    ArgumentCaptor<DrugFdaRequestQueryMap> drugFdaRequestQueryMapArgumentCaptor = ArgumentCaptor.forClass(DrugFdaRequestQueryMap.class);
    verify(openFdaFeignClient).search(drugFdaRequestQueryMapArgumentCaptor.capture());
    DrugFdaRequestQueryMap drugFdaRequestQueryMap = drugFdaRequestQueryMapArgumentCaptor.getValue();
    assertThat(drugFdaRequestQueryMap).isNotNull();
    assertThat(drugFdaRequestQueryMap.getSearch()).isEqualTo("openfda.brand_name:\"test brand name\" AND openfda.manufacturer_name:\"test manufacturer name\"");
    assertThat(drugFdaRequestQueryMap.getLimit()).isEqualTo(2);
    assertThat(drugFdaRequestQueryMap.getSkip()).isEqualTo(6);
  }

  private DrugFdaResponse givenDrugFdaResponse() {
    return new DrugFdaResponse(List.of(new DrugFda("applicationNumber1", null, null),
        new DrugFda("applicationNumber2", null, null)),
        new Meta(new MetaResults(8L)));
  }

  private DrugFdaResponse givenDrugFdaResponseWithoutTotalNumberOfRecords() {
    return new DrugFdaResponse(List.of(new DrugFda("applicationNumber1", null, null),
        new DrugFda("applicationNumber2", null, null)), null);
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
}