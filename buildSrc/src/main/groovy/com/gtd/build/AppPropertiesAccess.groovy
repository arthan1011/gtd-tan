package com.gtd.build

import org.gradle.api.DefaultTask

/**
 * Created by arthan on 15.04.2017. | Project gtd-tan
 */
class AppPropertiesAccess extends DefaultTask {

    public static final String APPLICATION_PROPERTIES_PATH = "src/main/resources/application.properties"
    public static final String DUMP_FILES_FOLDER = "src/main/resources/db_dump"
    public static final String DUMP_FILE_NAME = "database.dump"
    public static final String DUMP_FILE_NAME_LATEST = "database_latest.dump"

    def readProperties() {
        Properties props = new Properties()
        getProject().file(APPLICATION_PROPERTIES_PATH).withInputStream {
            props.load(it)
        }
        return props
    }

    def getDatabaseName() {
        Properties props = readProperties()
        def url = props.getProperty("spring.datasource.url")
        def slashIndex = url.lastIndexOf('/')
        return url.substring(slashIndex + 1)
    }

    def getUsername() {
        return readProperties().getProperty("spring.datasource.username")
    }

    def getPassword() {
        return readProperties().getProperty("spring.datasource.password")
    }
}
