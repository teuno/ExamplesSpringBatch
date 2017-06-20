package OrdersEnRegelsAanmakenVanStortingsbestand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;

class OrderToDatabaseListener implements SkipPolicy {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean shouldSkip(Throwable e, int i) throws SkipLimitExceededException {
        if (e instanceof FlatFileParseException) {
            FlatFileParseException exception = (FlatFileParseException) e;

            String rowNumber = String.valueOf(exception.getLineNumber());
            String reasonForFailure = exception.getCause().toString();
            String locationOfFailureClass = exception.getClass().getCanonicalName();
            String inputValueFromCSV = exception.getInput();

            flatFileParseExceptionLoggingMessage(rowNumber, reasonForFailure, locationOfFailureClass, inputValueFromCSV);

            return true;
        } else if (e instanceof IndexOutOfBoundsException) {
            log.info("Er staan lege regels aan het einde van de file");
            return true;
        }

        String reasonForFailure = e.getCause().toString();
        String locationOfFailureClass = e.getClass().getCanonicalName();

        unknownErrorLoggingMessage(reasonForFailure, locationOfFailureClass);
        return false;
    }

    private void flatFileParseExceptionLoggingMessage(String lineNumber, String reasonForFailure,
                                                      String locationOfFailureClass, String inputValueFromCSV) {
        log.warn(lineNumber + " " + reasonForFailure + " " + locationOfFailureClass + " " + inputValueFromCSV);
    }

    private void unknownErrorLoggingMessage(String reasonForFailure, String locationOfFailureClass) {
        log.error(reasonForFailure + " " + locationOfFailureClass);
    }
}
