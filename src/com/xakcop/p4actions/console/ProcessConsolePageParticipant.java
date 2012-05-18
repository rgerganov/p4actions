package com.xakcop.p4actions.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.actions.CloseConsoleAction;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;

public class ProcessConsolePageParticipant implements IConsolePageParticipant {

    @Override
    public void init(IPageBookViewPage page, IConsole console) {
        ProcessConsole processConsole = (ProcessConsole) console;
        TerminateProcessAction terminateAction = processConsole.getTerminateAction();
        CloseConsoleAction closeAction = processConsole.getCloseAction();
        IPageSite site = page.getSite();
        IActionBars actionBars = site.getActionBars();
        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        toolBarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, terminateAction);
        toolBarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, closeAction);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void activated() {
    }

    @Override
    public void deactivated() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }
}