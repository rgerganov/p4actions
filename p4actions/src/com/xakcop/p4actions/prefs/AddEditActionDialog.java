package com.xakcop.p4actions.prefs;

import com.xakcop.p4actions.Action;
import com.xakcop.p4actions.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddEditActionDialog extends StatusDialog implements ModifyListener {

    private Text nameText;
    private Text cmdText;
    private Text argText;
    Action action;

    public AddEditActionDialog(Shell parent, Action action) {
        super(parent);
        this.action = action;
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
        layout.numColumns = 3;
        inner.setLayout(layout);

        Label nameLabel = new Label(inner, SWT.WRAP);
        nameLabel.setText("Name:");

        nameText = new Text(inner, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.widthHint = 200;
        data.horizontalSpan = 2;
        nameText.setLayoutData(data);
        nameText.setText(action.getName());
        nameText.addModifyListener(this);

        Label cmdLabel = new Label(inner, SWT.WRAP);
        cmdLabel.setText("Executable:");

        cmdText = new Text(inner, SWT.SINGLE | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.widthHint = 200;
        cmdText.setLayoutData(data);
        cmdText.setText(action.getExecutable());
        cmdText.addModifyListener(this);
        if (action.getName().isEmpty() || action.getExecutable().isEmpty()) {
            updateStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ""));
        }

        Button btn = new Button(inner, SWT.NONE);
        btn.setText("Browse...");

        Label argLabel = new Label(inner, SWT.WRAP);
        argLabel.setText("Arguments:");

        argText = new Text(inner, SWT.SINGLE | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.widthHint = 200;
        data.horizontalSpan = 2;
        argText.setLayoutData(data);
        argText.setText(action.getArguments());
        argText.setToolTipText("Use {cln} as a placeholder for the changeset number");
        argText.addModifyListener(this);

        Label noteLabel = new Label(inner, SWT.WRAP);
        noteLabel.setText("Use {cln} as a placeholder for the changeset number.\n"
                + "Use double quotes to enclose arguments that contain spaces.");
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.verticalIndent = 10;
        data.horizontalSpan = 3;
        noteLabel.setLayoutData(data);

        return composite;
    }

    @Override
    public void modifyText(ModifyEvent e) {
        IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, "");
        action.setName(nameText.getText());
        action.setExecutable(cmdText.getText());
        action.setArguments(argText.getText());
        if (action.getName().isEmpty()) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Name cannot be empty");
        } else if (action.getName().startsWith(" ")) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid name");
        } else if (action.getExecutable().isEmpty()) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Executable cannot be empty");
        } else if (action.getExecutable().startsWith(" ")
                || action.getExecutable().endsWith(" ")) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid executable");
        }
        updateStatus(status);
    }
}
