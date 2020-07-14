package shorter.service.test;

public class SecondTestForCycleDepsService {

    private final FirstTestForCycleDepsService firstTestForCycleDepsService;

    public SecondTestForCycleDepsService(FirstTestForCycleDepsService firstTestForCycleDepsService) {
        this.firstTestForCycleDepsService = firstTestForCycleDepsService;
    }

    private void doNothing() {
    }
}