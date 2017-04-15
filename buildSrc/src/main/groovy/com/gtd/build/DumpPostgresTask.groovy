package com.gtd.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.apache.tools.ant.taskdefs.condition.Os

/**
 * Created by arthan on 11.04.2017. | Project gtd-tan
 */
class DumpPostgresTask extends AppPropertiesAccess {

    @TaskAction
    def action() {
        createDumpFile(getUsername(), getPassword(), getDatabaseName())
        moveDumpFile()
    }

    def createDumpFile(String user, String password, String database) {
        println "executing pg_dump BEGIN"
        println "DB user: ${user}, password: ${password}, database: ${database}"

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            getProject().logger.quiet"executing pg_dump on Windows machine"
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'cmd', '/c', "set PGPASSWORD=${password}"
                commandLine 'cmd', '/c', "pg_dump -U ${user} ${database} > ${DUMP_FILE_NAME}"
            }
        }
        if (Os.isFamily(Os.FAMILY_UNIX)) {
            getProject().logger.quiet "executing pg_dump on Unix machine"
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'bash', '-c', "PGPASSWORD=${password} pg_dump -h localhost --port 5432 -U ${user} ${database} > ${DUMP_FILE_NAME}"
            }
        }

        println "executing pg_dump END"
    }

    def moveDumpFile() {
        def latestDump = new File(DUMP_FILES_FOLDER, DUMP_FILE_NAME_LATEST)

        getProject().logger.quiet "Deleting latest dump file"
        def latestDumpDeleteSuccessful = latestDump.delete()
        if (!latestDumpDeleteSuccessful) {
            println "Problems deleting latest dump file"
        }

        getProject().logger.quiet "Moving dump file"
        def moveSuccessful = getProject().file(DUMP_FILE_NAME).renameTo(latestDump)
        if (!moveSuccessful) {
            println "Problems moving file"
        }
    }
}
