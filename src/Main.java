import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        final ExecutorService threadPool = Executors.newFixedThreadPool(25); // в лекции было озвучено, что еще есть специальный метод, который спрашивает у ОС про кол-во ядер для исполнения

        List<Future<Integer>> threadsCallable = new ArrayList<>();

        for (String text : texts) {
            Callable<Integer> myCallable = new MyCallable(text);
            final Future<Integer> task = threadPool.submit(myCallable); // отправляю в пул потоков
            threadsCallable.add(task); // добавляю в список
        }

        int maxSize = 0;
        for (Future<Integer> future : threadsCallable) {
            int currentMaxSize = future.get(); // получаем результат от потока
            if (currentMaxSize > maxSize) {
                maxSize = currentMaxSize;
            }
        }

        threadPool.shutdown(); // завершаем работу потоков

        System.out.println("maxSize " + maxSize);

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
