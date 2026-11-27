package br.com.jylabtech.getnet_payment_tech.usecase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;

public class PrintUsecaseTest {

    private static MethodCall printCall() {
        return new MethodCall("print", Collections.singletonMap("printable_content", Collections.emptyList()));
    }

    private static PrintUsecase usecaseReturning(PrintUsecase.PrintOutcome... outcomes) {
        Deque<PrintUsecase.PrintOutcome> queue = new ArrayDeque<>();
        Collections.addAll(queue, outcomes);
        return new PrintUsecase(content -> queue.removeFirst());
    }

    @Test
    public void eachPrintRepliesOnceOnItsOwnResult() {
        PrintUsecase usecase = usecaseReturning(
                new PrintUsecase.PrintOutcome("SUCCESS", "0"),
                new PrintUsecase.PrintOutcome("SUCCESS", "0"));
        FakeResult first = new FakeResult();
        FakeResult second = new FakeResult();

        usecase.print(printCall(), first);
        usecase.print(printCall(), second);

        assertEquals(1, first.replies());
        assertEquals(1, first.successes.size());
        assertEquals(1, second.replies());
        assertEquals(1, second.successes.size());
        assertEquals("SUCCESS", ((Map<?, ?>) second.successes.get(0)).get("code"));
    }

    @Test
    public void printAfterErrorStillWorks() {
        PrintUsecase usecase = usecaseReturning(
                new PrintUsecase.PrintOutcome("ERROR", "Instance of PosDigital not initialized"),
                new PrintUsecase.PrintOutcome("SUCCESS", "0"),
                new PrintUsecase.PrintOutcome("ERROR", null));
        FakeResult first = new FakeResult();
        FakeResult second = new FakeResult();
        FakeResult third = new FakeResult();

        usecase.print(printCall(), first);
        usecase.print(printCall(), second);
        usecase.print(printCall(), third);

        assertEquals(1, first.replies());
        assertEquals("ERROR", first.errorCodes.get(0));
        assertEquals("Instance of PosDigital not initialized", first.errorMessages.get(0));
        assertEquals(1, second.replies());
        assertEquals(1, second.successes.size());
        assertEquals(1, third.replies());
        assertEquals("result error", third.errorMessages.get(0));
    }

    @Test
    public void runnerExceptionRepliesError() {
        PrintUsecase usecase = new PrintUsecase(content -> {
            throw new IllegalStateException("printer offline");
        });
        FakeResult result = new FakeResult();

        usecase.print(printCall(), result);

        assertEquals(1, result.replies());
        assertEquals("ERROR", result.errorCodes.get(0));
        assertEquals("printer offline", result.errorMessages.get(0));
    }

    /** Executor que só guarda as tarefas, para o teste controlar quando cada thread roda. */
    static final class QueueExecutor implements java.util.concurrent.Executor {
        final List<Runnable> tasks = new ArrayList<>();

        @Override
        public void execute(Runnable command) {
            tasks.add(command);
        }

        void runAll() {
            List<Runnable> copy = new ArrayList<>(tasks);
            tasks.clear();
            for (Runnable task : copy) task.run();
        }
    }

    @Test
    public void printRunsOnWorkExecutorAndRepliesOnResultExecutor() {
        QueueExecutor work = new QueueExecutor();
        QueueExecutor main = new QueueExecutor();
        int[] printed = {0};
        PrintUsecase usecase = new PrintUsecase(content -> {
            printed[0]++;
            return new PrintUsecase.PrintOutcome("SUCCESS", "0");
        }, work, main);
        FakeResult result = new FakeResult();

        usecase.print(printCall(), result);
        // Nada roda na thread chamadora (main): nem a impressão nem a resposta.
        assertEquals(0, printed[0]);
        assertEquals(0, result.replies());
        assertEquals(1, work.tasks.size());

        work.runAll();
        assertEquals(1, printed[0]);
        assertEquals(0, result.replies());
        assertEquals(1, main.tasks.size());

        main.runAll();
        assertEquals(1, result.replies());
        assertEquals(1, result.successes.size());
    }

    @Test
    public void rejectedWorkRepliesErrorOnce() {
        PrintUsecase usecase = new PrintUsecase(
                content -> new PrintUsecase.PrintOutcome("SUCCESS", "0"),
                command -> { throw new java.util.concurrent.RejectedExecutionException("fechado"); },
                Runnable::run);
        FakeResult result = new FakeResult();

        usecase.print(printCall(), result);

        assertEquals(1, result.replies());
        assertEquals("ERROR", result.errorCodes.get(0));
        assertEquals("fechado", result.errorMessages.get(0));
    }
}
