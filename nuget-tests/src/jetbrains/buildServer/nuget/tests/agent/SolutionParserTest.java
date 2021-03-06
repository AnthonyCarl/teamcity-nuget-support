/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package jetbrains.buildServer.nuget.tests.agent;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.nuget.agent.util.sln.SolutionFileParser;
import jetbrains.buildServer.nuget.agent.util.sln.impl.SolutionParserImpl;
import jetbrains.buildServer.nuget.tests.integration.Paths;
import jetbrains.buildServer.util.FileUtil;
import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 15.06.12 20:06
 */
public class SolutionParserTest extends BaseTestCase {
  private Mockery m;
  private BuildProgressLogger myLogger;
  private SolutionFileParser myParser = new SolutionParserImpl();

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m = new Mockery();
    myLogger = m.mock(BuildProgressLogger.class);
  }

  @Test
  public void test_vs2008() throws IOException, RunBuildException {
    doTest("vs2008.sln",
            "SystemCoreReferenced\\SystemCoreReferenced.csproj",
            "SystemCoreNotReferenced\\SystemCoreNotReferenced.csproj");
  }

  @Test
  public void test_vs2010() throws IOException, RunBuildException {
    doTest( "vs2010.sln",
            "SystemCoreReferenced/SystemCoreReferenced.csproj",
            "SystemCoreNotReferenced_ImplicitAllowed/SystemCoreNotReferenced_ImplicitAllowed.csproj",
            "SystemCoreNotReferenced_ImplicitNotAllo/SystemCoreNotReferenced_ImplicitNotAllo.csproj",
            "NotDefaultImplicitReference/NotDefaultImplicitReference.csproj");
  }

  @Test
  public void test_vs2010_maxi() throws IOException, RunBuildException {
    doTest( "Lunochod1.sln",
            "Lunochod1\\Lunochod1.csproj",
            "Lunochod2\\Lunochod2.vcxproj",
            "Lunochod3\\Lunochod3.vbproj",
            "Lunochod5\\Lunochod5.csproj",
            "Lunochod6\\Lunochod6.csproj",
            "Lunochod6.Tests\\Lunochod6.Tests.csproj"
            );
  }

  @Test
  public void test_webSite() throws IOException, RunBuildException {
    doTest( "WebSiteReferencedProjects.sln",
            "..\\..\\WebSites\\WebSite2",
            "ClassLibrary1\\ClassLibrary1.csproj"
            );
  }

  @Test
  public void test_webSite11_1() throws IOException, RunBuildException {
    m.checking(new Expectations(){{
      allowing(myLogger).warning(with(any(String.class)));
    }});

    doTest("VS11Website.sln",
            "e:\\temp\\VS11Website"
    );
  }

  @Test
  public void test_webSite11_2() throws IOException, RunBuildException {
    m.checking(new Expectations() {{
      allowing(myLogger).warning(with(any(String.class)));
    }});

    doTest( "VS11Website2.sln",
            "..\\..\\WebSites\\WebSite1\\",
            "Test",
            "WebApplication1\\WebApplication1.csproj"
            );
  }

  @Test
  public void test_webSite11_ts() throws IOException, RunBuildException {
    doTest( "IncorrectTreeSectionStructure.sln",
            "CSSL4MusicPlayer\\CSSL4MusicPlayer.Web\\CSSL4MusicPlayer.Web.csproj",
            "CSSL4MusicPlayer\\CSSL4MusicPlayer\\CSSL4MusicPlayer.csproj"
            );
  }

  @Test
  public void test_webSite11_base() throws IOException, RunBuildException {
    doTest( "webProject2010.sln",
            "e:\\Temp\\x44"
            );
  }

  @Test
  public void test_projectData() throws IOException, RunBuildException {
    doTest( "ProjectsData.sln",
            "WebSite",
            "MVC_WebApp\\MVC_WebApp.csproj",
            "MVC2_WebApp\\MVC2_WebApp.csproj"
            );
  }

  @NotNull
  private File getTestDataPath(@NotNull String path) {
    return Paths.getTestDataPath("sln/" + path);
  }

  private void doTest(String slnName, String... relPaths) throws IOException, RunBuildException {
    final File sln = getTestDataPath(slnName);
    final Collection<File> projects = new TreeSet<File>();
    for (String path : relPaths) {
      projects.add(FileUtil.resolvePath(sln.getParentFile(), path));
    }

    Assert.assertEquals(projects, new TreeSet<File>(myParser.parseProjectFiles(myLogger, sln)));
  }

}
