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

package jetbrains.buildServer.nuget.agent.commands.impl;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.nuget.agent.commands.CommandFactory;
import jetbrains.buildServer.nuget.agent.parameters.*;
import jetbrains.buildServer.nuget.common.NuGetTeamCityProvider;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 21.07.11 16:10
 */
public class CommandFactoryImpl implements CommandFactory {
  private NuGetTeamCityProvider myProvider;

  public CommandFactoryImpl(@NotNull final NuGetTeamCityProvider provider) {
    myProvider = provider;
  }

  @NotNull
  public <T> T createInstall(@NotNull PackagesInstallParameters params, @NotNull File packagesConfig, @NotNull File targetFolder, @NotNull Callback<T> factory) throws RunBuildException {
    final List<String> argz = new ArrayList<String>();
    argz.add("install");
    argz.add(FileUtil.getCanonicalFile(packagesConfig).getPath()); //path to package
    if (params.getExcludeVersion()) {
      argz.add("-ExcludeVersion");
    }
    argz.add("-OutputDirectory");
    argz.add(FileUtil.getCanonicalFile(targetFolder).getPath());

    final NuGetFetchParameters nuget = params.getNuGetParameters();
    return executeNuGet(nuget, nuget.getNuGetPackageSources(), argz, packagesConfig.getParentFile(), Collections.singletonMap("EnableNuGetPackageRestore", "True"), factory);
  }

  @NotNull
  public <T> T createUpdate(@NotNull PackagesUpdateParameters params, @NotNull File packagesConfig, @NotNull File targetFolder, @NotNull Callback<T> factory) throws RunBuildException {
    final List<String> argz = new ArrayList<String>();
    argz.add("update");
    argz.add(FileUtil.getCanonicalFile(packagesConfig).getPath()); //path to package
    if (params.getUseSafeUpdate()) {
      argz.add("-Safe");
    }
    if (params.getIncludePrereleasePackages()) {
      argz.add("-Prerelease");
    }
    argz.add("-Verbose");
    argz.add("-RepositoryPath");
    argz.add(FileUtil.getCanonicalFile(targetFolder).getPath());

    for (String id : params.getPackagesToUpdate()) {
      argz.add("-Id");
      argz.add(id);
    }

    final NuGetFetchParameters nuget = params.getNuGetParameters();
    return executeNuGet(nuget, nuget.getNuGetPackageSources(), argz, packagesConfig.getParentFile(), factory);
  }

  @NotNull
  public <T> T createPack(@NotNull final File specFile,
                          @NotNull final NuGetPackParameters params,
                          @NotNull final File workdir,
                          @NotNull final Callback<T> factory) throws RunBuildException {
    final List<String> arguments = new ArrayList<String>();
    arguments.add("pack");
    arguments.add(specFile.getPath());

    arguments.add("-OutputDirectory");
    arguments.add(params.getOutputDirectory().getPath());

    arguments.add("-BasePath");
    arguments.add(params.getBaseDirectory().getPath());

    arguments.add("-Verbose");

    String version = params.getVersion();
    if (!StringUtil.isEmptyOrSpaces(version)) {
      arguments.add("-Version");
      arguments.add(version);
    }

    for (String exclude : params.getExclude()) {
      arguments.add("-Exclude");
      arguments.add(exclude);
    }

    if (params.packSymbols()) {
      arguments.add("-Symbols");
    }

    if (params.packTool()) {
      arguments.add("-Tool");
    }

    for (String prop : params.getProperties()) {
      arguments.add("-Properties");
      arguments.add(prop);
    }

    for (String cmd : params.getCustomCommandline()) {
      //TODO: check if -Build was added
      arguments.add(cmd);
    }

    return executeNuGet(params, Collections.<PackageSource>emptyList(), arguments, workdir, factory);
  }

  @NotNull
  public <T> T createPush(@NotNull final NuGetPublishParameters params,
                          @NotNull final File packagePath,
                          @NotNull final Callback<T> factory) throws RunBuildException {
    final String apiKey = "teamcity_nuget_api_key_" + System.currentTimeMillis();

    final List<String> arguments = new ArrayList<String>();
    arguments.add("push");
    arguments.add(packagePath.getPath());
    arguments.add("%%" + apiKey + "%%");
    if (params.getCreateOnly()) {
      arguments.add("-CreateOnly");
    }

    final PackageSource source = params.getPublishSource();
    return executeNuGet(
            params,
            source == null
                    ? Collections.<PackageSource>emptyList()
                    : Arrays.asList(source),
            arguments,
            packagePath.getParentFile(),
            Collections.singletonMap(apiKey, params.getApiKey()),
            factory);
  }

  @NotNull
  public <T> T createAuthorizeFeed(@NotNull final File authFile,
                                   @NotNull final NuGetParameters params,
                                   @NotNull final File workdir,
                                   @NotNull final Callback<T> factory) throws RunBuildException {
    final List<String> argz = new ArrayList<String>();

    argz.add(params.getNuGetExeFile().getPath());
    argz.add("TeamCity.AuthorizeFeed");
    argz.add(FileUtil.getCanonicalFile(authFile).getPath());

    return factory.createCommand(
            myProvider.getNuGetRunnerPath(),
            workdir,
            argz,
            Collections.<String, String>emptyMap());
  }

  @NotNull
  public <T> T createVersionCheck(@NotNull NuGetParameters params,
                                  @NotNull File versoinFile,
                                  @NotNull File workdir,
                                  @NotNull Callback<T> factory) throws RunBuildException {
    final List<String> argz = new ArrayList<String>();

    argz.add(params.getNuGetExeFile().getPath());
    argz.add("--TeamCity.NuGetVersion");
    argz.add(FileUtil.getCanonicalFile(versoinFile).getPath());

    return factory.createCommand(
            myProvider.getNuGetRunnerPath(),
            workdir,
            argz,
            Collections.<String, String>emptyMap());
  }

  @NotNull
  public <T> T createDeAuthorizeFeed(@NotNull final NuGetParameters params,
                                     @NotNull final File workdir,
                                     @NotNull final Callback<T> factory) throws RunBuildException {
    final List<String> argz = new ArrayList<String>();

    argz.add(params.getNuGetExeFile().getPath());
    argz.add("TeamCity.DeAuthorizeFeed");

    return factory.createCommand(
            myProvider.getNuGetRunnerPath(),
            workdir,
            argz,
            Collections.<String, String>emptyMap());
  }

  @NotNull
  private <T> T executeNuGet(@NotNull final NuGetParameters nuget,
                             @NotNull final Collection<PackageSource> sources,
                             @NotNull final Collection<String> arguments,
                             @NotNull final File workingDir,
                             @NotNull final Callback<T> factory) throws RunBuildException {
    return executeNuGet(nuget, sources, arguments, workingDir, Collections.<String,String>emptyMap(), factory);
  }

  @NotNull
  private <T> T executeNuGet(@NotNull final NuGetParameters nuget,
                             @NotNull final Collection<PackageSource> sources,
                             @NotNull final Collection<String> arguments,
                             @NotNull final File workingDir,
                             @NotNull final Map<String,String> additionalEnvironment,
                             @NotNull final Callback<T> factory) throws RunBuildException {
    final List<String> argz = new ArrayList<String>(arguments);
    for (PackageSource source : sources) {
      argz.add("-Source");
      argz.add(source.getSource());
    }

    return factory.createCommand(
            nuget.getNuGetExeFile(),
            workingDir,
            argz,
            additionalEnvironment
    );
  }
}
