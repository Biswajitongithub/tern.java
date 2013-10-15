package tern.eclipse.swt.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tern.Server;
import tern.TernDef;
import tern.doc.IJSDocument;
import tern.eclipse.jface.TernContentProposalProvider;
import tern.eclipse.swt.JSDocumentText;

public class TernEditorWithFiles {

	private CTabFolder tabFolder;
	private Server server;

	public static void main(String[] args) {
		TernEditorWithFiles editor = new TernEditorWithFiles();
		try {
			editor.createUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createUI() throws IOException {

		this.server = new Server();
		server.addDef(TernDef.browser);
		server.addDef(TernDef.ecma5);

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(500, 500);
		shell.setText("Tern SWT Eclipse");
		shell.setLayout(new GridLayout(2, true));

		final Button saveButton = new Button(shell, SWT.PUSH);
		saveButton.setText("Save");
		saveButton.setEnabled(false);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		saveButton.setLayoutData(data);

		createTreeFiles(shell);
		createEditorsArea(shell);

		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// editor.setDirty(false);
			}
		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void createTreeFiles(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));

		final TreeViewer tv = new TreeViewer(composite);
		tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		tv.setContentProvider(new FileTreeContentProvider());
		tv.setLabelProvider(new FileTreeLabelProvider());

		tv.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (event.getSelection().isEmpty()) {
					return;
				}
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					File file = (File) selection.getFirstElement();
					createEditor(tabFolder, file, server);
				}
			}
		});

		tv.setInput(new File("scripts")); // pass a non-null that will be
											// ignored
	}

	private void createEditorsArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));

		this.tabFolder = new CTabFolder(composite, SWT.TOP);
		tabFolder.setBorderVisible(true);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createEditor(CTabFolder tabFolder, File file, Server server) {

		CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
		tab.setText(file.getName());

		String js = "";
		try {
			js = readFile(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Text text = new Text(tabFolder, SWT.MULTI | SWT.BORDER);
		text.setText(js);

		tab.setControl(text);

		IJSDocument document = new JSDocumentText(file.getName(), server, text);

		// Les charact�res qui d�clenchent l'autocompl�tion
		char[] autoActivationCharacters = new char[] { '.' };
		// La combinaison de touches qui d�clenche l'autocompl�tion
		KeyStroke keyStroke = null;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// La vraie chose !
		ContentProposalAdapter adapter = new ContentProposalAdapter(text,
				new TextContentAdapter(), new TernContentProposalProvider(
						document), keyStroke, autoActivationCharacters);
		// adapter.setLabelProvider(TernLabelProvider.getInstance());
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		return stringBuilder.toString();
	}
}
