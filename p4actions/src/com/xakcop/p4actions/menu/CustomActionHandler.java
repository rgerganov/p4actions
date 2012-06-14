package com.xakcop.p4actions.menu;

import java.util.ArrayList;
import java.util.List;

import com.xakcop.p4actions.Activator;
import com.xakcop.p4actions.console.ProcessConsole;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.handlers.HandlerUtil;

public class CustomActionHandler extends AbstractHandler {

    private Job customJob;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (customJob != null && customJob.getState() != Job.NONE) {
            MessageDialog.openInformation(HandlerUtil.getActiveShellChecked(event), "P4 Actions",
                    "Another P4 action is running");
            return null;
        }
        String id = event.getParameter(CustomActionsMenu.ID_PARAM);
        String clientName = event.getParameter(CustomActionsMenu.CLIENT_NAME_PARAM);
        String clientRoot = event.getParameter(CustomActionsMenu.CLIENT_ROOT_PARAM);
        String connAddr = event.getParameter(CustomActionsMenu.CONN_ADDR_PARAM);
        String cmdLine = event.getParameter(CustomActionsMenu.CMD_LINE);
        cmdLine = cmdLine.replace("{cln}", id);
        cmdLine = cmdLine.replace("{clientName}", clientName);
        cmdLine = cmdLine.replace("{clientRoot}", clientRoot);

        final String workingDir = clientRoot;
        Activator.trace("working dir: " + workingDir);
        final String p4Port = connAddr;
        Activator.trace("p4 port: " + p4Port);

        final ProcessConsole console = ProcessConsole.getInstance();
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
        final List<String> command = parseLine(cmdLine);
        customJob = new Job("Execute custom action and display its stdout/stderr on the console") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                console.startProcess(command, workingDir, p4Port);
                return Status.OK_STATUS;
            }
        };
        customJob.setSystem(true);
        customJob.schedule();
        return null;
    }

    List<String> parseLine(String cmdLine) {
        List<String> result = new ArrayList<String>();
        boolean inQuote = false;
        String token = "";
        for (int i = 0 ; i < cmdLine.length() ; i++) {
            char ch = cmdLine.charAt(i);
            if (ch == '"') {
                if (inQuote) {
                    inQuote = false;
                    if (!token.isEmpty()) {
                        result.add(token);
                        token = "";
                    }
                } else {
                    inQuote = true;
                }
            } else if (ch == ' ') {
                if (inQuote) {
                    token += ch;
                } else {
                    if (!token.isEmpty()) {
                        result.add(token);
                        token = "";
                    }
                }
            } else {
                token += ch;
            }
        }
        if (!token.isEmpty()) {
            result.add(token);
        }
        return result;
    }
}
