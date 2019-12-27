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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.github.dkartaschew.galatea.cpugraph.CPUGraph;
import com.github.dkartaschew.galatea.memgraph.MemoryGraph;

/**
 * Snippet for CPU and Memory Graph field.
 */
public class CPUMemoryGraphSnippet {

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).spacing(10, 10).margins(15, 15).create());

		Image imgGC = new Image(display, CPUMemoryGraphSnippet.class.getResourceAsStream("/icons/user-trash.png"));

		final CPUGraph cpu = new CPUGraph(shell, SWT.BORDER, 1000);
		cpu.setPoints(20);
		cpu.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		cpu.run();

		final Button startThread = new Button(shell, SWT.NONE);
		startThread.setText("Run Task");
		startThread.setLayoutData(GridDataFactory.fillDefaults().create());

		final Label result = new Label(shell, SWT.NONE);
		result.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		final Random rnd = new Random();
		startThread.addListener(SWT.Selection, e -> {
			Thread th = new Thread(() -> {
				// Generate CPU load by MD5 calc of data buffer.
				byte[] data = new byte[10_000_000];
				rnd.nextBytes(data);
				try {
					MessageDigest digest = MessageDigest.getInstance("MD5");
					for (int i = 0; i < 10; i++) {
						digest.update(data);
					}
					display.asyncExec(() -> result.setText(Arrays.toString(digest.digest())));
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}

			});
			th.setDaemon(true);
			th.start();
		});

		final MemoryGraph mem = new MemoryGraph(shell, SWT.BORDER, imgGC, 1000);
		mem.setPoints(20);
		mem.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		mem.run();

		shell.pack();
		shell.open();

		while (!shell.isDisposed() && !mem.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
