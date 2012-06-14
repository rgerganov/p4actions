package com.xakcop.p4actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.xakcop.p4actions";
    public static final String ACTIONS_PREF_KEY = "p4actions";

    private static Activator plugin;

    public Activator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static void logError(String msg, Throwable t) {
        plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, msg, t));
    }

    public static void trace(String msg) {
        if (plugin.isDebugging()) {
            System.out.println("[p4actions] " + msg);
        }
    }

    public static Activator getDefault() {
        return plugin;
    }

    public static Map<String, String> getAllActions() {
        Map<String, String> result = new HashMap<String, String>();
        IPreferenceStore store = getDefault().getPreferenceStore();
        String allActions = store.getString(ACTIONS_PREF_KEY);
        if (allActions.isEmpty()) {
            return result;
        }
        String[] actions = allActions.split("\n");
        for (String action : actions) {
            String[] nameCmd = action.split("\t");
            if (nameCmd.length != 2) {
                logError("Invalid properties for p4actions", null);
                continue;
            }
            result.put(nameCmd[0], nameCmd[1]);
        }
        return result;
    }

    public static void saveAllActions(Map<String, String> actions) {
        IPreferenceStore store = getDefault().getPreferenceStore();
        StringBuilder allActions = new StringBuilder();
        for (Map.Entry<String, String> entry : actions.entrySet()) {
            allActions.append(entry.getKey());
            allActions.append("\t");
            allActions.append(entry.getValue());
            allActions.append("\n");
        }
        store.setValue(ACTIONS_PREF_KEY, allActions.toString());
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
