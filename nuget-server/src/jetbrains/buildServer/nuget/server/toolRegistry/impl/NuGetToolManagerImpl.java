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

package jetbrains.buildServer.nuget.server.toolRegistry.impl;

import jetbrains.buildServer.nuget.server.toolRegistry.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.08.11 1:07
 */
public class NuGetToolManagerImpl implements NuGetToolManager {
  @NotNull
  public Collection<NuGetInstalledTool> getInstalledTools() {
    return Collections.emptyList();
  }

  @NotNull
  public Collection<NuGetTool> getAvailableTools() {
    return Collections.emptyList();
  }

  public void installTool(@NotNull NuGetTool tool, @NotNull ActionProgress progress) {

  }

  public void registerCustomTool(@NotNull NuGetUserTool tool, @NotNull ActionProgress progress) {

  }
}