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

package jetbrains.buildServer.nuget.server.toolRegistry.ui;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.BasePropertiesBean;
import jetbrains.buildServer.nuget.server.toolRegistry.NuGetInstalledTool;
import jetbrains.buildServer.nuget.server.toolRegistry.NuGetToolManager;
import jetbrains.buildServer.nuget.server.toolRegistry.tab.ServerSettingsTab;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.11 10:21
 */
public class ToolSelectorController extends BaseController {
  private final NuGetToolManager myToolManager;
  private final PluginDescriptor myDescriptor;
  private final String myPath;

  public ToolSelectorController(@NotNull final NuGetToolManager toolManager,
                                @NotNull final PluginDescriptor descriptor,
                                @NotNull final WebControllerManager web) {
    myToolManager = toolManager;
    myDescriptor = descriptor;
    myPath = descriptor.getPluginResourcesPath("tool/runnerSettings.html");
    web.registerController(myPath, this);
  }

  @NotNull
  public String getPath() {
    return myPath;
  }

  @Override
  protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
    final String name = safe(request.getParameter("name"));
    String value = parseValue(request, "value", name);
    final Collection<ToolInfo> tools = getTools();
    ensureVersion(value, tools);


    ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath("tool/runnerSettings.jsp"));
    mv.getModel().put("name", name);
    mv.getModel().put("value", value);
    mv.getModel().put("customValue", safe(parseValue(request, "customValue", "nugetCustomPath")));
    mv.getModel().put("clazz", safe(request.getParameter("class")));
    mv.getModel().put("style", safe(request.getParameter("style")));
    mv.getModel().put("items", tools);
    mv.getModel().put("settingsUrl", "/admin/serverConfig.html?init=1&tab=" + ServerSettingsTab.TAB_ID);
    return mv;
  }

  private void ensureVersion(@NotNull final String version, @NotNull Collection<ToolInfo> actionInfos) {
    if (!version.startsWith("?")) return;
    for (ToolInfo actionInfo : actionInfos) {
      if (actionInfo.getId().equals(version)) return;
    }
    actionInfos.add(new ToolInfo(version, "Not Installed: " + version.substring(1)));
  }

  @NotNull
  private Collection<ToolInfo> getTools() {
    final ArrayList<ToolInfo> result = new ArrayList<ToolInfo>();
    for (NuGetInstalledTool nuGetInstalledTool : myToolManager.getInstalledTools()) {
      result.add(new ToolInfo(nuGetInstalledTool));
    }
    return result;
  }

  private static String safe(@Nullable String s) {
    if (StringUtil.isEmptyOrSpaces(s)) return "";
    return s;
  }

  @NotNull
  private String parseValue(HttpServletRequest request, final String requestName, String propertyName) {
    String value = null;

    final BasePropertiesBean bean = (BasePropertiesBean)request.getAttribute("propertiesBean");
    if (bean != null) {
      value = bean.getProperties().get(propertyName);
    }
    if (value == null) {
      value = request.getParameter(requestName);
    }
    if (value == null) {
      value = "";
    }
    return value;
  }
}