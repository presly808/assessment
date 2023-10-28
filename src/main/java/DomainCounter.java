import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DomainCounter {

  public static String count(String fileName) throws IOException {

    // PriorityQueue keeps n-top elements sorted
    final PriorityQueue<Entry<String, Long>> topDomains = new PriorityQueue<>(
        (a, b) -> b.getValue().compareTo(a.getValue())
    );

    // Stream will read file partially without loading the entire file into the program's memory
    // try with resources
    try (final Stream<String> stream = Files.lines(Paths.get(fileName))) {
      final Set<Entry<String, Long>> entries = stream
          // use parallel to speed up the process and utilize all the cores of the machine
          .parallel()
          .map(el -> el.trim().split("@"))
          .filter(el -> el.length == 2) // filter avoids corrupted lines, empty, broken lines
          .map(el -> el[1])
          // group by domain name with count as aggregation function
          .collect(Collectors.groupingBy(el -> el, Collectors.counting()))
          // moves all the results to priority queue
          .entrySet();
      topDomains.addAll(entries);
    }

    // Preparing Result as String
    int count = 0;
    final StringBuilder stringBuilder = new StringBuilder();
    while (!topDomains.isEmpty() && count < 10) {
      final Entry<String, Long> domainWithCount = topDomains.remove();
      stringBuilder
          .append(domainWithCount.getKey())
          .append(" ")
          .append(domainWithCount.getValue())
          .append(System.lineSeparator());
      count++;
    }
    return stringBuilder.toString();
  }

  public static void main(String[] args) throws IOException {
    if(args.length < 1) {
      throw new RuntimeException("Arguments are empty. Filename argument was expected");
    }
    System.out.println(DomainCounter.count(args[0]));
  }

}
