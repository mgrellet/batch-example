package com.example.testbatchreq.controller;


import com.example.testbatchreq.dto.Request;
import com.example.testbatchreq.dto.Response;
import com.example.testbatchreq.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/batch")
public class BatchController {

    Logger logger = LoggerFactory.getLogger(BatchController.class);

    private BatchService batchService;

    @Autowired
    public BatchController(BatchService batchService){
        this.batchService = batchService;
    }

    @GetMapping("1")
    public ResponseEntity<List<Response>> create(){
        Flux<List<Response>> responseFlux = batchService.processRequestsInBatches(createPayload());
        List<Response> allResponses = responseFlux.collectList().block().stream()
                .flatMap(List::stream) // Flatten the nested lists
                .toList();
        logger.debug(String.valueOf(allResponses.size()));
        return ResponseEntity.ok(allResponses);
    }

    @GetMapping("2")
    public ResponseEntity<List<Response>> create2(){
        Mono<List<Response>> responseMono = batchService.create(createPayload());
        return ResponseEntity.ok(responseMono.block());
    }

    private List<Request> createPayload() {
        List<Request> requestList = new ArrayList<>();
        for(int i = 0; i < 3000; i++){
            requestList.add(new Request((double) (i + 1), "Name "+i));
        }
        return requestList;
    }


}
