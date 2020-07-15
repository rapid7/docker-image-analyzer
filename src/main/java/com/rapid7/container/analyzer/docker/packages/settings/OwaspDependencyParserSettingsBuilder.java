package com.rapid7.container.analyzer.docker.packages.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.owasp.dependencycheck.analyzer.AbstractAnalyzer;
import org.owasp.dependencycheck.analyzer.ArchiveAnalyzer;
import org.owasp.dependencycheck.analyzer.AssemblyAnalyzer;
import org.owasp.dependencycheck.analyzer.AutoconfAnalyzer;
import org.owasp.dependencycheck.analyzer.CMakeAnalyzer;
import org.owasp.dependencycheck.analyzer.CocoaPodsAnalyzer;
import org.owasp.dependencycheck.analyzer.Experimental;
import org.owasp.dependencycheck.analyzer.FileNameAnalyzer;
import org.owasp.dependencycheck.analyzer.GolangDepAnalyzer;
import org.owasp.dependencycheck.analyzer.GolangModAnalyzer;
import org.owasp.dependencycheck.analyzer.JarAnalyzer;
import org.owasp.dependencycheck.analyzer.MSBuildProjectAnalyzer;
import org.owasp.dependencycheck.analyzer.NodePackageAnalyzer;
import org.owasp.dependencycheck.analyzer.NugetconfAnalyzer;
import org.owasp.dependencycheck.analyzer.NuspecAnalyzer;
import org.owasp.dependencycheck.analyzer.OpenSSLAnalyzer;
import org.owasp.dependencycheck.analyzer.PEAnalyzer;
import org.owasp.dependencycheck.analyzer.PipAnalyzer;
import org.owasp.dependencycheck.analyzer.PythonDistributionAnalyzer;
import org.owasp.dependencycheck.analyzer.PythonPackageAnalyzer;
import org.owasp.dependencycheck.analyzer.RetireJsAnalyzer;
import org.owasp.dependencycheck.analyzer.Retired;
import org.owasp.dependencycheck.analyzer.RubyGemspecAnalyzer;
import org.owasp.dependencycheck.analyzer.SwiftPackageManagerAnalyzer;
import org.owasp.dependencycheck.utils.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class OwaspDependencyParserSettingsBuilder {

  public static OwaspDependencyParserSettingsBuilder ALL = OwaspDependencyParserSettingsBuilder.builder()
      .enableAnalyzers(Analyzer.values())
      .allowExperimentalAnalyzers()
      .allowRetiredAnalyzers();
  public static OwaspDependencyParserSettingsBuilder EXPERIMENTAL = OwaspDependencyParserSettingsBuilder.builder()
      .enableAnalyzers(Arrays.stream(Analyzer.values())
          .filter(analyzer -> !analyzer.isRetired())
          .toArray(Analyzer[]::new))
      .allowExperimentalAnalyzers();
  public static OwaspDependencyParserSettingsBuilder DEFAULT = OwaspDependencyParserSettingsBuilder.builder()
      .enableAnalyzers(Arrays.stream(Analyzer.values())
          .filter(analyzer -> !analyzer.isRetired())
          .filter(analyzer -> !analyzer.isExperimental())
          .toArray(Analyzer[]::new));

  private static final Logger LOGGER = LoggerFactory.getLogger(OwaspDependencyParserSettingsBuilder.class);
  private static final String TRUE = "true";
  private final Set<Analyzer> enabledAnalyzers;
  private final Map<String, Object> additionalProperties;
  private boolean useExperimentalAnalyzers;
  private boolean useRetiredAnalyzers;

  private OwaspDependencyParserSettingsBuilder() {
    enabledAnalyzers = new HashSet<>();
    additionalProperties = new HashMap<>();
  }

  public static OwaspDependencyParserSettingsBuilder builder() {
    return new OwaspDependencyParserSettingsBuilder();
  }

  /**
   * Experimental analyzers are immature and may result in a higher-amount of false-positive matches.
   *
   * @return this
   */
  public OwaspDependencyParserSettingsBuilder allowExperimentalAnalyzers() {
    useExperimentalAnalyzers = true;
    return this;
  }

  /**
   * Retired analyzers are deprecated and may result in a higher amount of false-positive matches.
   *
   * @return this
   */
  public OwaspDependencyParserSettingsBuilder allowRetiredAnalyzers() {
    useRetiredAnalyzers = true;
    return this;
  }

  /**
   * Sets the property required for the {@link Analyzer} to be enabled.
   * </p>
   * If the analyzer is {@link Analyzer#isExperimental()}
   * experimental analyzers must be explicitly enabled with {@link #allowExperimentalAnalyzers()} ()} prior to
   * calling {@link #build()} for the analyzer to be enabled.
   * </p>
   * If the analyzer is {@link Analyzer#isRetired()}
   * retired analyzers must be explicitly enabled with {@link #allowRetiredAnalyzers()} prior to
   * calling {@link #build()} for the analyzer to be enabled.
   *
   * @param analyzer The analyzer to enable
   * @return this
   */
  public OwaspDependencyParserSettingsBuilder enableAnalyzer(Analyzer analyzer) {
    enabledAnalyzers.add(analyzer);
    return this;
  }

  /**
   * Sets the properties required for the array of {@link Analyzer} to be enabled.
   * </p>
   * If any analyzer is {@link Analyzer#isExperimental()}
   * experimental analyzers must be explicitly enabled with {@link #allowExperimentalAnalyzers()} ()} prior to
   * calling {@link #build()} for the analyzer to be enabled.
   * </p>
   * If any analyzer is {@link Analyzer#isRetired()}
   * retired analyzers must be explicitly enabled with {@link #allowRetiredAnalyzers()} prior to
   * calling {@link #build()} for the analyzer to be enabled.
   *
   * @param analyzers The analyzers to enable
   * @return this
   */
  public OwaspDependencyParserSettingsBuilder enableAnalyzers(Analyzer... analyzers) {
    enabledAnalyzers.addAll(asList(analyzers));
    return this;
  }

  /**
   * Set other custom properties for tailoring the underlying dependency fingerprinting
   *
   * @param property The property key to enable, from {@link Settings.KEYS}
   * @param value    The property value
   * @return this
   */
  public OwaspDependencyParserSettingsBuilder setProperty(String property, Object value) {
    additionalProperties.put(property, String.valueOf(value));
    return this;
  }

  public Settings build() {
    Properties properties = new Properties();
    if (useExperimentalAnalyzers) {
      properties.put(Settings.KEYS.ANALYZER_EXPERIMENTAL_ENABLED, TRUE);
    }
    if (useRetiredAnalyzers) {
      properties.put(Settings.KEYS.ANALYZER_RETIRED_ENABLED, TRUE);
    }
    for (Analyzer analyzer : enabledAnalyzers) {
      if (analyzer.isExperimental()) {
        if (!useExperimentalAnalyzers) {
          LOGGER.info(format("The experimental analyzer \"%s\" will not be enabled as allowExperimentalAnalyzers() has not been set", analyzer.getName()));
        }
        if (analyzer.isRetired()) {
          if (!useRetiredAnalyzers) {
            LOGGER.info(format("The retired analyzer \"%s\" will not be enabled as allowRetiredAnalyzers() has not been set", analyzer.getName()));
          }
        }
      }
      properties.put(analyzer.getToggleKey(), TRUE);
    }
    properties.putAll(additionalProperties);
    return new Settings(properties);
  }

  @SuppressWarnings("unused")
  public enum Analyzer {
    ARCHIVE(ArchiveAnalyzer.class, Settings.KEYS.ANALYZER_ARCHIVE_ENABLED),
    FILENAME(FileNameAnalyzer.class, Settings.KEYS.ANALYZER_FILE_NAME_ENABLED),
    CMAKE(CMakeAnalyzer.class, Settings.KEYS.ANALYZER_CMAKE_ENABLED),
    ASSEMBLY(AssemblyAnalyzer.class, Settings.KEYS.ANALYZER_ASSEMBLY_ENABLED),
    MS_BUILD_PROJECT(MSBuildProjectAnalyzer.class, Settings.KEYS.ANALYZER_MSBUILD_PROJECT_ENABLED),
    JAR(JarAnalyzer.class, Settings.KEYS.ANALYZER_JAR_ENABLED),
    RETIRE_JS(RetireJsAnalyzer.class, Settings.KEYS.ANALYZER_RETIREJS_ENABLED),
    NODE_PACKAGE(NodePackageAnalyzer.class, Settings.KEYS.ANALYZER_NODE_PACKAGE_ENABLED),
    NUGET_CONF(NugetconfAnalyzer.class, Settings.KEYS.ANALYZER_NUGETCONF_ENABLED),
    NUSPEC(NuspecAnalyzer.class, Settings.KEYS.ANALYZER_NUSPEC_ENABLED),
    OPENSSL(OpenSSLAnalyzer.class, Settings.KEYS.ANALYZER_OPENSSL_ENABLED),
    RUBY_GEMSPEC(RubyGemspecAnalyzer.class, Settings.KEYS.ANALYZER_RUBY_GEMSPEC_ENABLED),
    COCOA_PODS(CocoaPodsAnalyzer.class, Settings.KEYS.ANALYZER_COCOAPODS_ENABLED),
    GO_LANG_MOD(GolangModAnalyzer.class, Settings.KEYS.ANALYZER_GOLANG_MOD_ENABLED),
    GO_LANG_DEP(GolangDepAnalyzer.class, Settings.KEYS.ANALYZER_GOLANG_DEP_ENABLED),
    PE(PEAnalyzer.class, Settings.KEYS.ANALYZER_PE_ENABLED),
    PYTHON_DISTRIBUTION(PythonDistributionAnalyzer.class, Settings.KEYS.ANALYZER_PYTHON_DISTRIBUTION_ENABLED),
    PYTHON_PACKAGE(PythonPackageAnalyzer.class, Settings.KEYS.ANALYZER_PYTHON_PACKAGE_ENABLED),
    PIP(PipAnalyzer.class, Settings.KEYS.ANALYZER_PIP_ENABLED),
    SWIFT(SwiftPackageManagerAnalyzer.class, Settings.KEYS.ANALYZER_SWIFT_PACKAGE_MANAGER_ENABLED),
    AUTOCONF(AutoconfAnalyzer.class, Settings.KEYS.ANALYZER_AUTOCONF_ENABLED);

    private final String name;
    private final String toggleKey;
    private final boolean isRetired;
    private final boolean isExperimental;

    Analyzer(Class<? extends AbstractAnalyzer> analyzerClass, String toggleKey) {
      this.name = analyzerClass.getSimpleName();
      this.toggleKey = toggleKey;
      this.isRetired = analyzerClass.isAnnotationPresent(Retired.class);
      this.isExperimental = analyzerClass.isAnnotationPresent(Experimental.class);
    }

    public String getName() {
      return name;
    }

    public String getToggleKey() {
      return toggleKey;
    }

    public boolean isExperimental() {
      return isExperimental;
    }

    public boolean isRetired() {
      return isRetired;
    }
  }
}
