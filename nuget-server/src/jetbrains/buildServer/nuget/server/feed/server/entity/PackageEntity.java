/****
****
**** THIS CODE IS GENERATED BY jetbrains.buildServer.nuget.tests.server.entity.EntityGenerator$EntityBeanGenerator
**** DO NOT CHANGE!
*****/
package jetbrains.buildServer.nuget.server.feed.server.entity;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PackageEntity { 
  protected final Map<String, String> myFields;

  public PackageEntity(@NotNull final Map<String, String> data) {
    myFields = data;
  }


  public java.lang.String getId() { 
    final String v = myFields.get("Id");
    return v;
  }


  public java.lang.String getVersion() { 
    final String v = myFields.get("Version");
    return v;
  }


  public java.lang.String getAuthors() { 
    final String v = myFields.get("Authors");
    return v;
  }


  public java.lang.String getCopyright() { 
    final String v = myFields.get("Copyright");
    return v;
  }


  public org.joda.time.LocalDateTime getCreated() { 
    final String v = myFields.get("Created");
    return jetbrains.buildServer.nuget.server.feed.server.index.ODataDataFormat.parseDate(v);
  }


  public java.lang.String getDependencies() { 
    final String v = myFields.get("Dependencies");
    return v;
  }


  public java.lang.String getDescription() { 
    final String v = myFields.get("Description");
    return v;
  }


  public java.lang.Integer getDownloadCount() { 
    final String v = myFields.get("DownloadCount");
    return Integer.parseInt(v);
  }


  public java.lang.String getGalleryDetailsUrl() { 
    final String v = myFields.get("GalleryDetailsUrl");
    return v;
  }


  public java.lang.String getIconUrl() { 
    final String v = myFields.get("IconUrl");
    return v;
  }


  public java.lang.Boolean getIsLatestVersion() { 
    final String v = myFields.get("IsLatestVersion");
    return Boolean.valueOf(v);
  }


  public java.lang.Boolean getIsAbsoluteLatestVersion() { 
    final String v = myFields.get("IsAbsoluteLatestVersion");
    return Boolean.valueOf(v);
  }


  public org.joda.time.LocalDateTime getLastUpdated() { 
    final String v = myFields.get("LastUpdated");
    return jetbrains.buildServer.nuget.server.feed.server.index.ODataDataFormat.parseDate(v);
  }


  public org.joda.time.LocalDateTime getPublished() { 
    final String v = myFields.get("Published");
    return jetbrains.buildServer.nuget.server.feed.server.index.ODataDataFormat.parseDate(v);
  }


  public java.lang.String getLanguage() { 
    final String v = myFields.get("Language");
    return v;
  }


  public java.lang.String getLicenseUrl() { 
    final String v = myFields.get("LicenseUrl");
    return v;
  }


  public java.lang.String getPackageHash() { 
    final String v = myFields.get("PackageHash");
    return v;
  }


  public java.lang.String getPackageHashAlgorithm() { 
    final String v = myFields.get("PackageHashAlgorithm");
    return v;
  }


  public java.lang.Long getPackageSize() { 
    final String v = myFields.get("PackageSize");
    return Long.parseLong(v);
  }


  public java.lang.String getProjectUrl() { 
    final String v = myFields.get("ProjectUrl");
    return v;
  }


  public java.lang.String getReportAbuseUrl() { 
    final String v = myFields.get("ReportAbuseUrl");
    return v;
  }


  public java.lang.String getReleaseNotes() { 
    final String v = myFields.get("ReleaseNotes");
    return v;
  }


  public java.lang.Boolean getRequireLicenseAcceptance() { 
    final String v = myFields.get("RequireLicenseAcceptance");
    return Boolean.valueOf(v);
  }


  public java.lang.String getSummary() { 
    final String v = myFields.get("Summary");
    return v;
  }


  public java.lang.String getTags() { 
    final String v = myFields.get("Tags");
    return v;
  }


  public java.lang.String getTitle() { 
    final String v = myFields.get("Title");
    return v;
  }


  public java.lang.Integer getVersionDownloadCount() { 
    final String v = myFields.get("VersionDownloadCount");
    return Integer.parseInt(v);
  }


 public boolean isValid() { 
    if (!myFields.containsKey("Id")) return false;
    if (!myFields.containsKey("Version")) return false;
    if (!myFields.containsKey("Authors")) return false;
    if (!myFields.containsKey("Copyright")) return false;
    if (!myFields.containsKey("Created")) return false;
    if (!myFields.containsKey("Dependencies")) return false;
    if (!myFields.containsKey("Description")) return false;
    if (!myFields.containsKey("DownloadCount")) return false;
    if (!myFields.containsKey("GalleryDetailsUrl")) return false;
    if (!myFields.containsKey("IconUrl")) return false;
    if (!myFields.containsKey("IsLatestVersion")) return false;
    if (!myFields.containsKey("IsAbsoluteLatestVersion")) return false;
    if (!myFields.containsKey("LastUpdated")) return false;
    if (!myFields.containsKey("Published")) return false;
    if (!myFields.containsKey("Language")) return false;
    if (!myFields.containsKey("LicenseUrl")) return false;
    if (!myFields.containsKey("PackageHash")) return false;
    if (!myFields.containsKey("PackageHashAlgorithm")) return false;
    if (!myFields.containsKey("PackageSize")) return false;
    if (!myFields.containsKey("ProjectUrl")) return false;
    if (!myFields.containsKey("ReportAbuseUrl")) return false;
    if (!myFields.containsKey("ReleaseNotes")) return false;
    if (!myFields.containsKey("RequireLicenseAcceptance")) return false;
    if (!myFields.containsKey("Summary")) return false;
    if (!myFields.containsKey("Tags")) return false;
    if (!myFields.containsKey("Title")) return false;
    if (!myFields.containsKey("VersionDownloadCount")) return false;
    return true;
  }
  public void visitFields(@NotNull final PackageFieldsVisitor visitor) {
    String v;
    v = myFields.get("Id");
    if (v != null) visitor.visitPackageField("Id", v, "Edm.String");
    v = myFields.get("Version");
    if (v != null) visitor.visitPackageField("Version", v, "Edm.String");
    v = myFields.get("Authors");
    if (v != null) visitor.visitPackageField("Authors", v, "Edm.String");
    v = myFields.get("Copyright");
    if (v != null) visitor.visitPackageField("Copyright", v, "Edm.String");
    v = myFields.get("Created");
    v = jetbrains.buildServer.nuget.server.feed.server.index.ODataDataFormat.toODataString(v);
    if (v != null) visitor.visitPackageField("Created", v, "Edm.DateTime");
    v = myFields.get("Dependencies");
    if (v != null) visitor.visitPackageField("Dependencies", v, "Edm.String");
    v = myFields.get("Description");
    if (v != null) visitor.visitPackageField("Description", v, "Edm.String");
    v = myFields.get("DownloadCount");
    if (v != null) visitor.visitPackageField("DownloadCount", v, "Edm.Int32");
    v = myFields.get("GalleryDetailsUrl");
    if (v != null) visitor.visitPackageField("GalleryDetailsUrl", v, "Edm.String");
    v = myFields.get("IconUrl");
    if (v != null) visitor.visitPackageField("IconUrl", v, "Edm.String");
    v = myFields.get("IsLatestVersion");
    if (v != null) visitor.visitPackageField("IsLatestVersion", v, "Edm.Boolean");
    v = myFields.get("IsAbsoluteLatestVersion");
    if (v != null) visitor.visitPackageField("IsAbsoluteLatestVersion", v, "Edm.Boolean");
    v = myFields.get("LastUpdated");
    v = jetbrains.buildServer.nuget.server.feed.server.index.ODataDataFormat.toODataString(v);
    if (v != null) visitor.visitPackageField("LastUpdated", v, "Edm.DateTime");
    v = myFields.get("Published");
    v = jetbrains.buildServer.nuget.server.feed.server.index.ODataDataFormat.toODataString(v);
    if (v != null) visitor.visitPackageField("Published", v, "Edm.DateTime");
    v = myFields.get("Language");
    if (v != null) visitor.visitPackageField("Language", v, "Edm.String");
    v = myFields.get("LicenseUrl");
    if (v != null) visitor.visitPackageField("LicenseUrl", v, "Edm.String");
    v = myFields.get("PackageHash");
    if (v != null) visitor.visitPackageField("PackageHash", v, "Edm.String");
    v = myFields.get("PackageHashAlgorithm");
    if (v != null) visitor.visitPackageField("PackageHashAlgorithm", v, "Edm.String");
    v = myFields.get("PackageSize");
    if (v != null) visitor.visitPackageField("PackageSize", v, "Edm.Int64");
    v = myFields.get("ProjectUrl");
    if (v != null) visitor.visitPackageField("ProjectUrl", v, "Edm.String");
    v = myFields.get("ReportAbuseUrl");
    if (v != null) visitor.visitPackageField("ReportAbuseUrl", v, "Edm.String");
    v = myFields.get("ReleaseNotes");
    if (v != null) visitor.visitPackageField("ReleaseNotes", v, "Edm.String");
    v = myFields.get("RequireLicenseAcceptance");
    if (v != null) visitor.visitPackageField("RequireLicenseAcceptance", v, "Edm.Boolean");
    v = myFields.get("Summary");
    if (v != null) visitor.visitPackageField("Summary", v, "Edm.String");
    v = myFields.get("Tags");
    if (v != null) visitor.visitPackageField("Tags", v, "Edm.String");
    v = myFields.get("Title");
    if (v != null) visitor.visitPackageField("Title", v, "Edm.String");
    v = myFields.get("VersionDownloadCount");
    if (v != null) visitor.visitPackageField("VersionDownloadCount", v, "Edm.Int32");
  }
}

