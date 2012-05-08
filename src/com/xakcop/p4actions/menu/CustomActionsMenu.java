package com.xakcop.p4actions.menu;

import java.util.HashMap;
import java.util.Map;

import com.perforce.team.core.p4java.P4DefaultChangelist;
import com.perforce.team.core.p4java.P4PendingChangelist;
import com.perforce.team.ui.views.PendingView;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

public class CustomActionsMenu extends CompoundContributionItem {

    // must match the params of the command in plugin.xml
    public static final String ID_PARAM = "p4cl.id";
    public static final String CONN_ADDR_PARAM = "p4cl.conn.addr";
    public static final String CLIENT_NAME_PARAM = "p4cl.client.name";
    public static final String CLIENT_ROOT_PARAM = "p4cl.client.root";

    @Override
    protected IContributionItem[] getContributionItems() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPart activePart = window.getPartService().getActivePart();
        if (!(activePart instanceof PendingView)) {
            return new IContributionItem[0];
        }
        PendingView pv = (PendingView) activePart;
        IStructuredSelection selection = (IStructuredSelection) pv.getViewer().getSelection();
        if (selection.size() != 1) {
            return new IContributionItem[0];
        }
        Object sel = selection.getFirstElement();
        if (sel instanceof P4DefaultChangelist) {
            return new IContributionItem[0];
        }
        if (sel instanceof P4PendingChangelist) {
            P4PendingChangelist changeList = (P4PendingChangelist) sel;
            CommandContributionItemParameter params = new CommandContributionItemParameter(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                    "p4actions.contributionItem",
                    "p4actions.command",
                    SWT.NONE);
            params.parameters = extractParameters(changeList);
            params.label = "Echo";
            CommandContributionItem cci = new CommandContributionItem(params);
            return new IContributionItem[] { cci };
        } else {
            return new IContributionItem[0];
        }
    }

    Map<String, String> extractParameters(P4PendingChangelist changeList) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ID_PARAM, "" + changeList.getId());
        params.put(CLIENT_ROOT_PARAM, changeList.getClient().getRoot());
        params.put(CLIENT_NAME_PARAM, changeList.getClientName());
        params.put(CONN_ADDR_PARAM, changeList.getConnection().getAddress());
        return params;
    }
}
