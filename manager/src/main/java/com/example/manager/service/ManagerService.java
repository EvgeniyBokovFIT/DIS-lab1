package com.example.manager.service;

import com.example.manager.dto.CrackHashRequestResponse;
import com.example.manager.exception.CrackException;
import com.example.manager.repository.CrackHashRepository;
import com.example.manager.dto.CrackHashRequest;
import com.example.manager.dto.CrackHashStatusResponse;
import com.example.manager.model.RequestStatus;
import com.example.manager.model.CrackEntity;
import generated.ManagerRequest;
import generated.CrackHashWorkerResponse;
import generated.ObjectFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;
import java.util.*;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ManagerService {
    private static final List<String> ALPHABET = List.of(
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p", "q","r",
            "s","t","u","v","w","x","y","z","0","1","2","3","4","5","6","7","8","9", "");
    private final int WORKERS_COUNT = 4;

    private final CrackHashRepository crackHashRepository;

    public CrackHashRequestResponse startCracking(CrackHashRequest request) {
        String requestId = UUID.randomUUID().toString();
        CrackEntity crackEntity = new CrackEntity();
        crackEntity.setRequestTime(LocalTime.now());
        crackHashRepository.save(requestId, crackEntity);
        sendTaskToWorkers(requestId, request.hash(), request.maxLength());
        return new CrackHashRequestResponse(requestId);
    }

    private void sendTaskToWorkers(String requestId, String hash, int maxLength) {
        ObjectFactory factory = new ObjectFactory();

        ManagerRequest.Alphabet alphabet = factory.createCrackHashManagerRequestAlphabet();
        alphabet.getSymbols().addAll(ALPHABET);

        ManagerRequest request = factory.createCrackHashManagerRequest();
        request.setRequestId(requestId);
        request.setHash(hash);
        request.setMaxLength(maxLength);
        request.setAlphabet(alphabet);
        request.setPartCount(WORKERS_COUNT);

        for (int i = 0; i < WORKERS_COUNT; i++){
            request.setPartNumber(i);
            sendRequests(request, getWorkerUrl(i));
        }
    }

    private void sendRequests(ManagerRequest request, String url){
        WebClient client = WebClient.create(url);
        WebClient.RequestBodySpec bodySpec = client.method(HttpMethod.POST);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .bodyValue(request);
        headersSpec.retrieve().bodyToMono(String.class).block();
    }

    public CrackHashStatusResponse getStatus(String requestId) {
        Optional<CrackEntity> optionalCrackEntity = crackHashRepository.getById(requestId);
        if(optionalCrackEntity.isEmpty()) {
            throw new CrackException("Запроса с id:" + requestId + " не существует");
        }
        CrackEntity crackEntity = optionalCrackEntity.get();
        RequestStatus requestStatus = crackEntity.getRequestStatus();
        if (requestStatus == RequestStatus.IN_PROGRESS) {
            return new CrackHashStatusResponse(RequestStatus.IN_PROGRESS.name(), null);
        }
        if (requestStatus == RequestStatus.READY) {
            return new CrackHashStatusResponse(RequestStatus.READY.name(), crackEntity.getResults());
        }

        return new CrackHashStatusResponse(RequestStatus.ERROR.name(), null);
    }

    public void addAnswersToEntity(CrackHashWorkerResponse response) {
        CrackEntity crackEntity = crackHashRepository.getById(response.getRequestId()).get();
        synchronized (crackEntity){
            crackEntity.setWorkersAnswered(crackEntity.getWorkersAnswered() + 1);
            if(crackEntity.getWorkersAnswered() == WORKERS_COUNT){
                crackEntity.setRequestStatus(RequestStatus.READY);
            }
            crackEntity.getResults().addAll(response.getAnswers().getWords());
        }
    }

    @Scheduled(fixedDelay = 10_000)
    private void checkTimeout(){
        Map<String, CrackEntity> crackEntities = crackHashRepository.getCrackEntities();
        crackEntities.entrySet()
                .stream()
                .filter(crackEntity -> isTimedOut(crackEntity.getValue()))
                .forEach(crackEntity -> crackEntity.getValue().setRequestStatus(RequestStatus.ERROR));
    }

    private static boolean isTimedOut(CrackEntity crackEntity) {
        return crackEntity.getRequestStatus() == RequestStatus.IN_PROGRESS
                && crackEntity.getRequestTime().plusSeconds(60 * 10).isBefore(LocalTime.now());
    }

    private String getWorkerUrl(int workerNum) {
        return "http://worker" + workerNum + ":8080/internal/api/worker/hash/crack/task";
    }
}
