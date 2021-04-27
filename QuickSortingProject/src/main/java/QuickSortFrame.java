package main.java;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class QuickSortFrame extends JFrame { // The frame for the visual components.
	private int values[], amount, lastSize, // array for the random values and its size
			lastLow, lastHigh, // positions of the low and high elements
			delay = 500, // delay of the animation
			x = 30, y = 50, row; // positions for the buttons on the screen
	private static Color colors[] = new Color[6]; // colors to paint the buttons
	static final int colorCodes[] = // white, green, cherry, purple, blue, light-green
			{ 0xffffff, 0xf5b43, 0x841126, 0x56324e, 0x29519a, 0x1e8c36 };
	static {
		for (int i = 0; i < 6; i++)
			colors[i] = new Color(colorCodes[i]);
	}
	private static final Border border = BorderFactory.createLineBorder(colors[2], 3),
			border2 = BorderFactory.createLineBorder(colors[4], 1);
	private boolean isInit = true, isAscending = true; // controlling variables
	private final JTextField countField = new JTextField(4);
	private final JLabel howMany = new JLabel("How many numbers to display?"), // informational and
			between = new JLabel("Please enter a number between 0 and 100."), // warning messages
			bound = new JLabel("Please select a value smaller or equal to 30.");
	private final JButton enterButton = new JButton("Enter"), resetButton = new JButton("Reset"),
			sortButton = new JButton("Sort"); // option buttons
	private ArrayList<JButton> buttons = new ArrayList<>(); // list of the numbered buttons
	private final JPanel enterPanel = new JPanel(), sortPanel = new JPanel(), // main and sort screens
			optionsPanel = new JPanel(), grid = new JPanel(); // the panel to place the numbered buttons
	private Thread thread; // the thread which performs the animation
	private ActionListener gridLstnr; // the listener to process the button clicking

	QuickSortFrame(String name, int w, int h) // constructor to create the main frame
	{
		super(name); // calls the parent constructor of JFrame to set the name
		setSize(1300, 700); // sets the size of the main frame
		gridLstnr = (ActionEvent e) -> // sets the handler of the numbered button click
		{ // gets the value from the source which is the pressed button
			int selected = Integer.valueOf(((JButton) e.getSource()).getText());
			if (selected > 30) { // if the value is bigger than 30,
				bound.setVisible(true); // displays the warning message
			} else { // in other case - displays numbered buttons in selected amount,
				sortButton.setEnabled(selected > 1);
				resetArray(selected, true); // their amount is the value of the button
				bound.setVisible(false);
			} // and resets all the buttons values
		};
		setBackground(colors[0]);
	}

	public static void main(String[] args) { // starting point of the program file
		QuickSortFrame frame = new QuickSortFrame("Swing Quicksort Project", 1300, 700);
		frame.setAll(); // creates new frame and sets all its widgets parameters
	}

	public void setAll() { // sets all the parameters for most of the widgets
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setParameters(enterPanel, this, 1300, 700, 0, true); // such as size, color,
		setParameters(sortPanel, this, 1300, 700, 0, false); // visibility and
		setParameters(grid, sortPanel, 130, 700, 0, true); // place to attach
		setParameters(optionsPanel, sortPanel, 250, 500, 50, 50, 0, -1, true, null);
		setParameters(bound, optionsPanel, 250, 40, 0, 150, -1, -1, false, null);
		setParameters(howMany, enterPanel, 300, 20, 500, 200, -1, -1, true, null);
		setParameters(between, enterPanel, 350, 20, 480, 240, -1, -1, false, null);
		setParameters(countField, enterPanel, 100, 30, 535, 290, -1, -1, true, null);
		countField.setHorizontalAlignment(JTextField.CENTER);
		countField.setBorder(border2);
		setParameters(enterButton, enterPanel, 90, 30, 540, 350, 4, 0, true, (ActionEvent e) -> {
			// The button to enter the buttons amount
			isAscending = true; // changes the sorting order
			String input = countField.getText(); // reads the input text
			if (input != null) // validation of the entered value
				if (input.matches("\\d{1,3}")) {
					int number = Integer.parseInt(input);
					if (number <= 100) // it must be integer value between 0 and 100
					{
						between.setVisible(false); // not displaying the warning message
						resetArray(number, true); // resets all the button values
						sortButton.setEnabled(number > 1);
						enterPanel.setVisible(false); // switches from the main screen
						sortPanel.setVisible(true); // to the sort screen
						return;
					}
				} // in case of the incorrect input shows the warning message
			between.setVisible(true);
		});
		// The button to sort the numbered buttons
		setParameters(sortButton, optionsPanel, 100, 40, 30, 5, 5, 0, true, (ActionEvent e) -> {
			bound.setVisible(false); // warning message is not needed
			cancel(values.length < 2);
			isAscending = !isAscending; // changes the order of sorting to opposite
			if (values.length < 2) // no need to sort the array
				return; // if its length is less than 2
			lastLow = 0; // stores the array bounds
			lastHigh = amount - 1; // to use it for repainting buttons
			thread = new Thread(() -> { // this thread performs sorting
				try {
					repaintButtons(1, 0, false, false); // paints all buttons green on start
					quickSort(0, amount - 1); // calls the sorting method
					repaintButtons(4, 0, false, true);
					// repaints all the buttons on finish into their original state
				} catch (InterruptedException e1) {
				}
			});
			thread.start(); // the thread starts
		});
		// The button to reset array with new amount of buttons
		setParameters(resetButton, optionsPanel, 100, 40, 30, 70, 5, 0, true, (ActionEvent e) -> {
			cancel(true);
			sortPanel.setVisible(false); // switches from the sort screen
			enterPanel.setVisible(true); // to the enter screen
			bound.setVisible(false);
		}); // no need for the warning message
		setVisible(true); // displays the main screen
	}

	// The method to set all the parameters for the visual component
	void setParameters(Component item, Component place, int w, int h, int x, int y, int bgc, int fgc, boolean vis,
			ActionListener lstnr) {
		if (x > -1)
			item.setLocation(x, y); // sets its coordinates on the screen
		if (fgc > -1)
			item.setForeground(colors[fgc]); // sets its foreground color
		if (lstnr != null) // sets the listener to handle the button clicking
			if (item instanceof AbstractButton) {
				((AbstractButton) item).addActionListener(lstnr);
				((AbstractButton) item).setFocusPainted(false);
			} else if (item instanceof JTextField)
				((JTextField) item).addActionListener(lstnr);
		setParameters(item, place, w, h, bgc, vis);
	} // calls the overloaded method

	void setParameters(Component item, Component place, int w, int h, int bgc, boolean vis) {
		item.setSize(w, h); // sets the pixel size of the element
		if (place != null) // attaches the element to its place
			((Container) place).add(item);
		if (item instanceof JPanel) // layout manager is not used
			((JPanel) item).setLayout(null);
		if (bgc > -1) // sets the background color
			item.setBackground(colors[bgc]);
		item.setVisible(vis); // shows or hides the element on the screen
	}

	// The method to reset array, renew all the numbered buttons with new amount
	void resetArray(int count, boolean repaint) { // of random values and to update the buttons on screen
		cancel(repaint);
		lastSize = amount; // amount of the numbered buttons
		amount = count; // amount gets a new value
		if (isInit || lastSize != amount) // if the amount of buttons changed,
			values = new int[amount]; // changes the array size
		if (amount > 0) {
			for (int i = 0; i < amount; i++) // fills the array with random numbers
				values[i] = (int) (Math.random() * 1000) + 1; // between 1 and 1000
			int some = (int) (Math.random() * amount); // one of the button values
			if (values[some] > 30) // must be equals or less than 30
				values[some] = (int) (Math.random() * 30) + 1; // reset it in other case
			if (isInit) // if it is the first launch of the sorting,
				buttons = new ArrayList<JButton>(amount); // creates the buttons list
		} // creating the buttons list
		if (isInit || lastSize != amount)
			changeButtonsCount(); // resets the buttons view on the screen
		isInit = false;
		for (int i = 0; i < amount; i++) // sets the values of the buttons with the array values
			buttons.get(i).setText(values[i] + "");
		grid.setSize(100 * ((amount + 9) / 10) + 50, 700); // changes the panel size
		grid.setVisible(true); // displays the buttons panel
		optionsPanel.setLocation(grid.getWidth() + 20, 50);
	} // relocates the options panel next to the grid

	private void changeButtonsCount() // updates the buttons view on the screen
	{
		if (amount < lastSize) // if it need to remove some buttons
			for (int i = lastSize - 1; i >= amount; i--)
				buttons.get(i).setVisible(false); // hides them from the screen
		else
			for (int i = lastSize; i < amount; i++) {
				// in other case when some buttons need to be added
				if (i >= buttons.size()) // if they are not created yet
				{
					if (row == 10) { // if there are 10 buttons in the column,
						row = 0; // starts a new column
						y = 50; // with new locations
						x += 100;
					}
					JButton bttn = new JButton(); // creates the new button,
					bttn.setText(values[i] + ""); // sets its value
					buttons.add(bttn); // and stores it in the button list
					bttn.setBorder(border); // sets border for it,
					bttn.setBorderPainted(false); // which is not painted yet
					setParameters(bttn, grid, 90, 35, x, y, 4, 0, true, gridLstnr);
					row++;
					y += 50;
				} else
					buttons.get(i).setVisible(true); // shows button if it exists
			}
	}

	// The method to perform the quick sorting algorithm.
	public void quickSort(int low, int high) throws InterruptedException {
		int sG = low, fG = high + 1, sB = lastLow, fB = lastHigh + 1, speedInfo = 0, fP = 0;
		boolean isWithin = false, hasGreenMiddle = false;
		if (!(low == lastLow && high == lastHigh)) {
			isWithin = (!(low < lastLow ^ high > lastHigh));
			if (isWithin) // repaints the processed subarray buttons green
			{
				hasGreenMiddle = sG > sB || fB > fG; // and not used ones - blue
				speedInfo = hasGreenMiddle ? sG : sB;
				fP = hasGreenMiddle ? fB : fG;
			} else if (high > lastHigh) {
				sG = lastHigh + 1;
				fB = low;
			} else {
				fG = lastLow;
				sB = high + 1;
			}
			if (isWithin) {
				for (int i = (hasGreenMiddle ? sB : sG); i < fP; i++) {
					if (i == speedInfo) {
						i = hasGreenMiddle ? fG : fB;
						if (i >= values.length)
							break;
					}
					buttons.get(i).setBackground(hasGreenMiddle ? colors[4] : colors[1]);
				}
			} else {
				for (int i = sG; i < fG; i++)
					buttons.get(i).setBackground(colors[1]);
				for (int i = sB; i < fB; i++)
					buttons.get(i).setBackground(colors[4]);
			}
		}
		lastLow = low; // remembers the current sorting bounds
		lastHigh = high; // to use them for the further repainting
		if (values.length == 0 || low >= high)
			return; // returns if it is not possible to divide the array
		Thread.sleep(delay); // calls the delay to show the subarray for sorting
		int middle = low + (high - low) / 2; // chooses the pivot element
		int bearing = values[middle]; // the value of the pivot element
		buttons.get(middle).setBackground(colors[3]); // paints the pivot purple
		int i = low, j = high, temp; // iterating starts from this bounds
		boolean isI = false; // shows if the middle element is i or j
		Thread.sleep(delay); // displays the chosen pivot with the delay

		while (i <= j) { // search for the elements that need to swap
			while ((isAscending ? (values[i] < bearing) : (values[i] > bearing))) {
				buttons.get(i).setBorderPainted(true);
				Thread.sleep(delay);
				buttons.get(i).setBorderPainted(false);
				i++;
			}
			buttons.get(i).setBorderPainted(true);
			while ((isAscending ? (values[j] > bearing) : (values[j] < bearing))) {
				buttons.get(j).setBorderPainted(true);
				Thread.sleep(delay);
				buttons.get(j).setBorderPainted(false);
				j--;
			}
			buttons.get(i).setBorderPainted(false);
			if ((isI = i == middle) || (j == middle)) // if it equals to pivot
			{
				while (i < j) // setting it upper or lower than pivot
				{
					if (isAscending ? values[j] < values[i] : values[j] > values[i]) {
						highlight(i, j, true); // highlight it and the pivot
						highlight(i, j, false);
						temp = values[isI ? j : i];
						for (int t = isI ? j : i; isI ? t > i : t < j; t += (isI ? -1 : 1)) {
							values[t] = values[isI ? t - 1 : t + 1];
							buttons.get(t).setText(values[t] + "");
						} // offsets the other elements from the pivot to the moved element place
						values[middle] = temp; // moved element is on the middle position
						JButton b = buttons.get(middle);
						b.setText(temp + "");
						for (int t = 1; t < 4; t += 2, b = buttons.get(isI ? i + 1 : j - 1)) {
							b.setBackground(colors[t]); // repaints the changed elements
							b.setBorderPainted(true);
						}
						highlight(middle, middle + (isI ? 1 : -1), true);
						highlight(middle, middle + (isI ? 1 : -1), false); // undo the highlight after the swapping
						if (isI) // sets new middle position
							i++;
						else
							j--;
						middle = isI ? i : j;
					} else // if the element is not suitable for swapping
					{
						if (isI) // move to the next element
							j--; // this loop will continue until the cursor
						else
							i++;
					}
				} // is on pivot position in both directions
				if (high - low < 2) // returns if it is nothing to swap
					return;
				break;
			} // this loop is final for the current subarray
			if (i <= j) { // in case when both elements are not middle
				if (i != j) // they are swapped normally
				{
					highlight(i, j, true); // and highlighted to show it
					temp = values[i];
					values[i] = values[j];
					values[j] = temp;
					buttons.get(i).setText(values[i] + "");
					buttons.get(j).setText(values[j] + "");
					highlight(i, j, false);
				}
				i++;
				j--;
			}
		}
		buttons.get(middle).setBackground(colors[1]); // it is not pivot now
		if (low < j) // calls the recursive method
			quickSort(low, j); // to sort left and right parts as subarrays
		if (high > i)
			quickSort(i, high);
	}

	// The method to highlight the swapped elements with the border and text color.
	private void highlight(int i, int j, boolean on) throws InterruptedException {
		if (!on) // if the highlight is on the off-phase
			Thread.sleep(delay); // calls the delay to display changes before this
		buttons.get(i).setBorderPainted(on); // and shows or hides the border
		buttons.get(j).setBorderPainted(on); // to display their swapping
		if (on) // makes delay to see the result if it is on-phase
			Thread.sleep(delay);
	}

	void cancel(boolean repaintOnCancel) // The method to cancel the animation thread.
	{
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
			if (repaintOnCancel)
				repaintButtons(4, 0, false, true);
		} // repaints all the buttons into their original state
	}

	// The method to change color, border state and enabled state for all buttons.
	private void repaintButtons(int bgc, int fgc, boolean paintBorder, boolean enable) {
		for (JButton jb : buttons) {
			jb.setBackground(colors[bgc]);
			jb.setForeground(colors[fgc]);
			jb.setBorderPainted(paintBorder);
			jb.setEnabled(enable);
		}
	}
}
