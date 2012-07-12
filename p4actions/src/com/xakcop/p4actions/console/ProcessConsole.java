package com.xakcop.p4actions.console;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.xakcop.p4actions.Action;
import com.xakcop.p4actions.Activator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.actions.CloseConsoleAction;

public class ProcessConsole extends IOConsole {

    private static final String CONSOLE_NAME = "P4 Custom Actions";

    private TerminateProcessAction terminateAction;
    private CloseConsoleAction closeAction;
    private IOConsoleOutputStream consoleOutputStream;

    public ProcessConsole(String name, ImageDescriptor imageDescriptor) {
        super(name, imageDescriptor);
        terminateAction = new TerminateProcessAction();
        closeAction = new CloseConsoleAction(this);
        consoleOutputStream = newOutputStream();
        addPatternMatchListener(new HttpLinkPatternMatcher());
    }

    public void startProcess(Action action, String workingDir, String p4port) {
        List<String> command = action.parseArguments();
        command.add(0, action.getExecutable());
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // http://kb.perforce.com/article/44/symbolic-links-and-workspace-roots
        processBuilder.environment().put("PWD", workingDir);
        processBuilder.environment().put("P4PORT", p4port);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(workingDir));
        try {
            Process process = processBuilder.start();
            onProcessStart(process);
            redirect(process.getInputStream(), consoleOutputStream);
        } catch (IOException e) {
            if (consoleOutputStream.isClosed()) {
                Activator.logError("Error while processing stdout/stderr", e);
            } else {
                e.printStackTrace(new PrintStream(consoleOutputStream));
            }
        } finally {
            onProcessFinish();
        }
    }

    void redirect(InputStream is, OutputStream os) throws IOException {
        byte buff[] = new byte[512];
        while (true) {
            int count = is.read(buff);
            if (count == -1) {
                break;
            }
            os.write(buff, 0, count);
            os.flush();
        }
    }

    void onProcessStart(Process process) {
        Activator.trace("process started");
        terminateAction.setProcess(process);
        terminateAction.setEnabled(true);
        closeAction.setEnabled(false);
    }

    void onProcessFinish() {
        Activator.trace("process ended");
        terminateAction.setEnabled(false);
        closeAction.setEnabled(true);
        try {
            getInputStream().close();
            consoleOutputStream.close();
        } catch (IOException e) {
            Activator.logError("Error closing streams", e);
        }
    }

    public TerminateProcessAction getTerminateAction() {
        return terminateAction;
    }

    public CloseConsoleAction getCloseAction() {
        return closeAction;
    }

    public static ProcessConsole getInstance() {
        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] consoles = manager.getConsoles();
        for (IConsole console : consoles) {
            if (console.getName().equals(CONSOLE_NAME)) {
                manager.removeConsoles(new IConsole[] { console });
            }
        }
        ProcessConsole console = new ProcessConsole(CONSOLE_NAME, null);
        manager.addConsoles(new IConsole[] { console });
        return console;
    }

    private class HttpLinkPatternMatcher implements IPatternMatchListener {

        @Override
        public int getCompilerFlags() {
            return 0;
        }

        @Override
        public String getLineQualifier() {
            return null;
        }

        @Override
        public String getPattern() {
            return "(^|\\s)https?://\\S+";
        }

        @Override
        public void connect(TextConsole console) {
        }

        @Override
        public void disconnect() {
        }

        @Override
        public void matchFound(PatternMatchEvent event) {
            try {
                int offset = event.getOffset();
                int length = event.getLength();
                String url = getDocument().get(offset, length);
                if (url.charAt(0) == ' ') {
                    url = url.trim();
                    offset += 1;
                    length -= 1;
                }
                addHyperlink(new HttpHyperLink(url), offset, length);
            } catch (BadLocationException e) {
                Activator.logError("Bad link location", e);
            }
        }
    }

    private static class HttpHyperLink implements IHyperlink {

        final String url;

        public HttpHyperLink(String url) {
            this.url = url;
        }

        @Override
        public void linkActivated() {
            try {
                IWebBrowser browser =
                        PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                browser.openURL(new URL(url));
            } catch (PartInitException e) {
                Activator.logError("Error opening url", e);
            } catch (MalformedURLException e) {
                Activator.logError("Malformed url", e);
            }
        }

        @Override
        public void linkEntered() {
        }

        @Override
        public void linkExited() {
        }
    }
}
