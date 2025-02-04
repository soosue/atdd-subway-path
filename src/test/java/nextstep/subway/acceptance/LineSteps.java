package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineSteps {
    private static final String LINES_PATH = "/lines";
    public static final String LINES_SECTIONS_PATH = "/lines/{lineId}/sections";

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES_PATH)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get(LINES_PATH)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().get(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().get(LINES_PATH + "/{id}", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES_PATH)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createResponse, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createResponse.header("location"))
                .then().log().all().extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(ExtractableResponse<Response> createResponse) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(createResponse.header("location"))
                .then().log().all().extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(LINES_SECTIONS_PATH, lineId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete(LINES_SECTIONS_PATH + "?stationId={stationId}", lineId, stationId)
                .then().log().all().extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.CREATED);
    }

    public static void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response, String ... names) {
        assertStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getList("name", String.class)).contains(names);
    }

    public static void 지하철_노선_조회됨(ExtractableResponse<Response> response, String name) {
        assertStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.OK);
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.NO_CONTENT);
    }

    public static void 지하철_노선_생성_중복됨(ExtractableResponse<Response> createResponse) {
        assertStatus(createResponse, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철_노선에_구간등록됨(Long lineId, Long ... stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stationIds);
    }

    public static void 지하철_노선에_구간등록_실패됨(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철_노선에_구간_제거됨(Long lineId, Long ... stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stationIds);
    }

    public static void 지하철_노선에_구간_제거되지_않음(Long lineId, Long ... stationIds) {
        지하철_노선에_구간_제거됨(lineId, stationIds);
    }

    public static void 지하철_노선_구간_제거_실패됨(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.BAD_REQUEST);
    }

    private static void assertStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    public static Long 지하철_노선_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        return 지하철_노선_생성_요청(createLineCreateParams(name, color, upStationId, downStationId, distance))
                .jsonPath().getLong("id");
    }

    public static Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    public static Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
