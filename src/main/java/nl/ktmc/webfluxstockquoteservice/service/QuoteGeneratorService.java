package nl.ktmc.webfluxstockquoteservice.service;

import nl.ktmc.webfluxstockquoteservice.model.Quote;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface QuoteGeneratorService {

    // hoe vaak emitten we de quotes
    Flux<Quote> fetchQuoteStream(Duration period);
}
