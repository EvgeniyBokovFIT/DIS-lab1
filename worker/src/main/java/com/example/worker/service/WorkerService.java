package com.example.worker.service;

import com.example.worker.utils.MD5Utils;
import generated.ManagerRequest;
import generated.CrackHashWorkerResponse;
import generated.ObjectFactory;
import org.paukov.combinatorics3.Generator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class WorkerService {
    private final static String MANAGER_URL = "http://manager:8000/internal/api/manager/hash/crack/request";

    @Async
    public void findWords(ManagerRequest request){

        List<String> foundWords = crackHash(request);

        makeResponse(request, foundWords);
    }

    private static List<String> crackHash(ManagerRequest request) {
        int alphabetSize = request.getAlphabet().getSymbols().size();
        long partSize = (long) Math.ceil(
                Math.pow(alphabetSize, request.getMaxLength()) /
                request.getPartCount());

        return Generator.permutation(request.getAlphabet().getSymbols())
                .withRepetitions(request.getMaxLength())
                .stream()
                .skip(partSize * request.getPartNumber())
                .limit(partSize)
                .filter(word -> request.getHash().equals(MD5Utils.calculateMd5(String.join("", word))))
                .map(word -> String.join("", word))
                .toList();
    }

    private static void makeResponse(ManagerRequest request, List<String> foundWords) {
        ObjectFactory objectFactory = new ObjectFactory();
        CrackHashWorkerResponse.Answers answers = objectFactory.createCrackHashWorkerResponseAnswers();
        answers.getWords().addAll(foundWords);
        CrackHashWorkerResponse response = objectFactory.createCrackHashWorkerResponse();
        response.setAnswers(answers);
        response.setPartNumber(request.getPartNumber());
        response.setRequestId(request.getRequestId());

        WebClient client = WebClient.create(MANAGER_URL);
        WebClient.RequestBodySpec bodySpec = client.method(HttpMethod.PATCH);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .bodyValue(response);
        headersSpec.retrieve().bodyToMono(String.class).block();
    }
}
