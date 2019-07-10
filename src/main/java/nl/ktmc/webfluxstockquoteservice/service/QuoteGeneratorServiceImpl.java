package nl.ktmc.webfluxstockquoteservice.service;

import nl.ktmc.webfluxstockquoteservice.model.Quote;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
@Service
public class QuoteGeneratorServiceImpl implements QuoteGeneratorService {

    private final MathContext mathContext = new MathContext(2);
    private final Random random = new Random();
    private final List<Quote> prices = new ArrayList<>();

    public QuoteGeneratorServiceImpl() {
        this.prices.add(new Quote("AAPL", BigDecimal.valueOf(160.16)));
        this.prices.add(new Quote("MSFT", 77.74));
        this.prices.add(new Quote("GOOG", 847.24));
        this.prices.add(new Quote("ORCL", 49.51));
    }

    @Override
    public Flux<Quote> fetchQuoteStream(Duration period) {
        return Flux.generate(() -> 0,
            (BiFunction<Integer, SynchronousSink<Quote>, Integer >) (index, sink) -> {
            Quote updatedQuote = updateQuote(this.prices.get(index));
            sink.next(updatedQuote);
            return ++index % this.prices.size();
        })
        .zipWith(Flux.interval(period))
        .map(t -> t.getT1())
        .map(quote -> {
            quote.setInstant(Instant.now());
            return quote;
        })
        .log("nl.ktmc.service.QuoteGenerator");
    }

    private Quote updateQuote(Quote quote) {
        BigDecimal priceChange = quote.getPrice()
                .multiply(new BigDecimal(0.05 * this.random.nextDouble(), this.mathContext));
        return new Quote(quote.getTicker(), quote.getPrice().add(priceChange));
    }
}
