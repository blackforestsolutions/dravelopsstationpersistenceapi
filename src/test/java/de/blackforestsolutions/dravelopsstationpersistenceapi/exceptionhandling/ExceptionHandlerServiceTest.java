package de.blackforestsolutions.dravelopsstationpersistenceapi.exceptionhandling;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getTravelPointWithNoEmptyFields;
import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerServiceTest {

    private final ExceptionHandlerService classUnderTest = new ExceptionHandlerServiceImpl();

    @Test
    void test_handleExceptions_with_calledObject_as_null_status_as_null_exception_as_null_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(null, null, null);

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_status_as_null_exception_as_null_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(getTravelPointWithNoEmptyFields(), null, null);

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_status_as_null_exception_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(getTravelPointWithNoEmptyFields(), null, new Exception());

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_as_null_status_as_null_exception_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(null, null, new Exception());

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_as_null_status_as_success_exception_as_null_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(null, Status.SUCCESS, null);

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_status_as_success_exception_as_null_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(getTravelPointWithNoEmptyFields(), Status.SUCCESS, null);

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .assertNext(TravelPoint -> assertThat(TravelPoint).isEqualToComparingFieldByFieldRecursively(getTravelPointWithNoEmptyFields()))
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_status_as_success_exception_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(getTravelPointWithNoEmptyFields(), Status.SUCCESS, new Exception());

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_as_null_status_as_success_exception_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(null, Status.SUCCESS, new Exception());

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_as_null_status_as_failed_exception_as_null_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(null, Status.FAILED, null);

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_status_as_failed_exception_as_null_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(getTravelPointWithNoEmptyFields(), Status.FAILED, null);

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_status_as_failed_exception_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(getTravelPointWithNoEmptyFields(), Status.FAILED, new Exception());

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_handleExceptions_with_calledObject_as_null_status_as_failed_exception_returns_emptyMono() {
        CallStatus<TravelPoint> testData = new CallStatus<>(null, Status.FAILED, new Exception());

        Mono<TravelPoint> result = classUnderTest.handleExceptions(testData);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
