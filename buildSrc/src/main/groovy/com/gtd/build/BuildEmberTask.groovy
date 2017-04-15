package com.gtd.build

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
*  Created by arthan on 4/12/17 . | Project gtd-tan
*/
class BuildEmberTask extends DefaultTask {
    @TaskAction
    def action() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            getProject().logger.quiet("building ember on Windows machine")
            getProject().exec {
                workingDir "${getProject().projectDir}\\src\\main\\webapp\\gtd"
                commandLine 'cmd', '/c', "npm install"
            }
            getProject().exec {
                workingDir "${getProject().projectDir}\\src\\main\\webapp\\gtd"
                commandLine 'cmd', '/c', "ember build"
            }
        }
        if (Os.isFamily(Os.FAMILY_UNIX)) {
            getProject().logger.quiet("building ember on Unix machine")
            getProject().exec {
                workingDir "${getProject().projectDir}/src/main/webapp/gtd"
                commandLine 'bash', '-c', "npm install"
            }
            getProject().exec {
                workingDir "${getProject().projectDir}/src/main/webapp/gtd"
                commandLine 'bash', '-c', "ember build"
            }
        }
    }
}
