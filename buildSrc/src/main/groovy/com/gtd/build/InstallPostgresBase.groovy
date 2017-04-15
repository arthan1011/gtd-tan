package com.gtd.build

import groovy.swing.SwingBuilder
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by arthan on 15.04.2017. | Project gtd-tan
 */
class InstallPostgresBase extends AppPropertiesAccess {

    @TaskAction
    def action() {

        def isConfirmed = false

        def warning = "Database ${getDatabaseName()} will be rewrited. "
        print warning
        println "Are you sure? "
        new SwingBuilder().edt {
            dialog(modal: true, // Otherwise the build will continue running before you closed the dialog
                    title: 'Confirmation', // Dialog title
                    alwaysOnTop: true, // pretty much what the name says
                    resizable: false, // Don't allow the user to resize the dialog
                    locationRelativeTo: null, // Place dialog in center of the screen
                    pack: true, // We need to pack the dialog (so it will take the size of it's children)
                    show: true // Let's show it
            ) {
                vbox { // Put everything below each other
                    label(warning)
                    label(text: "Are you sure?")
                    button(defaultButton: true, text: 'Yes', actionPerformed: {
                        isConfirmed = true
                        getProject().logger.quiet("yes")
                        dispose() // Close dialog
                    })
                    button(defaultButton: true, text: 'No', actionPerformed: {
                        getProject().logger.quiet("no")
                        dispose()
                    })
                } // vbox end
            } // dialog end
        } // edt end

        if (isConfirmed) {
            println "OK. Executing..."
            restoreFromDump(getUsername(), getPassword(), getDatabaseName())
        } else {
            println "Aborting..."
        }
    }

    def restoreFromDump(user, password, database) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            getProject().logger.quiet"restoring database ${getDatabaseName()} on Windows machine"
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'cmd', '/c', "set PGPASSWORD=${password}"
                commandLine 'cmd', '/c', "dropdb -U ${user} ${database}"
                ignoreExitValue true
            }
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'cmd', '/c', "set PGPASSWORD=${password}"
                commandLine 'cmd', '/c', "createdb -U ${user} ${database}"
            }
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'cmd', '/c', "set PGPASSWORD=${password}"
                commandLine 'cmd', '/c', "psql -U ${user} ${database} < ${DUMP_FILES_FOLDER}/${DUMP_FILE_NAME_LATEST}"
            }
        }
        if (Os.isFamily(Os.FAMILY_UNIX)) {
            getProject().logger.quiet "restoring database ${getDatabaseName()} on Unix machine"
                        getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'bash', '-c', "PGPASSWORD=${password} dropdb -h localhost --port 5432 -U ${user} ${database}"
                ignoreExitValue true
            }
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'bash', '-c', "PGPASSWORD=${password} createdb -h localhost --port 5432 -U ${user} ${database}"
            }
            getProject().exec {
                workingDir "${getProject().projectDir}"
                commandLine 'bash', '-c', "PGPASSWORD=${password} psql -h localhost --port 5432 -U ${user} " +
                        "${database} < ${DUMP_FILES_FOLDER}/${DUMP_FILE_NAME_LATEST}"
            }
        }
    }
}
