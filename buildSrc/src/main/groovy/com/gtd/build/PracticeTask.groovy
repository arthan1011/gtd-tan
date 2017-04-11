package com.gtd.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by arthan on 11.04.2017. | Project gtd-tan
 */
class PracticeTask extends DefaultTask {
    String message = 'DEFAULT MESSAGE'

    @TaskAction
    def print() {
        println 'Practice Task print method'
        printMsg()
    }

    def printMsg() {
        println message
    }
}
