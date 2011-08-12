/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.nuget.tests.integration;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.nuget.server.feed.reader.FeedConstants;
import jetbrains.buildServer.nuget.server.feed.reader.FeedPackage;
import jetbrains.buildServer.nuget.server.feed.reader.NuGetFeedReader;
import jetbrains.buildServer.nuget.server.feed.reader.impl.*;
import jetbrains.buildServer.util.FileUtil;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.08.11 16:04
 */
public class FeedReaderTest extends BaseTestCase {
  private NuGetFeedReader myReader;
  private FeedClient myClient;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myClient = new FeedClient();
    final FeedGetMethodFactory methods = new FeedGetMethodFactory();
    myReader = new NuGetFeedReaderImpl(myClient, new UrlResolver(myClient, methods), methods, new PackagesFeedParser());
  }

  @AfterMethod
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    myClient.dispose();
  }

  @Test
  public void testRead() throws IOException {
    final Collection<FeedPackage> feedPackages = myReader.queryPackageVersions(FeedConstants.FEED_URL, "NuGet.CommandLine");
    Assert.assertTrue(feedPackages.size() > 0);

    boolean hasLatest = false;
    for (FeedPackage feedPackage : feedPackages) {
      Assert.assertFalse(hasLatest && feedPackage.isLatestVersion(), "There could be only one latest");
      hasLatest |= feedPackage.isLatestVersion();
      System.out.println("feedPackage = " + feedPackage);
    }
  }

  @Test
  public void testDownloadLatest() throws IOException {
    final Collection<FeedPackage> packages = myReader.queryPackageVersions(FeedConstants.FEED_URL, "NuGet.CommandLine");
    FeedPackage latest = null;
    for (FeedPackage aPackage : packages) {
      if (aPackage.isLatestVersion()) {
        latest = aPackage;
      }
    }
    Assert.assertNotNull(latest, "there should be the latest package");

    final File pkd = createTempFile();
    myReader.downloadPackage(latest, pkd);

    Assert.assertTrue(pkd.length() > 100);
    boolean hasNuGetExe = false;
    ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(pkd)));
    try {
      for (ZipEntry ze = zip.getNextEntry(); ze != null; ze = zip.getNextEntry()) {
        String name = ze.getName();
        System.out.println("ze = " + name);
        name = name.toLowerCase();
        hasNuGetExe |= name.endsWith("/NuGet.exe".toLowerCase());
        hasNuGetExe |= name.endsWith("\\NuGet.exe".toLowerCase());
      }
    } finally {
      FileUtil.close(zip);
    }

    Assert.assertTrue(hasNuGetExe, "package should contain nuget.exe");
  }
}
