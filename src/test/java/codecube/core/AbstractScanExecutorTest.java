package codecube.core;

import java.io.*;
import java.nio.file.Path
        ;
import java.util.HashSet;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public abstract class AbstractScanExecutorTest {
  

  private ScanExecutor executor = new ScanExecutorImpl();

   final LanguagePlugin languagePlugin = newLanguagePlugin();

  private LanguagePlugin newLanguagePlugin() {
    try {
      String languageCode = languageCode();
      String inputFileExtension = InputFileExtensions.fromLanguageCode(languageCode);
      return new LanguagePlugin(findPluginFile().toUri().toURL(), inputFileExtension);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    // unreachable
    return null;
  }

  abstract Path findPluginFile();

  abstract String languageCode();

  abstract String validExampleCode();

  abstract String invalidExampleCode();

  abstract int issueCount();

  abstract int highlightingCount();

  abstract int symbolRefCount();

  @Test
  public void should_report_issues() {
    HashSet<String> texts = new HashSet<String>() {
        {
            add("a");
            add("b");
            add("c");
        }
    };

    ScanResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.errors()).isEmpty();
    assertThat(result.issues()).hasSize(issueCount());
	assertThat(texts).hasSize(3);
  }

  @Test
  public void should_report_highlightings() {
    ScanResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.errors()).isEmpty();
    assertThat(result.highlightings()).hasSize(highlightingCount());
  }

  @Test
  public void should_report_symbol_refs() {
    ScanResult result = execute(validExampleCode());
    assertThat(result.success()).isTrue();
    assertThat(result.errors()).isEmpty();
    assertThat(result.symbolRefs()).hasSize(symbolRefCount());
  }

  @Test
  public void should_report_analysis_failed() {
    ScanResult result = execute(invalidExampleCode() + validExampleCode());
    assertThat(result.success()).isFalse();
    assertThat(result.errors()).isNotEmpty();
    assertThat(result.errors().get(0).message()).isNotNull();
    // TODO let implementations return expected location of first parsing error
    //assertThat(result.errors().get(0).location()).isNotNull();
    //assertThat(result.errors().get(0).location().line()).isGreaterThanOrEqualTo(1);
    //assertThat(result.errors().get(0).location().lineOffset()).isGreaterThanOrEqualTo(0);

    assertThat(result.issues()).isEmpty();
    assertThat(result.highlightings()).isEmpty();
    assertThat(result.symbolRefs()).isEmpty();
  }

  private ScanResult execute(String code) {
    return executor.execute(languagePlugin, code);
  }
  
  abstract class Example {
    String field;
    int otherField;
  }
}
