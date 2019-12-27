/*-
 * Galatea SWT Widgets
 * Copyright (C) 2017-2019 Darran Kartaschew 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 */
package com.github.dkartaschew.galatea.snippets;

import java.io.File;
import java.util.concurrent.Callable;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.github.dkartaschew.galatea.actiontext.ActionText;

/**
 * Snippet for ActionText field.
 */
public class ActionTextSnippet {

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).spacing(10, 10).margins(15, 15).create());

		final Label lbl1 = new Label(shell, SWT.NONE);
		lbl1.setText("Sample Question Selection:");
		lbl1.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());

		Image imageMenu = new Image(display, ActionTextSnippet.class.getResourceAsStream("/icons/open-menu.png"));
		Image imageFile = new Image(display, ActionTextSnippet.class.getResourceAsStream("/icons/file.png"));
		Image imageFolder = new Image(display, ActionTextSnippet.class.getResourceAsStream("/icons/folder.png"));

		Callable<String> actionDialog = new Callable<String>() {

			@Override
			public String call() throws Exception {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				dialog.setText("My info");
				dialog.setMessage("Do you really want to do this?");

				// open dialog and await user selection
				int returnCode = dialog.open();
				return returnCode == SWT.YES ? "Yes" : "No";
			}
		};

		final ActionText textField = new ActionText(shell, SWT.NONE, imageMenu, actionDialog);
		textField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(250, SWT.DEFAULT).create());
		textField.setMessage("I need an answer");
		textField.addListener(SWT.Modify, e -> System.out.println(e.text));

		final Label lbl2 = new Label(shell, SWT.NONE);
		lbl2.setText("Sample File Selection:");
		lbl2.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());

		Callable<String> actionFile = new Callable<String>() {

			@Override
			public String call() throws Exception {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath(System.getProperty("user.home"));
				String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
				fd.setFilterExtensions(filterExt);
				return fd.open();
			}
		};

		final ActionText textFieldFile = new ActionText(shell, SWT.NONE, imageFile, actionFile);
		textFieldFile.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		textFieldFile.setMessage("I need a File");
		textFieldFile.addListener(SWT.Modify, e -> System.out.println(e.text));

		final Label lbl3 = new Label(shell, SWT.NONE);
		lbl3.setText("Sample Folder Selection:");
		lbl3.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());

		Callable<String> actionFolder = new Callable<String>() {

			@Override
			public String call() throws Exception {
				DirectoryDialog fd = new DirectoryDialog(shell, SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath(System.getProperty("user.home"));
				return fd.open();
			}
		};

		final ActionText textFieldFolder = new ActionText(shell, SWT.NONE, imageFolder, actionFolder);
		textFieldFolder.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		textFieldFolder.setMessage("I need a Folder");
		ControlDecoration dec = new ControlDecoration(textFieldFolder, SWT.TOP | SWT.CENTER);
		dec.setImage(display.getSystemImage(SWT.ICON_WARNING));
		dec.hide();
		dec.setDescriptionText("Folder does not exist or is not readable and writable");
		textFieldFolder.addListener(SWT.Modify, e -> {
			String folder = textFieldFolder.getText();
			File f = new File(folder);
			if (!f.canRead() && !f.canWrite()) {
				dec.show();
			} else {
				dec.hide();
			}
		});

		final Label elbl1 = new Label(shell, SWT.NONE);
		elbl1.setText("NonEditable Question Selection:");
		elbl1.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());

		Callable<String> eactionDialog = new Callable<String>() {

			@Override
			public String call() throws Exception {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				dialog.setText("My info");
				dialog.setMessage("Do you really want to do this?");

				// open dialog and await user selection
				int returnCode = dialog.open();
				return returnCode == SWT.YES ? "Yes" : "No";
			}
		};

		final ActionText etextField = new ActionText(shell, SWT.NONE, imageMenu, eactionDialog);
		etextField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		etextField.setMessage("I need an answer");
		etextField.addListener(SWT.Modify, e -> System.out.println(e.text));
		etextField.setEditable(false);

		final Label dlbl1 = new Label(shell, SWT.NONE);
		dlbl1.setText("Disabled Question Selection:");
		dlbl1.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());

		Callable<String> dactionDialog = new Callable<String>() {

			@Override
			public String call() throws Exception {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				dialog.setText("My info");
				dialog.setMessage("Do you really want to do this?");

				// open dialog and await user selection
				int returnCode = dialog.open();
				return returnCode == SWT.YES ? "Yes" : "No";
			}
		};

		final ActionText dtextField = new ActionText(shell, SWT.NONE, imageMenu, dactionDialog);
		dtextField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		dtextField.setMessage("I need an answer");
		dtextField.addListener(SWT.Modify, e -> System.out.println(e.text));
		dtextField.setEnabled(false);

		final Label dlbl2 = new Label(shell, SWT.NONE);
		dlbl2.setText("Regular Text Field:");
		dlbl2.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
		final Text textFieldOrig = new Text(shell, SWT.BORDER);
		textFieldOrig.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		textFieldOrig.setMessage("Regular Text Field");
		
		shell.pack();
		shell.open();

		while (!shell.isDisposed() && !textField.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
