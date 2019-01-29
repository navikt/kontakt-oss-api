package no.nav.tag.kontaktskjema;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class Transactor {

    private final TransactionTemplate transactionTemplate;

    public Transactor(PlatformTransactionManager platformTransactionManager) {
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    public void inTransaction(InTransaction inTransaction) {
        transactionTemplate.execute(transactionStatus -> {
            try {
                inTransaction.run();
                return null;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }

    @FunctionalInterface
    public interface InTransaction {
        void run() throws Throwable;
    }

}