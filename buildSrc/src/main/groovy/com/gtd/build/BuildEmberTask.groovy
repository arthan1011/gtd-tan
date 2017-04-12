package com.gtd.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by arthan on 4/12/17 .
 */
class BuildEmberTask extends DefaultTask {
    @TaskAction
    def action() {
        getProject().exec {
            workingDir "${getProject().projectDir}/src/main/webapp/gtd"
            commandLine 'bash', '-c', "ember build"
        }
    }
}
