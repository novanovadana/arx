package org.deidentifier.arx.r.terminal;

import java.io.IOException;
import org.deidentifier.arx.r.OS;
import org.deidentifier.arx.r.RBuffer;
import org.deidentifier.arx.r.RIntegration;
import org.deidentifier.arx.r.RListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * R terminal
 * 
 * @author Fabian Prasser
 * @author Dana Novanova
 * @author Alisa Fedorenko
 */
public class TestTerminal {

	/** Buffer size */
	private static final int BUFFER_SIZE = 10000;
	/** Event delay */
	private static final int EVENT_DELAY = 10;

	/**
	 * Creates a new shell within the given control
	 * 
	 * @param shell
	 * @throws IOException
	 */
	public TestTerminal(Composite parent) throws IOException {
		// Display
		Display terminalDisplay = parent.getDisplay();

		// Folder
		TabFolder folder = new TabFolder(parent, SWT.NULL);

		// Tabs
		final TestTerminalTab tabTerminal = new TestTerminalTab(folder);
		final RSetupTab tabSetup = new RSetupTab(folder);

		// Item 1
		TabItem item1 = new TabItem(folder, SWT.NULL);
		item1.setText("Terminal");
		item1.setControl(tabTerminal.getControl());

		// Item 2
		TabItem item2 = new TabItem(folder, SWT.NULL);
		item2.setText("Setup");
		item2.setControl(tabSetup.getControl());

		// R integration
		final RBuffer buffer = new RBuffer(BUFFER_SIZE);
		final RListener listener = new RListener(EVENT_DELAY, terminalDisplay) {

			@Override
			public void bufferUpdated() {
				tabTerminal.setOutput(buffer.toString());
			}

			@Override
			public void closed() {
				// TODO: Handle
			}
		};

		// Start integration
		final RIntegration r = new RIntegration(OS.getR(), buffer, listener);

		// Redirect user input
		tabTerminal.setCommandListener(new RCommandListener() {
			@Override
			public void command(String command) {
				r.execute(command);
			}
		});
	}
}