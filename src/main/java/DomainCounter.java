import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DomainCounter {

  /**
   * Algorithm Complexity is O(Nlog2N). As we read each line, process it and inside
   * .groupingBy method we put it into HashMap also in the end we use PriorityQueue to sort it.
   * PriorityQueue will take O(N*log2N)
   *
   * Hence, we can say
   *  Read And Grouping {O(N)} +
   *  Adding to PriorityQueue aka Sorting {O(Nlog2N)} +
   *  Preparing to String {O(N)}
   *
   * In order to simplify, we can leave the only O(Nlog2N) as it is the highest complexity
   *
  */
  public static String count(String fileName) throws IOException {

    // PriorityQueue keeps n-top elements sorted
    // PriorityQueue supports O(log2N) for enqueing and dequeing
    final PriorityQueue<Entry<String, Long>> topDomains = new PriorityQueue<>(
        (a, b) -> b.getValue().compareTo(a.getValue())
    );

    // Stream will read file partially without loading the entire file into the program's memory
    // try with resources
    try (final Stream<String> stream = Files.lines(Paths.get(fileName))) {

      // Read and group by the domain take O(N) as inside Collectors.groupingBy we use HashMap to collect elements
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

      // O(Nlog2N), where N is unique domains that were found in the file
      topDomains.addAll(entries);
    }

    // Preparing Result as String
    // In order to Print we should just Iterate O(N)
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
