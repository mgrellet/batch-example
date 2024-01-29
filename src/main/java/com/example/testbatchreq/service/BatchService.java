package com.example.testbatchreq.service;

import com.example.testbatchreq.dto.Request;
import com.example.testbatchreq.dto.Response;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BatchService {


    public Mono<List<Response>> create(List<Request> batch) {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8080/api/batch")
                .build();
        return client
                .post()
                .bodyValue(batch)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Response>>() {});
    }

    public Flux<List<Response>> processRequestsInBatches(List<Request> requests) {
        int batchSize = 100; // Set an appropriate batch size

        return Flux.fromIterable(requests)
                .buffer(batchSize)
                .limitRate(10)
                .flatMap(this::create);
    }
}
