package codecube.core;

public interface ScanExecutor {
  ScanResult execute(LanguagePlugin languagePlugin, String code);
}
