package nl.ktmc.webfluxstockquoteservice.service;

import nl.ktmc.webfluxstockquoteservice.model.Quote;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class QuoteGeneratorServiceImplTest {

    QuoteGeneratorServiceImpl quoteGeneratorService = new QuoteGeneratorServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

    //output hiervan ga je waarschijnlijk niet zien..
    @Test
    public void fetchQuoteStream() {
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));
        quoteFlux.take(10)
                .subscribe(System.out::println);
    }

    //deze wel want de output wacht op de test door de countdownlatch
    @Test
    public void fetchQuoteStreamCountdown() throws InterruptedException {
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L));
        Consumer<Quote> printer = System.out::println;
        Consumer<Throwable> errorhandler = e -> System.out.println("foutje");
        CountDownLatch countDownLatch = new CountDownLatch(1); // wat is deze
        Runnable allDone = countDownLatch::countDown;

        quoteFlux.take(10)
                .subscribe(printer, errorhandler, allDone);
        countDownLatch.await();
    }
}