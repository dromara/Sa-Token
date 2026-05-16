package cn.dev33.satoken.reactor.model;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaResponseForReactorTest {

	@Test
	public void testSetStatusWithStandardCodes() {
		MockServerHttpResponse mockResponse = new MockServerHttpResponse();
		SaResponseForReactor saResponse = new SaResponseForReactor(mockResponse);

		saResponse.setStatus(200);
		assertEquals(200, mockResponse.getStatusCode().value());

		saResponse.setStatus(401);
		assertEquals(401, mockResponse.getStatusCode().value());

		saResponse.setStatus(500);
		assertEquals(500, mockResponse.getStatusCode().value());
	}

	@Test
	public void testSetStatusWithNonStandardCodes() {
		MockServerHttpResponse mockResponse = new MockServerHttpResponse();
		SaResponseForReactor saResponse = new SaResponseForReactor(mockResponse);

		assertDoesNotThrow(() -> saResponse.setStatus(499));
		assertEquals(499, mockResponse.getStatusCode().value());

		assertDoesNotThrow(() -> saResponse.setStatus(522));
		assertEquals(522, mockResponse.getStatusCode().value());

		assertDoesNotThrow(() -> saResponse.setStatus(599));
		assertEquals(599, mockResponse.getStatusCode().value());
	}

}
