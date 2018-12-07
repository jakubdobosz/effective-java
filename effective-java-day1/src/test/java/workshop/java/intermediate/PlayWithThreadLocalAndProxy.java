package workshop.java.intermediate;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

public class MakeOwnSpringTest {

    private static final ThreadLocal<TransactionContext> threadLocal = new ThreadLocal<>();

    @Test
    void simpleAspect() {
        SomeImplementation object = new SomeImplementation();
        SomeInterface wrapped = wrapWithAspect(object);

        wrapped.doBusiness();
    }

    private static SomeInterface wrapWithAspect(SomeInterface object) {
        return (SomeInterface) Proxy.newProxyInstance(
                SomeInterface.class.getClassLoader(),
                new Class[]{SomeInterface.class},
                (proxy, method, args) -> {
                    TransactionContext transaction = threadLocal.get();
                    boolean shouldCloseTransaction = false;
                    if (transaction == null) {
                        shouldCloseTransaction = true;
                        threadLocal.set(TransactionContext.beginTransaction());
                    }
                    try {
                        Object value = method.invoke(object, args);
                        if (shouldCloseTransaction) {
                            transaction.commitTransaction();
                        }
                        return value;
                    } catch (Throwable t) {
                        transaction.rollbackTransaction();
                        return null;
                    }
                }
        );
    }


    interface SomeInterface {
        void doBusiness();
    }

    private class SomeImplementation implements SomeInterface {
        @Override
        public void doBusiness() {
            System.out.println("hi");
        }
    }

    private static class TransactionContext {
        public static TransactionContext beginTransaction() {
            System.out.println("begin transaction");
            return new TransactionContext();
        }

        public void commitTransaction() {
            System.out.println("commit transaction");
        }

        public void rollbackTransaction() {
            System.out.println("commit transaction");
        }
    }
}
