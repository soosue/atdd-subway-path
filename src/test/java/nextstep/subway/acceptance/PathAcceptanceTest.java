package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * Scenario
     * Given 지하철 노선과 역들이 주어져 있을 때,
     * When 출발역과 도착역으로 경로 조회 요청을 하면
     * Then 경로가 조회된다.
     */
    @DisplayName("경로 조회")
    @Test
    void findPath() {
        // given
        Long sourceId = 1L;
        Long targetId = 6L;

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
                .then().log().all().extract();

        // then
    }
}
