package com.pwawrzyniak.fdademo.infrastructure.openfda;

import com.pwawrzyniak.fdademo.FdaDemoApplication;
import com.pwawrzyniak.fdademo.IntegrationTest;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFda;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.DrugFdaResponse;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.Meta;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.MetaResults;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.OpenFda;
import com.pwawrzyniak.fdademo.infrastructure.openfda.dto.Product;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.resourceContent;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.endsWithUri;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.parameter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContextConfiguration(classes = {FdaDemoApplication.class, StubServerConfiguration.class},
    initializers = StubServerConfiguration.StubServerPropertySourceInitializer.class)
class OpenFdaFeignClientIntegrationTest extends IntegrationTest {

  @Autowired
  private StubServer stubServer;

  @Autowired
  private OpenFdaFeignClient openFdaFeignClient;

  @AfterEach
  void stubServerCleanup() {
    stubServer.clear();
  }

  @Test
  void shouldCallOpenFda() throws OpenFdaFeignClientException {
    // given
    stubOpenFdaResponse();

    // when
    DrugFdaResponse response = openFdaFeignClient.search(givenDrugFdaRequestQueryMap());

    // then
    assertThat(response).isNotNull();
    assertThat(response.getMeta().flatMap(Meta::getResults).flatMap(MetaResults::getTotal)).hasValue(6L);
    assertDrugFdaList(response.getResults());
    verifyOpenFdaDrugsFdaCall();
  }

  @Test
  void shouldCallOpenFdaAndThrowExceptionInCaseOfResultsNotFound() {
    // given
    stubOpenFdaResponseNotFound();

    // when - then
    assertThatThrownBy(() -> openFdaFeignClient.search(givenDrugFdaRequestQueryMap()))
        .isExactlyInstanceOf(OpenFdaFeignClientException.class)
        .hasMessage("Request to Open FDA failed with status 404")
        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
    verifyOpenFdaDrugsFdaCall();
  }

  @Test
  void shouldCallOpenFdaAndThrowExceptionInCaseOfUnknownError() {
    // given
    stubOpenFdaResponseUnknownError();

    // when - then
    assertThatThrownBy(() -> openFdaFeignClient.search(givenDrugFdaRequestQueryMap()))
        .isExactlyInstanceOf(OpenFdaFeignClientException.class)
        .hasMessage("Request to Open FDA failed with status 500")
        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);
    verifyOpenFdaDrugsFdaCall();
  }

  private void assertDrugFdaList(List<DrugFda> results) {
    assertThat(results)
        .usingRecursiveComparison()
        .isEqualTo(List.of(
            new DrugFda("NDA022065",
                new OpenFda(List.of("R-Pharm US Operating, LLC"),
                    Collections.emptyList(),
                    List.of("IXEMPRA")),
                List.of(new Product("002"), new Product("001"))),
            new DrugFda("ANDA216131",
                new OpenFda(List.of("Hainan Poly Pharm. Co., Ltd."),
                    List.of("DOBUTAMINE HYDROCHLORIDE"),
                    List.of("DOBUTAMINE")),
                List.of(new Product("001")))));
  }

  private DrugFdaRequestQueryMap givenDrugFdaRequestQueryMap() {
    return DrugFdaRequestQueryMap.builder()
        .withManufacturerName("test manufacturer name")
        .withBrandName("test brand name")
        .withPageable(PageRequest.of(3, 2))
        .build();
  }

  private void verifyOpenFdaDrugsFdaCall() {
    verifyHttp(stubServer).once(endsWithUri("/drug/drugsfda.json"),
        method(Method.GET),
        parameter("search", "openfda.brand_name:\"test brand name\" AND openfda.manufacturer_name:\"test manufacturer name\""),
        parameter("limit", "2"),
        parameter("skip", "6"));
  }

  private void stubOpenFdaResponse() {
    whenHttp(stubServer)
        .match(endsWithUri("/drug/drugsfda.json"))
        .then(ok(), resourceContent("mock/open-fda-drug-drugs-fda-response.json"));
  }

  private void stubOpenFdaResponseNotFound() {
    whenHttp(stubServer)
        .match(endsWithUri("/drug/drugsfda.json"))
        .then(status(org.glassfish.grizzly.http.util.HttpStatus.NOT_FOUND_404), resourceContent("mock/open-fda-drug-drugs-fda-response-not-found.json"));
  }

  private void stubOpenFdaResponseUnknownError() {
    whenHttp(stubServer)
        .match(endsWithUri("/drug/drugsfda.json"))
        .then(status(org.glassfish.grizzly.http.util.HttpStatus.INTERNAL_SERVER_ERROR_500), resourceContent("mock/open-fda-drug-drugs-fda-response-unknown-error.json"));
  }
}