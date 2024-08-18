package io.xmake.actions

import com.intellij.execution.RunManager
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import io.xmake.project.xmakeConsoleView
import io.xmake.run.XMakeRunConfiguration
import io.xmake.shared.xmakeConfiguration
import io.xmake.utils.SystemUtils

class RunAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        // the project
        val project = e.project ?: return

        // get selected run configuration
        val runConfiguration = RunManager.getInstance(project).selectedConfiguration?.configuration
        if (runConfiguration is XMakeRunConfiguration) {

            // clear console first
            project.xmakeConsoleView.clear()

            // configure and run it
            val xmakeConfiguration = project.xmakeConfiguration
            if (xmakeConfiguration.changed) {
                SystemUtils.runvInConsole(project, xmakeConfiguration.configurationCommandLine)
                    ?.addProcessListener(object : ProcessAdapter() {
                    override fun processTerminated(e: ProcessEvent) {
                        SystemUtils.runvInConsole(project, runConfiguration.runCommandLine, false, true, true)
                    }
                })
                xmakeConfiguration.changed = false
            } else {
                SystemUtils.runvInConsole(project, runConfiguration.runCommandLine, true, true, true)
            }

        } else {

            // show tips
            project.xmakeConsoleView.print("Please select a xmake run configuration first!\n", ConsoleViewContentType.ERROR_OUTPUT)
        }
    }
}
