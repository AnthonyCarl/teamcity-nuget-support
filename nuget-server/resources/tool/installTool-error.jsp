<%--
  ~ Copyright 2000-2011 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ include file="/include-internal.jsp" %>
<jsp:useBean id="errorText" scope="request" type="java.lang.String"/>
<div id="nugetInstallFormResreshInner">
  <div class="error">
    <p>Failed to fetch latest NuGet packages from default feed.</p>
    <p><c:out value="${errorText}"/></p>
    <p>Press <a href="#" onclick="BS.NuGet.Tools.InstallPopup.refreshForm()">Refresh</a> to try again.</p>
    <script type="text/javascript">
      BS.NuGet.Tools.InstallPopup.disableSubmit();
    </script>
  </div>
</div>