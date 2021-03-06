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

package jetbrains.buildServer.nuget.tests.integration.feed.server;

import jetbrains.buildServer.nuget.tests.integration.Paths;
import jetbrains.buildServer.nuget.tests.server.entity.FeedParseResult;
import jetbrains.buildServer.nuget.tests.server.entity.MetadataBeanProperty;
import jetbrains.buildServer.nuget.tests.server.entity.MetadataParseResult;
import jetbrains.buildServer.nuget.tests.server.entity.XmlFeedParsers;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.XmlUtil;
import org.jdom.*;
import org.jdom.xpath.XPath;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 06.01.12 22:23
 */
public class NuGetJavaFeedContentTest extends NuGetJavaFeedIntegrationTestBase {
  private static final boolean LOCAL_DIFF_GOLD_AND_GENERATED = false;

  @BeforeMethod
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMetadata_v1() throws JDOMException, IOException {
    final String s = openRequest("$metadata");
    compareStringAsXml(s, "/feed/odata/metadata.v1.xml");
  }

  @Test
  public void testMetadata_v2() throws JDOMException, IOException {
    final String s = openRequest("$metadata");
    checkMetadata(s, "/feed/odata/metadata.v2.xml");
  }

  private void checkMetadata(@NotNull final String metadataXml, @NotNull final String gold) throws JDOMException, IOException {
    MetadataParseResult actual = XmlFeedParsers.loadMetadataBeans(parseXml(metadataXml));
    MetadataParseResult expected = XmlFeedParsers.loadMetadataBeans(parseGoldXml(gold));

    Assert.assertEquals(listProps(actual.getKey()), listProps(expected.getKey()));
    Assert.assertTrue(listProps(actual.getData()).containsAll(listProps(expected.getData())));

    compareStringAsXml(metadataXml, gold);
  }

  private void checkFeed(@NotNull final String feedXml, @NotNull final String gold) throws JDOMException, IOException {
    final Element root = parseXml(feedXml);

    final String base = root.getAttributeValue("base", Namespace.XML_NAMESPACE);
    Assert.assertTrue(base.startsWith(getNuGetServerUrl()), base);

    XPath xp = XPath.newInstance("//a:content/@src");
    xp.addNamespace("a", "http://www.w3.org/2005/Atom");
    for (Object o : xp.selectNodes(root)) {
      Attribute att = (Attribute) o;
      final String value = att.getValue();
      Assert.assertTrue(value.startsWith(getServerBase() + "/"), value);
    }

    FeedParseResult actual = XmlFeedParsers.loadFeedBeans(root);
    FeedParseResult expected = XmlFeedParsers.loadFeedBeans(parseGoldXml(gold));

    Assert.assertFalse(actual.getPropertyNames().isEmpty());
    Assert.assertFalse(expected.getPropertyNames().isEmpty());
    Assert.assertFalse(actual.getAtomProperties().isEmpty());
    Assert.assertFalse(expected.getAtomProperties().isEmpty());

    Assert.assertTrue(actual.getPropertyNames().containsAll(expected.getPropertyNames()));
    Assert.assertEquals(actual.getAtomProperties().toString(), expected.getAtomProperties().toString());

    compareStringAsXml(feedXml, gold);
  }

  private Set<String> listProps(Collection<MetadataBeanProperty> result) {
    Set<String> set = new HashSet<String>();
    for (MetadataBeanProperty property : result) {
      set.add(property.getName());
    }
    return set;
  }

  @Test
  public void testRoot() throws JDOMException, IOException {
    final String s = openRequest("");
    compareStringAsXml(s, "/feed/odata/root.v2.xml", true);
  }

  @Test
  public void testPackages_v1() throws JDOMException, IOException {
    addPackage(Paths.getTestDataPath("/packages/CommonServiceLocator.1.0.nupkg"), false);
    final String s = openRequest("Packages()");

    checkFeed(s, "/feed/odata/packages.v1.CommonServiceLocator.xml");
  }

  @Test
  public void testPackages_v2() throws JDOMException, IOException {
    addPackage(Paths.getTestDataPath("/packages/CommonServiceLocator.1.0.nupkg"), false);
    final String s = openRequest("Packages()");

    checkFeed(s, "/feed/odata/packages.v2.CommonServiceLocator.xml");
  }

  @Test
  public void testPackages_count() throws JDOMException, IOException {
    addPackage(Paths.getTestDataPath("/packages/CommonServiceLocator.1.0.nupkg"), false);
    final String s = openRequest("Packages()/$count");
    Assert.assertEquals(s, "1");
  }


  @Test
  public void testVSRequests() {
    String[] reqs = {
            "Packages()",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeee',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeee',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeee%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeeeff',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeeeff',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeeeff%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeeeffdfg',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeeeffdfg',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeeeffdfg%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeeeffdfgdfg',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeeeffdfgdfg',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeeeffdfgdfg%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeeeffdfgdfgdfg',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeeeffdfgdfgdfg',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeeeffdfgdfgdfg%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeeeffdfgdfgdfgdf',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeeeffdfgdfgdfgdf',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeeeffdfgdfgdfgdf%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=((((Id%20ne%20null)%20and%20substringof('freerereeeeffdfgdfgdfgdfgdfg',tolower(Id)))%20or%20((Description%20ne%20null)%20and%20substringof('freerereeeeffdfgdfgdfgdfgdfg',tolower(Description))))%20or%20((Tags%20ne%20null)%20and%20substringof('%20freerereeeeffdfgdfgdfgdfgdfg%20',tolower(Tags))))%20and%20IsLatestVersion",
            "Packages()/$count?$filter=IsLatestVersion",
            "Packages()?$filter=(tolower(Id)%20eq%20'castle.core')%20or%20(tolower(Id)%20eq%20'castle.windsor')&$orderby=Id&$skip=0&$top=30",
    };

    for (String req : reqs) {
      assert200(req).run();
    }
  }


  private String replaceXml(@NotNull final String text) {
    return text
            .replace("http://nuget.org/api/v2/", "BASE_URI/")
            .replace(getNuGetServerUrl(), "BASE_URI/")
            .replaceAll("\\d+-\\d+-\\d+T\\d+:\\d+:\\d+(\\.\\d+)?Z?", "TIME")
            ;
  }

  private void compareStringAsXml(String actualXml, String goldPath) throws JDOMException, IOException {
    compareStringAsXml(actualXml, goldPath, false);
  }

  private void compareStringAsXml(String actualXml, String goldPath, boolean checkUnderTeamCity) throws JDOMException, IOException {
    //disable comparison check for TeamCity
    if (!checkUnderTeamCity && System.getenv("TEAMCITY_VERSION") != null) return;
    //noinspection PointlessBooleanExpression
    if (!checkUnderTeamCity && !LOCAL_DIFF_GOLD_AND_GENERATED) return;
    System.out.println("actualXml = " + actualXml);
    compareXmlWithGold(parseXml(actualXml), goldPath);
  }

  private Element parseXml(String text) {
    return XmlUtil.from_s(text);
  }

  private void compareXmlWithGold(Element actual, String goldPath) throws JDOMException, IOException {
    System.out.println("actual: \r\n" + XmlUtil.to_s(actual) + "\r\n\r\n");
    Element gold = parseGoldXml(goldPath);
    compareXml(actual, gold);
  }

  private Element parseGoldXml(String goldPath) throws IOException {
    final File file = Paths.getTestDataPath(goldPath);
    final String text = new String(FileUtil.loadFileText(file, "utf-8"));
    return parseXml(text);
  }

  private void compareXml(Element actual, Element gold) throws JDOMException {
    String actualText = XmlUtil.to_s(sortProperties(sortEntityTypeProperties(actual)));
    String goldText = XmlUtil.to_s(sortProperties(sortEntityTypeProperties(gold)));

    Assert.assertEquals(replaceXml(actualText), replaceXml(goldText));
  }

  private Element sortEntityTypeProperties(Element el) throws JDOMException {
    XPath xp = XPath.newInstance("//x:EntityType");
    xp.addNamespace("x", "http://schemas.microsoft.com/ado/2006/04/edm");

    for (Object pNode : xp.selectNodes(el)) {
      Element node = (Element) pNode;
      final List<Element> props = new ArrayList<Element>();
      for (Object pChild : new ArrayList<Object>(node.getChildren())) {
        Element child = (Element) pChild;
        if (child.getName().equals("Property")) {
          props.add((Element) child.clone());
          child.detach();
        }
      }

      Collections.sort(props, new Comparator<Element>() {
        public int compare(Element o1, Element o2) {
          return o1.getAttributeValue("Name").compareTo(o2.getAttributeValue("Name"));
        }
      });

      for (Element prop : props) {
        node.addContent((Content)prop);
      }
    }
    return el;
  }

  private Element sortProperties(Element el) throws JDOMException {
    XPath xp = XPath.newInstance("//m:properties");
    xp.addNamespace("m", "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata");

    for (Object pNode : xp.selectNodes(el)) {
      Element node = (Element) pNode;
      final List<Element> props = new ArrayList<Element>();
      for (Object pChild : new ArrayList<Object>(node.getChildren())) {
        Element child = (Element) pChild;
        props.add((Element) child.clone());
        child.detach();
      }

      Collections.sort(props, new Comparator<Element>() {
        public int compare(Element o1, Element o2) {
          return o1.getName().compareTo(o2.getName());
        }
      });

      for (Element prop : props) {
        node.addContent((Content)prop);
      }
    }
    return el;
  }

}
