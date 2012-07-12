package com.xakcop.p4actions.prefs;

import java.util.LinkedList;
import java.util.List;

import com.xakcop.p4actions.Action;
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

public class P4ActionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener {

    Table table;
    Button addButton;
    Button editButton;
    Button removeButton;

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.WRAP);
        label.setText("Custom actions that will appear in the context menu of any P4 pending changeset:");
        GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
        data.horizontalSpan = 2;
        data.widthHint = 300;
        label.setLayoutData(data);

        table = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 300;
        data.heightHint = 300;
        data.verticalSpan = 3;
        data.verticalIndent = 5;
        table.setLayoutData(data);
        table.addSelectionListener(this);

        addButton = new Button(composite, SWT.PUSH);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.verticalIndent = 5;
        addButton.setLayoutData(data);
        addButton.setText("Add");
        addButton.addSelectionListener(this);

        editButton = new Button(composite, SWT.PUSH);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.verticalIndent = 5;
        editButton.setLayoutData(data);
        editButton.setText("Edit");
        editButton.addSelectionListener(this);
        editButton.setEnabled(false);

        removeButton = new Button(composite, SWT.PUSH);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        data.verticalIndent = 5;
        removeButton.setLayoutData(data);
        removeButton.setText("Remove");
        removeButton.addSelectionListener(this);
        removeButton.setEnabled(false);

        String[] titles = {"Name"};
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
        }
        initializeValues();
        return composite;
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.widget == table) {
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
        } else if (e.widget == addButton) {
            onAdd();
        } else if (e.widget == removeButton) {
            onRemove();
        } else if (e.widget == editButton) {
            onEdit();
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    @Override
    public boolean performOk() {
        List<Action> actions = new LinkedList<Action>();
        TableItem[] items = table.getItems();
        for (int i = 0 ; i < items.length ; i++) {
            Action action = (Action) items[i].getData();
            //System.out.println(action.toString());
            actions.add(action);
        }
        Activator.saveAllActions(actions);
        return true;
    }

    void initializeValues() {
        List<Action> actions = Activator.getAllActions();
        for (Action action : actions) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setData(action);
            item.setText(0, action.getName());
            table.getColumn(0).pack();
        }
    }

    void onEdit() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        TableItem item = table.getSelection()[0];
        Action action = (Action) item.getData();
        AddEditActionDialog dlg = new AddEditActionDialog(shell, new Action(action));
        if (dlg.open() != Window.OK) {
            return;
        }
        action = dlg.action;
        item.setData(action);
        item.setText(0, action.getName());
        table.getColumn(0).pack();
    }

    void onRemove() {
        table.remove(table.getSelectionIndices());
        editButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    void onAdd() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        AddEditActionDialog dlg = new AddEditActionDialog(shell, new Action());
        if (dlg.open() != Window.OK) {
            return;
        }
        TableItem item = new TableItem(table, SWT.NONE);
        Action action = dlg.action;
        item.setData(action);
        item.setText(0, action.getName());
        table.getColumn(0).pack();
    }
}
