package com.roisagiv.aroundme.managers;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.roisagiv.aroundme.utils.AssetReader;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.SocketPolicy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Google places auto complete test.
 */
@RunWith(Enclosed.class) public class GooglePlacesAutoCompleteAPITest {

  @RunWith(AndroidJUnit4.class) public static class AutoCompleteTest {
    /**
     * The Server.
     */
    @Rule public final MockWebServer server = new MockWebServer();

    /**
     * Should perform http request to google.
     *
     * @throws IOException the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test public void shouldPerformHttpRequestToGoogle() throws IOException, InterruptedException {
      // arrange
      server.enqueue(new MockResponse());
      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "");

      // act
      autoComplete.autoComplete("never mind");

      // assert
      RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.MILLISECONDS);
      assertThat(recordedRequest.getPath()).contains("/maps/api/place/autocomplete/json?");
    }

    /**
     * Should add api key to request.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test public void shouldAddApiKeyToRequest() throws InterruptedException {
      // arrange
      server.enqueue(new MockResponse());
      HttpUrl url = server.url("/");
      String apiKey = "my api key";
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), apiKey);

      // act
      autoComplete.autoComplete("never mind");

      // assert
      RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.MILLISECONDS);
      assertThat(recordedRequest.getPath()).contains("key=my%20api%20key");
    }

    /**
     * Should add text as input query parameter.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test public void shouldAddTextAsInputQueryParameter() throws InterruptedException {
      // arrange
      server.enqueue(new MockResponse());
      HttpUrl url = server.url("/");
      String text = "my super text";
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      autoComplete.autoComplete(text);

      // assert
      RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.MILLISECONDS);
      assertThat(recordedRequest.getPath()).contains("input=my%20super%20text");
    }

    @Test public void whenRequestSucceedShouldReturn5Results()
        throws IOException, InterruptedException {
      String json = AssetReader.readFile(InstrumentationRegistry.getContext(),
          "places_autocomplete_example.json");
      server.enqueue(new MockResponse().setBody(json).setResponseCode(200));

      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      NetworkResponse<List<PlacesAutoCompleteAPI.AutoCompletePrediction>> results =
          autoComplete.autoComplete("some text");

      // assert
      List<PlacesAutoCompleteAPI.AutoCompletePrediction> predictions = results.getResults();
      assertThat(predictions).hasSize(5);

      assertThat(predictions.get(0).getDescription()).isEqualTo("Victoria, Australia");
      assertThat(predictions.get(0).getId()).isEqualTo("ChIJT5UYfksx1GoRNJWCvuL8Tlo");
      assertThat(predictions.get(1).getDescription()).isEqualTo("Victoria, BC, Canada");
      assertThat(predictions.get(1).getId()).isEqualTo("ChIJcWGw3Ytzj1QR7Ui7HnTz6Dg");
      assertThat(predictions.get(2).getDescription()).isEqualTo(
          "Victoria Station, London, United Kingdom");
      assertThat(predictions.get(2).getId()).isEqualTo("ChIJDdMfySEFdkgRVkEX9DXbVvU");
      assertThat(predictions.get(3).getDescription()).isEqualTo(
          "Victoria Coach Station, Buckingham Palace Road, London, United Kingdom");
      assertThat(predictions.get(3).getId()).isEqualTo("ChIJrxpS6hgFdkgRhUwkBy1TEGU");
      assertThat(predictions.get(4).getDescription()).isEqualTo("Victorville, CA, United States");
      assertThat(predictions.get(4).getId()).isEqualTo("ChIJedLdY1pkw4ARdjT0JVkRlQ0");
    }

    @Test public void whenRequestFailsShouldReturnHttpCode() {
      server.enqueue(new MockResponse().setResponseCode(500));

      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      NetworkResponse<List<PlacesAutoCompleteAPI.AutoCompletePrediction>> results =
          autoComplete.autoComplete("some text");

      // assert
      assertThat(results.getHttpCode()).isEqualTo(500);
      assertThat(results.getResults()).isNull();
    }

    @Test public void whenNetworkFailsShouldReturnError() throws IOException {
      // arrange
      String json = AssetReader.readFile(InstrumentationRegistry.getContext(),
          "places_autocomplete_example.json");
      server.enqueue(new MockResponse().setBody(json)
          .setResponseCode(200)
          .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));

      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      NetworkResponse<List<PlacesAutoCompleteAPI.AutoCompletePrediction>> results =
          autoComplete.autoComplete("some text");

      // assert
      assertThat(results.getHttpCode()).isEqualTo(200);
      assertThat(results.getError()).isNotNull();
    }
  }

  @RunWith(AndroidJUnit4.class) public static class PredictionDetailsTest {

    /**
     * The Server.
     */
    @Rule public final MockWebServer server = new MockWebServer();

    /**
     * Should perform http request to google.
     *
     * @throws IOException the io exception
     * @throws InterruptedException the interrupted exception
     */
    @Test public void shouldPerformHttpRequestToGoogle() throws IOException, InterruptedException {
      // arrange
      server.enqueue(new MockResponse());
      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "");

      // act
      autoComplete.predictionDetails("never mind");

      // assert
      RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.MILLISECONDS);
      assertThat(recordedRequest.getPath()).contains("/maps/api/place/details/json?");
    }

    /**
     * Should add api key to request.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test public void shouldAddApiKeyToRequest() throws InterruptedException {
      // arrange
      server.enqueue(new MockResponse());
      HttpUrl url = server.url("/");
      String apiKey = "my api key";
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), apiKey);

      // act
      autoComplete.predictionDetails("never mind");

      // assert
      RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.MILLISECONDS);
      assertThat(recordedRequest.getPath()).contains("key=my%20api%20key");
    }

    /**
     * Should add text as input query parameter.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test public void shouldAddIdAsPlaceIdQueryParameter() throws InterruptedException {
      // arrange
      server.enqueue(new MockResponse());
      HttpUrl url = server.url("/");
      String id = "my super id";
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      autoComplete.predictionDetails(id);

      // assert
      RecordedRequest recordedRequest = server.takeRequest(10, TimeUnit.MILLISECONDS);
      assertThat(recordedRequest.getPath()).contains("placeid=my%20super%20id");
    }

    @Test public void whenRequestSucceedShouldReturnPredictionDetails()
        throws IOException, InterruptedException {
      String json =
          AssetReader.readFile(InstrumentationRegistry.getContext(), "places_details_example.json");
      server.enqueue(new MockResponse().setBody(json).setResponseCode(200));

      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      NetworkResponse<PlacesAutoCompleteAPI.PredictionDetails> results =
          autoComplete.predictionDetails("some id");

      // assert
      PlacesAutoCompleteAPI.PredictionDetails prediction = results.getResults();
      assertThat(prediction).isNotNull();
      assertThat(prediction.getId()).isEqualTo("4f89212bf76dde31f092cfc14d7506555d85b5c7");
      assertThat(prediction.getDescription()).isEqualTo("Google Sydney");
      assertThat(prediction.getLatitude()).isEqualTo(-33.8669710);
      assertThat(prediction.getLongitude()).isEqualTo(151.1958750);
    }

    @Test public void whenRequestFailsShouldReturnHttpCode() {
      server.enqueue(new MockResponse().setResponseCode(500));

      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      NetworkResponse<PlacesAutoCompleteAPI.PredictionDetails> results =
          autoComplete.predictionDetails("some id");

      // assert
      assertThat(results.getHttpCode()).isEqualTo(500);
      assertThat(results.getResults()).isNull();
    }

    @Test public void whenNetworkFailsShouldReturnError() throws IOException {
      // arrange
      String json =
          AssetReader.readFile(InstrumentationRegistry.getContext(), "places_details_example.json");
      server.enqueue(new MockResponse().setBody(json)
          .setResponseCode(200)
          .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));

      HttpUrl url = server.url("/");
      GooglePlacesAutoCompleteAPI autoComplete =
          new GooglePlacesAutoCompleteAPI(url.toString(), "key");

      // act
      NetworkResponse<PlacesAutoCompleteAPI.PredictionDetails> results =
          autoComplete.predictionDetails("some id");

      // assert
      assertThat(results.getHttpCode()).isEqualTo(200);
      assertThat(results.getError()).isNotNull();
    }
  }
}
