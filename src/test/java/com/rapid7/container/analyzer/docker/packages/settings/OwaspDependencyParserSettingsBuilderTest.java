package com.rapid7.container.analyzer.docker.packages.settings;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.owasp.dependencycheck.utils.InvalidSettingException;
import org.owasp.dependencycheck.utils.Settings;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwaspDependencyParserSettingsBuilderTest {
  @Test
  public void staticAllSettingsAllowsRetiredAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.ALL.build();
    assertTrue(settings.getBoolean(Settings.KEYS.ANALYZER_RETIRED_ENABLED));
  }

  @Test
  public void staticAllSettingsAllowsExperimentalAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.ALL.build();
    assertTrue(settings.getBoolean(Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED));
  }

  @Test
  public void staticAllSettingsHasAllAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.ALL.build();
    List<String> analyzerToggleKeys = Arrays.stream(OwaspDependencyParserSettingsBuilder.Analyzer.values())
        .map(OwaspDependencyParserSettingsBuilder.Analyzer::getToggleKey)
        .collect(toList());
    for (String analyzerToggleKey : analyzerToggleKeys) {
      assertTrue(settings.getBoolean(analyzerToggleKey),
          "Expected setting " + analyzerToggleKey + " to be true");
    }
  }

  @Test
  public void staticExperimentalSettingsDisallowsRetiredAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.EXPERIMENTAL.build();
    assertFalse(settings.getBoolean(Settings.KEYS.ANALYZER_RETIRED_ENABLED));
  }

  @Test
  public void staticExperimentalSettingsAllowsExperimentalAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.EXPERIMENTAL.build();
    assertTrue(settings.getBoolean(Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED));
  }

  @Test
  public void staticExperimentalSettingsDoesNotHaveRetiredAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.EXPERIMENTAL.build();
    List<String> retiredAnalyzerToggleKeys = Arrays.stream(OwaspDependencyParserSettingsBuilder.Analyzer.values())
        .filter(OwaspDependencyParserSettingsBuilder.Analyzer::isRetired)
        .map(OwaspDependencyParserSettingsBuilder.Analyzer::getToggleKey)
        .collect(toList());

    for (String retiredAnalyzerToggleKey : retiredAnalyzerToggleKeys) {
      assertFalse(settings.getBoolean(retiredAnalyzerToggleKey),
          "Expected setting " + retiredAnalyzerToggleKey + " to be false");
    }
  }

  @Test
  public void staticDefaultSettingsDisallowsRetiredAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.DEFAULT.build();
    assertFalse(settings.getBoolean(Settings.KEYS.ANALYZER_RETIRED_ENABLED));
  }

  @Test
  public void staticDefaultSettingsDisallowsExperimentalAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.DEFAULT.build();
    assertFalse(settings.getBoolean(Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED));
  }

  @Test
  public void staticDefaultSettingsDoesNotHaveRetiredNorExperimentalAnalyzers() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.DEFAULT.build();
    List<String> retiredOrExperimentalAnalyzerToggleKeys = Arrays.stream(OwaspDependencyParserSettingsBuilder.Analyzer.values())
        .filter(analyzer -> analyzer.isRetired() || analyzer.isExperimental())
        .map(OwaspDependencyParserSettingsBuilder.Analyzer::getToggleKey)
        .collect(toList());

    for (String retiredOrExperimentalAnalyzerToggleKey : retiredOrExperimentalAnalyzerToggleKeys) {
      assertFalse(settings.getBoolean(retiredOrExperimentalAnalyzerToggleKey),
          "Expected setting " + retiredOrExperimentalAnalyzerToggleKey + " to be false");
    }
  }

  @Test
  public void experimentalAnalyzersAreDisabledByDefault() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.builder().build();
    assertFalse(settings.getBoolean(Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED));
  }

  @Test
  public void retiredAnalyzersAreDisabledByDefault() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.builder().build();
    assertFalse(settings.getBoolean(Settings.KEYS.ANALYZER_RETIRED_ENABLED));
  }

  @Test
  public void allowExperimentalAnalyzersSetsPropertyTrue() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.builder()
        .allowExperimentalAnalyzers()
        .build();
    assertTrue(settings.getBoolean(Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED));
  }

  @Test
  public void allowRetiredAnalyzersSetsPropertyTrue() throws InvalidSettingException {
    Settings settings = OwaspDependencyParserSettingsBuilder.builder()
        .allowRetiredAnalyzers()
        .build();
    assertTrue(settings.getBoolean(Settings.KEYS.ANALYZER_RETIRED_ENABLED));
  }

  @Test
  public void enableAnalyzerSetsPropertyTrue() throws InvalidSettingException {
    OwaspDependencyParserSettingsBuilder.Analyzer expected = OwaspDependencyParserSettingsBuilder.Analyzer.ARCHIVE;
    Settings settings = OwaspDependencyParserSettingsBuilder.builder()
        .enableAnalyzer(expected)
        .build();
    assertTrue(settings.getBoolean(expected.getToggleKey()));
  }

  @Test
  public void enableAnalyzersSetsPropertiesTrue() throws InvalidSettingException {
    OwaspDependencyParserSettingsBuilder.Analyzer[] expected = {
        OwaspDependencyParserSettingsBuilder.Analyzer.ARCHIVE,
        OwaspDependencyParserSettingsBuilder.Analyzer.JAR};
    Settings settings = OwaspDependencyParserSettingsBuilder.builder()
        .enableAnalyzers(expected)
        .build();
    for (OwaspDependencyParserSettingsBuilder.Analyzer analyzer : expected) {
      assertTrue(settings.getBoolean(analyzer.getToggleKey()));
    }
  }

  @Test
  public void setPropertySetsPropertyToExpectedValue() throws InvalidSettingException {
    String expectedKey = Settings.KEYS.ANALYZER_RETIRED_ENABLED;
    Settings settings = OwaspDependencyParserSettingsBuilder.builder()
        .setProperty(expectedKey, true)
        .build();
    assertTrue(settings.getBoolean(expectedKey));
  }

  @Test
  public void setPropertySetsPropertyAsString() {
    String expectedKey = Settings.KEYS.ANALYZER_RETIRED_ENABLED;
    Settings settings = OwaspDependencyParserSettingsBuilder.builder()
        .setProperty(expectedKey, true)
        .build();
    assertEquals(settings.getString(expectedKey), "true");
  }
}
