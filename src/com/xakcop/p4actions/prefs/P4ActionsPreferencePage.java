package com.xakcop.p4actions.prefs;

import com.xakcop.p4actions.Activator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class P4ActionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    Table table;

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.WRAP);
        label.setText("You can add custom P4 actions here. Use {cln} as a placeholder for the change list id. " +
                "Use {cln} as a placeholder for the change list id. Use {cln} as a placeholder for the change list id.");
        GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
        data.horizontalSpan = 2;
        data.widthHint = 300;
        label.setLayoutData(data);

        table = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 300;
        data.heightHint = 300;
        data.verticalSpan = 3;
        data.verticalIndent = 5;
        table.setLayoutData(data);

        Button addButton = new Button(composite, SWT.PUSH);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.verticalIndent = 5;
        addButton.setLayoutData(data);
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                AddEditActionDialog dlg = new AddEditActionDialog(shell);
                if (dlg.open() != Window.OK) {
                    return;
                }
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(0, dlg.name);
                item.setText(1, dlg.command);
                table.getColumn(0).pack();
                table.getColumn(1).pack();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        addButton.setText("Add");

        Button editButton = new Button(composite, SWT.PUSH);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.verticalIndent = 5;
        editButton.setLayoutData(data);
        editButton.setText("Edit");

        Button removeButton = new Button(composite, SWT.PUSH);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.verticalIndent = 5;
        removeButton.setLayoutData(data);
        removeButton.setText("Remove");

        String[] titles = { "Name", "Command"};
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }
        for (int i = 0; i < titles.length; i++) {
            table.getColumn(i).pack();
        }
        return composite;
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }
}
