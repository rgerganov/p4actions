package com.xakcop.p4actions.prefs;

import com.xakcop.p4actions.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddEditActionDialog extends StatusDialog implements ModifyListener {

    private Text nameText;
    private Text cmdText;
    String name = "";
    String command = "";

    public AddEditActionDialog(Shell parent) {
        super(parent);
        setTitle("P4 Custom Action");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite= (Composite) super.createDialogArea(parent);
        Composite inner= new Composite(composite, SWT.NONE);
        inner.setFont(composite.getFont());

        GridLayout layout= new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.numColumns = 2;
        inner.setLayout(layout);

        Label nameLabel = new Label(inner, SWT.WRAP);
        nameLabel.setText("Name:");

        nameText = new Text(inner, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.widthHint = 200;
        nameText.setLayoutData(data);
        nameText.setText(name);
        nameText.addModifyListener(this);

        Label cmdLabel = new Label(inner, SWT.WRAP);
        cmdLabel.setText("Command:");

        cmdText = new Text(inner, SWT.SINGLE | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.widthHint = 200;
        cmdText.setLayoutData(data);
        cmdText.setText(command);
        cmdText.addModifyListener(this);
        if (name.isEmpty() || command.isEmpty()) {
            updateStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ""));
        }
        return composite;
    }

    @Override
    public void modifyText(ModifyEvent e) {
        IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, "");
        name = nameText.getText();
        command = cmdText.getText();
        if (name.isEmpty()) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Name cannot be empty");
        } else if (name.startsWith(" ")) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid name");
        } else if (command.isEmpty()) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Command cannot be empty");
        } else if (command.startsWith(" ")) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid command");
        }
        updateStatus(status);
    }
}
