package nl.ktmc.webfluxstockquoteservice;

import nl.ktmc.webfluxstockquoteservice.model.Quote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebfluxStockQuoteServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void fetchQuotesTest() {
		webTestClient
				.get()
				.uri("/quotes?size=20")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Quote.class)
				.hasSize(20)
				.consumeWith(allQuotes -> {
					assertThat(allQuotes.getResponseBody())
							.allSatisfy(quote -> assertThat(quote.getPrice()).isPositive());
					assertThat(allQuotes.getResponseBody()).hasSize(20);
				});
	}

	@Test
	public void testStreamQuotes() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(10);
		webTestClient
				.get()
				.uri("/quotes")
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.returnResult(Quote.class)
				.getResponseBody()
				.take(10)
				.subscribe(quote -> {
					assertThat(quote.getPrice()).isPositive();
					countDownLatch.countDown();
				});
		// wanneer de countdown 10x gebeurt wordt de flux released
		countDownLatch.await();
		System.out.println("Test klaar");
	}

}
