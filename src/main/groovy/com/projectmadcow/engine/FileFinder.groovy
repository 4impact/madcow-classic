/*
 * Copyright 2008-2011 4impact Technology Services, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.projectmadcow.engine

import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

/**
 * File Finder locates files, returning lists or singular matching files.
 * Searching is done on the Classpath, using the PathMatchingResourcePatternResolver.
 */
public class FileFinder {

    private static final Logger LOG = Logger.getLogger(FileFinder.class);
\
    public static List<File> findPropertyFiles(String propertiesName, String basedir = '') {
        return loadFilesFromFileName(propertiesName, ".properties", basedir)
    }

    public static File findPropertyFile(String propertiesName, String basedir = '') {
        def files = findFile(propertiesName, ".properties", basedir)

        if (files?.size() > 1) {
            throw new RuntimeException("more than one property file found : $files")
        }

        if (files?.size() == 0) {
            throw new RuntimeException("property file not found : ${propertiesName}.properties")
        }

        // return the first file
        return files[0]
    }

    public static List<File> loadFilesFromProperty(def madcowProperty, def fileExtension) {
        final String fileName = System.getProperty(madcowProperty)
        LOG.debug "loadFileFromProperty($madcowProperty, $fileExtension) filename from property : $fileName"
        return loadFilesFromFileName(fileName.split('!')[0], fileExtension)
    }

    private static List<File> findFile(String propertiesName, String suffix, String basedir = '') {
        String filenamePattern = addFileExtensionIfRequired(propertiesName, suffix)
        def resources = getResources("classpath*:${StringUtils.isEmpty(basedir) ? '' : "$basedir/"}**/$filenamePattern")

        def matchingResources = resources.findAll {Resource r ->
            try {
                LOG.debug "Resource = $r"
                r.file
                return true
            } catch (Exception e) {
                return false
            }
        }

        return matchingResources.collect { resource -> resource.file}
    }


    private static List<Resource> getResources(String pattern) {
        return new PathMatchingResourcePatternResolver().getResources(pattern) as List<Resource>
    }

    private static List<File> loadFilesFromFileName(String fileName, String fileExtension, String basedir = '') {
        File file = new File(fileName);
        if (file.exists()) {
            return [file]
        }
        return FileFinder.findFile(fileName, fileExtension, basedir)
    }

    private static String addFileExtensionIfRequired(String filename, String extension) {
        if (extension.startsWith('.'))
            return filename.endsWith(extension) ? filename : "$filename$extension"
        else
            return filename.endsWith(extension) ? filename : "$filename.$extension"
    }
}