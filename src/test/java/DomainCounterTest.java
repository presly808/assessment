import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DomainCounterTest {

  @Test
  void countOK() throws IOException {
    final String result = DomainCounter.count(
        DomainCounterTest.class.getResource("/domains.txt").getFile());
    final String lineSeparator = System.lineSeparator();
    Assertions.assertEquals(result, "example.com 91" + lineSeparator
        + "hotmail.com 44" + lineSeparator
        + "gmail.com 41" + lineSeparator
        + "yahoo.com 32" + lineSeparator);
  }

  @Test
  void countEmptyFile() throws IOException {
    final String result = DomainCounter.count(
        DomainCounterTest.class.getResource("/domains_empty.txt").getFile());
    Assertions.assertEquals(result, "");
  }
}