package com.rapid7.container.analyzer.docker.test;

import com.rapid7.recog.Recog;
import com.rapid7.recog.RecogMatchResult;
import java.util.List;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageAnalyzerMockConfig {

  public RecogMatcherService recogMatcherService;

  public Recog recogClient() {
    Recog client = mock(Recog.class);

    when(client.fingerprint(anyString())).then(new Answer<List<RecogMatchResult>>() {
      @Override
      public List<RecogMatchResult> answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return recogMatcherService.fingerprint((String) args[0]);
      }
    });

    return client;
  }
}
