package unportant.gist.csvparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import unportant.gist.csvparser.fsm.FsmParser;
import unportant.gist.csvparser.simple.SimpleParser;

import java.util.List;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class DummyBenchmark {

  private final Parser fsm = new FsmParser(',', '"');
  private final Parser simple = new SimpleParser(',', '"', 5);

  private int lineIdx = 0;
  private final String[] lines = {
          "a",
          "a,b,c,d,e",
          "aaaaaaaaaaaaaaaaaaaa,b",
          "d,e,e,e"
  };


  @Benchmark
  public List<String> fsm() {
    lineIdx = (lineIdx + 1) % lines.length;
    return fsm.parse(lines[lineIdx]);
  }

  @Benchmark
  public List<String> simple() {
    lineIdx = (lineIdx + 1) % lines.length;
    return simple.parse("a,b,c");
  }
}
