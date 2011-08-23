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

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.nuget.agent.dependencies.impl.NuGetPackagesCollectorImpl;
import jetbrains.buildServer.nuget.agent.runner.install.PackagesInstallerRunner;
import jetbrains.buildServer.nuget.agent.parameters.PackagesInstallParameters;
import jetbrains.buildServer.nuget.agent.parameters.PackagesUpdateParameters;
import jetbrains.buildServer.nuget.common.PackageInfo;
import jetbrains.buildServer.nuget.common.PackagesUpdateMode;
import jetbrains.buildServer.util.ArchiveUtil;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 08.07.11 2:15
 */
public class InstallPackageIntegtatoinTest extends IntegrationTestBase {
  protected PackagesInstallParameters myInstall;
  protected PackagesUpdateParameters myUpdate;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    myInstall = m.mock(PackagesInstallParameters.class);
    myUpdate = m.mock(PackagesUpdateParameters.class);

    m.checking(new Expectations(){{
      allowing(myInstall).getNuGetParameters();
      will(returnValue(myNuGet));
      allowing(myUpdate).getNuGetParameters();
      will(returnValue(myNuGet));

      allowing(myLogger).activityStarted(with(equal("install")), with(any(String.class)), with(any(String.class)));
      allowing(myLogger).activityFinished(with(equal("install")), with(any(String.class)));
    }});
  }

  @Test
  public void test_01_online_sources() throws RunBuildException {
    ArchiveUtil.unpackZip(getTestDataPath("test-01.zip"), "", myRoot);

    fetchPackages(new File(myRoot, "sln1-lib.sln"), Collections.<String>emptyList(), false, false,
            Arrays.asList(
                    new PackageInfo("Machine.Specifications", "0.4.13.0"),
                    new PackageInfo("NUnit", "2.5.7.10213"),
                    new PackageInfo("Ninject", "2.2.1.4"))
    );

    List<File> packageses = Arrays.asList(new File(myRoot, "packages").listFiles());
    System.out.println("installed packageses = " + packageses);

    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.7.10213").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NInject.2.2.1.4").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/Machine.Specifications.0.4.13.0").isDirectory());
    Assert.assertEquals(4, packageses.size());
  }

  @Test
  public void test_01_online_sources_update_forConfig() throws RunBuildException {
    ArchiveUtil.unpackZip(getTestDataPath("test-01.zip"), "", myRoot);

    m.checking(new Expectations() {{
      allowing(myLogger).activityStarted(with(equal("update")), with(any(String.class)), with(equal("nuget")));
      allowing(myLogger).activityFinished(with(equal("update")), with(equal("nuget")));

      allowing(myUpdate).getUseSafeUpdate(); will(returnValue(false));
      allowing(myUpdate).getPackagesToUpdate(); will(returnValue(Collections.<String>emptyList()));
      allowing(myUpdate).getUpdateMode(); will(returnValue(PackagesUpdateMode.FOR_EACH_PACKAGES_CONFIG));
    }});

    fetchPackages(new File(myRoot, "sln1-lib.sln"), Collections.<String>emptyList(), false, true, null);


    List<File> packageses = Arrays.asList(new File(myRoot, "packages").listFiles());
    System.out.println("installed packageses = " + packageses);

    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.7.10213").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.10.11092").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NInject.2.2.1.4").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/Machine.Specifications.0.4.13.0").isDirectory());
    Assert.assertEquals(5, packageses.size());
  }

  @Test
  public void test_01_online_sources_update_forSln() throws RunBuildException {
    ArchiveUtil.unpackZip(getTestDataPath("test-01.zip"), "", myRoot);

    m.checking(new Expectations() {{
      allowing(myLogger).activityStarted(with(equal("update")), with(any(String.class)), with(equal("nuget")));
      allowing(myLogger).activityFinished(with(equal("update")), with(equal("nuget")));

      allowing(myUpdate).getUseSafeUpdate();
      will(returnValue(false));
      allowing(myUpdate).getPackagesToUpdate();
      will(returnValue(Collections.<String>emptyList()));
      allowing(myUpdate).getUpdateMode();
      will(returnValue(PackagesUpdateMode.FOR_SLN));
    }});

    fetchPackages(new File(myRoot, "sln1-lib.sln"), Collections.<String>emptyList(), false, true, null);


    List<File> packageses = Arrays.asList(new File(myRoot, "packages").listFiles());
    System.out.println("installed packageses = " + packageses);

    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.7.10213").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.10.11092").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NInject.2.2.1.4").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/Machine.Specifications.0.4.13.0").isDirectory());
    Assert.assertEquals(5, packageses.size());
  }

  @Test
  public void test_01_online_sources_update_safe() throws RunBuildException {
    ArchiveUtil.unpackZip(getTestDataPath("test-01.zip"), "", myRoot);

    m.checking(new Expectations() {{
      allowing(myLogger).activityStarted(with(equal("update")), with(any(String.class)), with(equal("nuget")));
      allowing(myLogger).activityFinished(with(equal("update")), with(equal("nuget")));

      allowing(myUpdate).getUseSafeUpdate();
      will(returnValue(true));
      allowing(myUpdate).getPackagesToUpdate();
      will(returnValue(Collections.<String>emptyList()));
      allowing(myUpdate).getUpdateMode();
      will(returnValue(PackagesUpdateMode.FOR_EACH_PACKAGES_CONFIG));
    }});

    fetchPackages(new File(myRoot, "sln1-lib.sln"), Collections.<String>emptyList(), false, true, null);


    List<File> packageses = Arrays.asList(new File(myRoot, "packages").listFiles());
    System.out.println("installed packageses = " + packageses);

    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.7.10213").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.10.11092").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NInject.2.2.1.4").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/Machine.Specifications.0.4.13.0").isDirectory());
    Assert.assertEquals(5, packageses.size());
  }

  @Test
  public void test_01_online_sources_ecludeVersion() throws RunBuildException {
    ArchiveUtil.unpackZip(getTestDataPath("test-01.zip"), "", myRoot);

    fetchPackages(new File(myRoot, "sln1-lib.sln"), Collections.<String>emptyList(), true, false,
            Arrays.asList(
                    new PackageInfo("Machine.Specifications", "0.4.13.0"),
                    new PackageInfo("NUnit", "2.5.7.10213"),
                    new PackageInfo("Ninject", "2.2.1.4")));

    List<File> packageses = Arrays.asList(new File(myRoot, "packages").listFiles());
    System.out.println("installed packageses = " + packageses);

    Assert.assertTrue(new File(myRoot, "packages/NUnit").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NInject").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/Machine.Specifications").isDirectory());
    Assert.assertEquals(4, packageses.size());
  }

  @Test(enabled = false, dependsOnGroups = "Need to understand how to check NuGet uses only specified sources")
  public void test_01_local_sources() throws RunBuildException {
    ArchiveUtil.unpackZip(getTestDataPath("test-01.zip"), "", myRoot);
    File sourcesDir = new File(myRoot, "js");
    ArchiveUtil.unpackZip(Paths.getTestDataPath("test-01-sources.zip"), "", sourcesDir);

    fetchPackages(new File(myRoot, "sln1-lib.sln"), Arrays.asList("file:///" + sourcesDir.getPath()), false, false, null);

    List<File> packageses = Arrays.asList(new File(myRoot, "packages").listFiles());
    System.out.println("installed packageses = " + packageses);

    Assert.assertTrue(new File(myRoot, "packages/NUnit.2.5.7.10213").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/NInject.2.2.1.4").isDirectory());
    Assert.assertTrue(new File(myRoot, "packages/Machine.Specifications.0.4.13.0").isDirectory());
    Assert.assertEquals(4, packageses.size());
  }

  private void fetchPackages(final File sln,
                             final List<String> sources,
                             final boolean excludeVersion,
                             final boolean update,
                             @Nullable Collection<PackageInfo> detectedPackages) throws RunBuildException {

    m.checking(new Expectations() {{
      allowing(myParametersFactory).loadNuGetFetchParameters(myContext);
      will(returnValue(myNuGet));
      allowing(myParametersFactory).loadInstallPackagesParameters(myContext, myNuGet);
      will(returnValue(myInstall));

      allowing(myNuGet).getNuGetExeFile();
      will(returnValue(Paths.getPathToNuGet()));
      allowing(myNuGet).getSolutionFile();
      will(returnValue(sln));
      allowing(myNuGet).getNuGetPackageSources();
      will(returnValue(sources));
      allowing(myInstall).getExcludeVersion();
      will(returnValue(excludeVersion));
      allowing(myParametersFactory).loadUpdatePackagesParameters(myContext, myNuGet);
      will(returnValue(update ? myUpdate : null));
    }});

    BuildProcess proc = new PackagesInstallerRunner(
            myActionFactory,
            myParametersFactory
    ).createBuildProcess(myBuild, myContext);
    ((NuGetPackagesCollectorImpl)myCollector).removeAllPackages();

    assertRunSuccessfully(proc, BuildFinishedStatus.FINISHED_SUCCESS);

    System.out.println(myCollector.getPackages());
    if (detectedPackages != null) {
      Assert.assertEquals(
              new TreeSet<PackageInfo>(myCollector.getPackages().getPackages()),
              new TreeSet<PackageInfo>(detectedPackages));
    }

    m.assertIsSatisfied();
  }

}
