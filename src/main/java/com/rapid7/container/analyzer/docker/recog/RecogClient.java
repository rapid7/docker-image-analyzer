package com.rapid7.container.analyzer.docker.recog;

import com.rapid7.recog.Recog;
import com.rapid7.recog.RecogMatch;
import com.rapid7.recog.RecogMatchResult;
import com.rapid7.recog.RecogMatcher;
import com.rapid7.recog.RecogMatchers;
import com.rapid7.recog.RecogVersion;
import com.rapid7.recog.provider.RecogMatchersProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static com.rapid7.recog.RecogType.BUILTIN;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class RecogClient implements Recog {
  
  private RecogMatchersProvider provider;

  public RecogClient(File matchersDirectory) {
    this.provider = new RecogMatchersProvider(BUILTIN, matchersDirectory);
  }

  @Override
  public List<RecogMatchResult> fingerprint(String description) {
    List<RecogMatchResult> matches = new ArrayList<>();
    matches.addAll(fingerprint(description, getMatchers()));

    // sort the results by the matcher preference, then OS certainty
    return matches.stream()
        .sorted(
            comparing((RecogMatchResult match) -> match.getPreference())
              .thenComparing((RecogMatchResult match) -> match.getMatches().containsKey("os.certainty") ? Float.valueOf(match.getMatches().get("os.certainty")) : 1)
            .reversed()
        ).collect(toList());
  }
  
  private Collection<RecogMatchResult> fingerprint(String description, Collection<RecogMatchers> recogMatchers) {

    Collection<RecogMatchResult> matches = new ArrayList<>();

    for (RecogMatchers matchers : recogMatchers) {
      for (RecogMatch match : matchers.getMatches(description)) {
        RecogMatcher matcher = match.getMatcher();
        matches.add(new RecogMatchResult(matchers.getKey(), matchers.getType(), matchers.getProtocol(), matchers.getPreference(), match.getMatcher().getDescription(), matcher.getPattern(), matcher.getExamples(), match.getParameters()));
      }
    }

    return matches;
  }

  @Override
  public RecogVersion refreshContent() {
    throw new UnsupportedOperationException("Local recog content cannot be refreshed");
  }
  
  protected Collection<RecogMatchers> getMatchers() {
    return provider.getMatchers(BUILTIN);
  }
}
