package com.xakcop.p4actions.console;

import java.io.IOException;

import com.xakcop.p4actions.Activator;

import org.eclipse.jface.action.Action;

public class TerminateProcessAction extends Action {

    private Process process;

    public TerminateProcessAction() {
        setToolTipText("Terminate");
        setImageDescriptor(Activator.getImageDescriptor("icons/teminate_enabled.gif"));
        setDisabledImageDescriptor(Activator.getImageDescriptor("icons/terminate_disabled.gif"));
        setHoverImageDescriptor(Activator.getImageDescriptor("icons/terminate_enabled.gif"));
        setEnabled(false);
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public void run() {
        if (process != null) {
            try {
                process.getOutputStream().close();
                process.destroy();
            } catch (IOException e) {
                Activator.logError("Error killing process", e);
            }
        }
    }

}
