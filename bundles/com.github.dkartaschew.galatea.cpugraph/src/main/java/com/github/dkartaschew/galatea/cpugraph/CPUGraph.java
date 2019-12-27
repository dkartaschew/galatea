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
package com.github.dkartaschew.galatea.cpugraph;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

import com.github.dkartaschew.galatea.graph.Graph;

public class CPUGraph extends Composite implements Runnable {

	/**
	 * The graph implementation
	 */
	private final Graph scope;

	/**
	 * JVM process bean.
	 */
	private final OperatingSystemMXBean bean;

	/**
	 * Delay for each sample
	 */
	private final int delay;

	/**
	 * Create the composite.
	 * 
	 * @param parent The parent composite.
	 * @param style  The default style
	 * @param delay  The sample delay (msec)
	 */
	public CPUGraph(Composite parent, int style, int delay) {
		super(parent, style);
		this.delay = delay > 0 ? delay : 500;
		setLayout(GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).create());

		scope = new Graph(this, SWT.BORDER);
		scope.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 24).create());
		scope.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
		scope.setFontColor(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		bean = ManagementFactory.getOperatingSystemMXBean();
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
		double load = bean.getSystemLoadAverage();
		try {
			if (bean instanceof com.sun.management.OperatingSystemMXBean) {
				load = ((com.sun.management.OperatingSystemMXBean) bean).getProcessCpuLoad() * 100.0d;
			}
		} catch (Throwable e) {
			// Ignore possible class loader errors.
		}
		scope.addValue(load);
		String text = String.format("%.2f%%", load);
		scope.setToolTipText(text);
		scope.setText(text);
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
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		scope.setBackground(color);
	}

}
