package shorter.service.test;

public class FirstTestForCycleDepsService {

    private final SecondTestForCycleDepsService secondTestForCycleDepsService;

    public FirstTestForCycleDepsService(SecondTestForCycleDepsService secondTestForCycleDepsService) {
        this.secondTestForCycleDepsService = secondTestForCycleDepsService;
    }

    private void doNothing() {
    }
}
