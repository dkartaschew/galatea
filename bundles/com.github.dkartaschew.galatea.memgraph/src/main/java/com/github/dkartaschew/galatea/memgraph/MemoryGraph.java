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
package com.github.dkartaschew.galatea.memgraph;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.github.dkartaschew.galatea.graph.Graph;

public class MemoryGraph extends Composite implements Runnable {

	/**
	 * The graph implementation
	 */
	private final Graph scope;
	/**
	 * The toolbar for the button
	 */
	private final ToolBar toolBar;
	/**
	 * The GC button
	 */
	private final ToolItem btnGC;

	/**
	 * JVM runtime.
	 */
	private final Runtime runtime = Runtime.getRuntime();

	/**
	 * Delay for each sample
	 */
	private final int delay;
	/**
	 * 1MB const
	 */
	private final static double MB = 1024 * 1024;

	/**
	 * Create the composite.
	 * 
	 * @param parent   The parent composite.
	 * @param style    The default style
	 * @param gcButton The image for the GC Button
	 * @param delay    The sample delay (msec)
	 */
	public MemoryGraph(Composite parent, int style, Image gcButton, int delay) {
		super(parent, style);
		this.delay = delay > 0 ? delay : 500;
		setLayout(GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).create());

		scope = new Graph(this, SWT.BORDER);
		scope.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 24).create());
		scope.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
		scope.setFontColor(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(GridDataFactory.fillDefaults().create());

		btnGC = new ToolItem(toolBar, SWT.NONE);
		btnGC.setImage(gcButton);
		btnGC.addListener(SWT.Selection, e -> System.gc());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Get the sample delay
	 * 
	 * @return The sample delay in msec.
	 */
	public int getDelay() {
		return delay;
	}

	@Override
	public void run() {
		if (isDisposed()) {
			return;
		}
		long freeHeap = runtime.freeMemory();
		long heap = runtime.totalMemory();
		scope.addValue((double) ((heap - freeHeap) * 100d) / (double) heap);
		scope.setToolTipText(String.format("%.2fMiB / %.2fMiB", (double) (heap - freeHeap) / MB, (double) heap / MB));
		scope.setText(String.format("%.2fMiB", (double) (heap - freeHeap) / MB));
		scope.redraw();
		getDisplay().timerExec(getDelay(), this);
	}

	/**
	 * Set the number of points to catch/display
	 * 
	 * @param points The number of points to display.
	 */
	public void setPoints(int points) {
		scope.setPoints(points);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		scope.setFont(font);
	}

	/**
	 * Set the font color
	 * 
	 * @param color The font color
	 */
	public void setFontColor(Color color) {
		scope.setFontColor(color);
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		scope.setForeground(color);
		toolBar.setForeground(color);
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		scope.setBackground(color);
		toolBar.setBackground(color);
	}

}
