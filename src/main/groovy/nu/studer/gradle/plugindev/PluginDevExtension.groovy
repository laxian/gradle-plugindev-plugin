/*
 * Copyright 2014 Etienne Studer
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
package nu.studer.gradle.plugindev

import nu.studer.gradle.util.Licenses
import org.gradle.api.Project

/**
 * Extension point to configure the plugin development plugin.
 */
class PluginDevExtension {

    private static final String GITHUB_URL_PREFIX = 'https://github.com/'

    private final PluginDevPlugin plugin
    private final Project project

    String pluginId
    String pluginName
    String pluginDescription
    String pluginImplementationClass
    Set<String> pluginLicenses
    Set<String> pluginTags
    String authorId
    String authorName
    String authorEmail
    String projectUrl
    String projectIssuesUrl
    String projectVcsUrl
    String projectInceptionYear
    Closure pomConfiguration

    PluginDevExtension(PluginDevPlugin plugin, Project project) {
        this.plugin = plugin
        this.project = project
        this.pluginLicenses = new LinkedHashSet<>()
        this.pluginTags = new LinkedHashSet<>()
    }

    def setPluginLicense(String license) {
        pluginLicenses.clear()
        pluginLicenses.add license
    }

    def setPluginLicenses(List<String> licenses) {
        pluginLicenses.clear()
        pluginLicenses.addAll licenses
    }

    def pluginLicense(String license) {
        pluginLicenses.add license
    }

    def pluginLicenses(String... licenses) {
        pluginLicenses.addAll licenses
    }

    def setPluginTag(String tag) {
        pluginTags.clear()
        pluginTags.add tag
    }

    def setPluginTags(List<String> tags) {
        pluginTags.clear()
        pluginTags.addAll tags
    }

    def pluginTag(String tag) {
        pluginTags.add tag
    }

    def pluginTags(String... tags) {
        pluginTags.addAll tags
    }

    def done() {
        applyDefaultValuesForEmptyValues()
        checkPropertiesHaveNonEmptyValues()
        checkPropertiesHaveValidValues()

        plugin.afterExtensionConfiguration this
    }

    private void applyDefaultValuesForEmptyValues() {
        // use default in case of missing plugin id, derived from package path of plugin class minus 'gradle' & 'plugin' packages
        if (!pluginId) {
            if (pluginImplementationClass?.indexOf('.') > -1) {
                pluginId = pluginImplementationClass.
                        substring(0, pluginImplementationClass.lastIndexOf('.')).
                        replaceAll('\\.gradle\\.', '.').
                        replaceAll('\\.gradle$', '').
                        replaceAll('\\.plugin\\.', '.').
                        replaceAll('\\.plugin$', '')
            }
        }

        // use default in case of missing plugin name, derived from project name
        if (!pluginName) {
            pluginName = project.name
        }

        // use default for github project in case of missing project issues url, derived from project url
        if (!projectIssuesUrl) {
            if (projectUrl?.startsWith(GITHUB_URL_PREFIX)) {
                projectIssuesUrl = "${projectUrl}/issues"
            }
        }

        // use default for github project in case of missing project vcs url, derived from project url
        if (!projectVcsUrl) {
            if (projectUrl?.startsWith(GITHUB_URL_PREFIX)) {
                projectVcsUrl = "${projectUrl}.git"
            }
        }
    }

    private void checkPropertiesHaveNonEmptyValues() {
        checkPropertyNotEmpty(pluginId, 'pluginId')
        checkPropertyNotEmpty(pluginName, 'pluginName')
        checkPropertyNotEmpty(pluginDescription, 'pluginDescription')
        checkPropertyNotEmpty(pluginImplementationClass, 'pluginImplementationClass')
        checkPropertyNotEmpty(pluginLicenses, 'pluginLicenses')
        checkPropertyNotEmpty(pluginTags, 'pluginTags')
        checkPropertyNotEmpty(authorId, 'authorId')
        checkPropertyNotEmpty(authorName, 'authorName')
        checkPropertyNotEmpty(authorEmail, 'authorEmail')
        checkPropertyNotEmpty(projectUrl, 'projectUrl')
        checkPropertyNotEmpty(projectIssuesUrl, 'projectIssuesUrl')
        checkPropertyNotEmpty(projectVcsUrl, 'projectVcsUrl')
        checkPropertyNotEmpty(projectInceptionYear, 'projectInceptionYear')
    }

    private void checkPropertiesHaveValidValues() {
        // ensure fully qualified plugin id
        if (!pluginId.contains('.')) {
            throw new IllegalStateException("Property 'pluginId' must be a fully qualified id: $pluginId")
        }

        // ensure valid license type
        pluginLicenses.each { String licenseTypeKey ->
            if (!Licenses.LICENSE_TYPES[licenseTypeKey]) {
                String supportedLicenseTypes = Licenses.LICENSE_TYPES.keySet().join(", ")
                throw new IllegalStateException("Property 'pluginLicenses' contains a non-supported license type: $licenseTypeKey." +
                        " Currently supported license types are: " + supportedLicenseTypes + ".")
            }
        }
    }

    private static void checkPropertyNotEmpty(String propertyValue, String propertyName) {
        if (!propertyValue?.trim()) {
            throw new IllegalStateException("Property '$propertyName' is missing or empty.")
        }
    }

    private static void checkPropertyNotEmpty(Set propertyValue, String propertyName) {
        if (!propertyValue) {
            throw new IllegalStateException("Property '$propertyName' is missing or empty.")
        }
    }

    @Override
    public String toString() {
        return "PluginDevExtension{" +
                "pluginId='" + pluginId + '\'' +
                ", pluginName='" + pluginName + '\'' +
                ", pluginDescription='" + pluginDescription + '\'' +
                ", pluginImplementationClass='" + pluginImplementationClass + '\'' +
                ", pluginLicenses=" + pluginLicenses +
                ", pluginTags=" + pluginTags +
                ", authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", projectUrl='" + projectUrl + '\'' +
                ", projectIssuesUrl='" + projectIssuesUrl + '\'' +
                ", projectVcsUrl='" + projectVcsUrl + '\'' +
                "  projectInceptionYear='" + projectInceptionYear + '\'' +
                ", pomConfiguration=" + pomConfiguration +
                "}"
    }

}
