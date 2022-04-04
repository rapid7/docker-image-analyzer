/***************************************************************************
 * COPYRIGHT (C) 2022, Rapid7 LLC, Boston, MA, USA.
 * All rights reserved. This material contains unpublished, copyrighted
 * work including confidential and proprietary information of Rapid7.
 **************************************************************************/
/**
 *
 */
package com.rapid7.container.analyzer.docker.packages;

import com.rapid7.container.analyzer.docker.model.MavenArtifactInfo;
import java.util.HashMap;
import java.util.Map;
import org.owasp.dependencycheck.dependency.Dependency;
import static java.lang.String.format;

/*
 * Checks the sha1 checksum of a package to identify vulnerable versions of spring-beans
 *
 * More information: https://security.snyk.io/vuln/SNYK-JAVA-ORGSPRINGFRAMEWORK-2436751
 */
public class Spring4ShellParser {
  private static Map<String, MavenArtifactInfo> SPRING_BEANS_VERSION_MAP;
  private static final String PACKAGE_NAME_FORMAT = "%s:%s";

  static {
    SPRING_BEANS_VERSION_MAP = new HashMap<>();
    SPRING_BEANS_VERSION_MAP.put("3f0ea6598a5a1eae0a672f025a33a0b7e0d6dfd3", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.18"));
    SPRING_BEANS_VERSION_MAP.put("3d9c415cb47c96a81b1267665f513e4676af53b4", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.17"));
    SPRING_BEANS_VERSION_MAP.put("15decec5cea7a91423272daaae6f5d050c23cf3b", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.16"));
    SPRING_BEANS_VERSION_MAP.put("a88e2ccfe8b131bcff2e643b90d52f6d928e7369", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.15"));
    SPRING_BEANS_VERSION_MAP.put("24cc27af89edc1581a57bb15bc160d2353f40a0e", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.14"));
    SPRING_BEANS_VERSION_MAP.put("1d90c96b287253ec371260c35fbbea719c24bad6", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.13"));
    SPRING_BEANS_VERSION_MAP.put("caaa1d489bce88d6aa01ddd255ad5046acf8f282", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.12"));
    SPRING_BEANS_VERSION_MAP.put("85f090e0969c8fef81e149bdbedc2fdfe082546d", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.11"));
    SPRING_BEANS_VERSION_MAP.put("1ff16eb107dd0411deaffa236467efed44d65c60", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.10"));
    SPRING_BEANS_VERSION_MAP.put("48600db2cb1abc0f7ef2b073f0c1abd78a83bcfc", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.9"));
    SPRING_BEANS_VERSION_MAP.put("03d66fed1eebfcd119efcabc6218c813700a21ed", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.8"));
    SPRING_BEANS_VERSION_MAP.put("8b1eacd7aaa12f7d173a2f0836d28bd0c1b098fe", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.7"));
    SPRING_BEANS_VERSION_MAP.put("99cc944fb3454410b47fc98d4b148a6205bfe8f6", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.6"));
    SPRING_BEANS_VERSION_MAP.put("7604a458b0d8a47cdb113cf874c21c9750b53188", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.5"));
    SPRING_BEANS_VERSION_MAP.put("ac6c5ea0ba82f555405f74104cf378f8071c6d25", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.4"));
    SPRING_BEANS_VERSION_MAP.put("855f38fc8b85901681495ac6c6379051ee688cd5", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.3"));
    SPRING_BEANS_VERSION_MAP.put("289d8047f7cc524d60ca0c4cc6fedb8cb003e02d", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.2"));
    SPRING_BEANS_VERSION_MAP.put("a4bb5ffad5564e4a0e25955e3a40b1c6158385b2", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.1"));
    SPRING_BEANS_VERSION_MAP.put("bd01e44fd597fd8e14fb4ff2bc11362305b3a0b8", new MavenArtifactInfo("spring-beans", "org.springframework", "5.3.1"));
    SPRING_BEANS_VERSION_MAP.put("3cfd1e0bb13a1947305eae9bf03c19e528e11a8b", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.19.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("efa904f0d53b47f00df75e1753dacde6eba871dc", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.18.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("984bd92cfa2ab99ba30f2c0f268e7723d1ab14c2", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.17.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("7c8ce5f1f712b23233b5c126f8dd222c94431065", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.16.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("7372138d9f4f3393990472db6d1c07603d5bb5dc", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.15.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("5dec932dfff847e476b18ef4e137222664b25c31", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.14.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("b20c22c16270e758441bb7f3ce6707c9034083bb", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.13.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("d3c571e7128fec6947cb93bd539fe07fd9084a7b", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.12.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("5ab03ccb4c8d7b0a00b96773742b2b02e8777b4c", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.11.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("88d4eb1380940163b7cbfe1f991158f4a4cd7058", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.10.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("80e722ffa73a43459f639d36e25aa4e4a08d8d79", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.9.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("5cfafc2b0f821bda0b537449a6fdf634b0a666ff", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.8.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("05465ab17688ed62254fdef411cf883fd5c3b77a", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.7.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("d1b4c338e52dac001a48e29a605bb8f73572ad84", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.6.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("6dd11f183d252605e5dae2552cd918b758390139", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.5.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("fc5b50a647f319f9ca6ab272177f6d8188bfdfe5", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.4.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("0250c8c641433dc06b1b44e4563fa08a2fbf8954", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.3.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("81e4d9cc2e8fac88ab4eb7325c4521bd07c6389c", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.2.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("d05690257d8e8034b180db3893d5baf8250fb9d3", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.1.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("03ae97694618c59e6af695a15e54fabb7e319776", new MavenArtifactInfo("spring-beans", "org.springframework", "5.2.0.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("8d6f2526f935e8687888d220be2ffb23295efc0f", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.20.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("df90e4006dedd9259186fc329e05ebb7074b3d33", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.19.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("9bb21eb84ccf1f474c57a9af37b5500316943cd7", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.18.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("8ba0b5e3bb65bcee9e46c66007dd6fbb80f4bdf0", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.17.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("093e2697bdbace15cf784a6cf0ad00eb2650adb7", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.16.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("aa4101e8e5acd6d28f77d2d1a9694b5ab3813343", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.15.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("c0dec97d0ba121e14c7f8ad6fcff16b9278f56e6", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.14.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("0398efa268223a8fdebec380dc14342f1d2ddd78", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.13.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("e653a9892589fa757f8454af9ee9e82acf8e250e", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.12.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("b910a97a7aceb39bbdaf560ea102c574200cdd78", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.11.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("6aa17eabf08008f22e7824285663662e7c27e3f4", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.10.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("05a03b3983108d73978aec2fa3e681aedad6782c", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.9.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("e35fa81d0142ef7c1247a7dee8b1ef2dd78c6322", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.8.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("14cd651e4aa3514e75710c9450c7a0c89413e63f", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.7.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("90d2f4bf7eced108de0b5bf617abb2b13a6206a3", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.6.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("58b10c61f6bf2362909d884813c4049b657735f5", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.5.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("507c9391e0b786704929453e7fd3a74cfba46534", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.4.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("9a15a7c84bd12516574bcaf87ffa38c1e65e8a2b", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.3.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("5d513701a79c92f0549574f5170a05c4af7c893d", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.2.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("b0f2d4c9b7cb3d94140df878635880302b67c403", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.1.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("8de4fe847e95a87e25acfb7fe5f21c0eddbe94c7", new MavenArtifactInfo("spring-beans", "org.springframework", "5.1.0.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("01e7300966cc805411cb7d5463fb2ac4d11caea8", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.20.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("869e4d847051825c61ef850a3fe07f3b76f6d339", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.19.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("90efe9d7c52f5eadd285f72f2bcb42147d7502f3", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.18.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("c4b5ca2fc4c60fd08ef14bcf50f7bc1839045e1a", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.17.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("e5be7f20449e875f7029789b1bd7cf2f050db371", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.16.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("734b5d1429bf2d36687038f5d9e079db86aa31b4", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.15.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("0949554d84e4abcb6f890c15ae4b11c5c7a299fa", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.14.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("13880e7cd21df626736d6a4f5decd17b42cdeaca", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.13.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("985983cc69e42defefef099c3cfdf24e81b28b6c", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.12.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("9d486329560a0cfc0ccdd71877eb59b945032bb1", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.11.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("8ec7da928082a123c50d5d70cd4b46382db27e8b", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.10.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("65f56fdab1bb90ad059e314d2f2f4cf76f9bdbde", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.9.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("5fc965d3e7f5515099244857a8ae9e2a208c169b", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.8.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("c1196cb3e56da83e3c3a02ef323699f4b05feedc", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.7.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("d609b83cd8a71650a70778cf8d02c9a05b9161fe", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.6.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("984445863c0bbdaaf860615762d998b471a6bf92", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.5.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("7a8c3d48d4c33621e64d1399721d8e067450fcbd", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.4.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("c65a623d51721f6037505ce6c11e5d19edfa1c3a", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.3.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("301ee07b390bc8b5691f4206411b49beb06f7ff2", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.2.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("5667beb711927d73ff89e487411b450c2fa4d42a", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.1.RELEASE"));
    SPRING_BEANS_VERSION_MAP.put("6f95f071cfe29a84661648e5445dcb054be0cfa7", new MavenArtifactInfo("spring-beans", "org.springframework", "5.0.0.RELEASE"));
  }

  public static Dependency getPackageNameandVersion(Dependency dependency) {
    String sha1Sum = dependency.getSha1sum();
    if (sha1Sum == null || sha1Sum.isEmpty()) {
      return dependency;
    }
    if (SPRING_BEANS_VERSION_MAP.containsKey(sha1Sum)) {
      MavenArtifactInfo info = SPRING_BEANS_VERSION_MAP.get(sha1Sum);
      dependency.setName(format(PACKAGE_NAME_FORMAT, info.getGroupId(), info.getArtifactId()));
      dependency.setVersion(info.getVersion());
    }
    return dependency;
  }
}
