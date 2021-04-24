package sortingApp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class QuickSortFrame extends JFrame{ // The frame for the visual components
    private int values[], amount, lastSize, // array for the random values and its size 
    		lastLow, lastHigh, // positions of the low and high elements
    		d, delay=500, speed=1, // delay and speed of the animation
    		optBtPos=150, x=30, y=50, k; // positions for the buttons on the screen
    private static Color colors[] = new Color[6]; // colors to paint the buttons 
    static final int colorCodes[] = // white, green, cherry, purple, blue, light-green
    	{0xffffff, 0xf5b43, 0x841126, 0x56324e, 0x29519a, 0x1e8c36};  
    static {for (int i=0; i<6; i++) 
    	colors[i] = new Color(colorCodes[i]);}
    private static final Border border = BorderFactory.createLineBorder(colors[2], 3),
    		border2 = BorderFactory.createLineBorder(colors[4], 1);    
    static final String HOWMANY="How many numbers to display?", // informational and
    	    BETWEEN="Please enter a number between 0 and 100.", // warning messages
    	    SPEEDDSCR="<html>Enter speed show sort [1,30] int (default 0.5 s):</html>",
    	    SPEEDBND="<html>Please enter a number between 1 and 30.</html>",
    	    SMALLER="<html>Please select a value smaller or equal to 30.</html>";    
    private boolean  init = true, shift, intrptd, paused; // controlling variables
    private final JTextField fieldAmt = new JTextField(4), enterSpeed = new JTextField(7);
    private final JLabel hm = new JLabel(HOWMANY), bw = new JLabel(BETWEEN),
    		sm = new JLabel(SMALLER), sp = new JLabel(SPEEDDSCR); // info message labels
    private final JButton enterButton = new JButton("Enter"), resetButton = new JButton("Reset"),
    		sortButton = new JButton("Sort"), pauseButton = new JButton("Pause"); // options  		
    private final JComponent optBt[] = {resetButton, sortButton, pauseButton, enterSpeed, sm, sp};
    private ArrayList<JButton> buttons = new ArrayList<>(); // list of the numbered buttons
    private final JPanel enterPage = new JPanel(), sortPage = new JPanel(),  //main, sort screens
    		table = new JPanel(); // the panel to add all the numbered buttons in their order
    private Thread thr; // the thread which performs the animation
    private ActionListener gridLstnr; // the listener to process the button clicking   
    		    	    
    	    QuickSortFrame(String name, int w, int h) // constructor to create the main frame
    	    {super(name); // calls the parent constructor of JFrame to set the name
    	    setSize(1300, 700); // sets the size of the main frame
    	    gridLstnr = (ActionEvent e)-> // sets the handler of the numbered button click
    		{if(Integer.valueOf(((JButton)e.getSource()).getText())>30)
    			{sm.setText(SMALLER); // the value is bigger than 30, sets warning message 
    			sm.setVisible(true);} // and displays it
    		else resetArray(false);}; // else resets all the button values
            setBackground(colors[0]);}
	
    public static void main(String[] args) { // starting point of the program file
    	QuickSortFrame frame = new QuickSortFrame("Swing Quicksort Project", 1300, 700);
    	frame.setAll();} // creates new frame and sets all its parameters
    
    public void setAll() { // sets all the parameters for most of the widgets
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setItem(enterPage, this, 1300, 700, 0, true); // such as size, color,
        setItem(sortPage, this, 1300, 700, 0, false); // visibility and
        setItem(table, sortPage, 130, 700, 0, true); // place to attach
        setItem(hm, enterPage, 300, 20, 500, 200, -1, -1, true, null);     
        setItem(bw, enterPage, 350, 20, 480, 240, -1, -1, false, null);
        setItem(sm, sortPage, 170, 40, optBtPos, 370, -1, -1, false, null);
        setItem(fieldAmt, enterPage, 100, 30, 535, 290, -1, -1, true, null);
        setItem(sp, sortPage, 100, 80, optBtPos, 210, -1, -1, true, null);
        enterSpeed.setHorizontalAlignment(JTextField.CENTER);
        fieldAmt.setHorizontalAlignment(JTextField.CENTER);
        fieldAmt.setBorder(border2);          
        setItem(enterButton, enterPage, 90, 30, 540, 350, 4, 0, true, 
        		(ActionEvent e)->{  // the button to enter the buttons amount      			
        			String input = fieldAmt.getText(); // reads the input text    	
                	if(input!=null) // validation of the entered value
                		if (input.matches("\\d{1,3}"))
                		{int inp = Integer.parseInt(input); 
                		if(inp<=100) //it must be integer value between 0 and 100
                		{bw.setText(""); // not displaying the warning message
                		lastSize=amount;
                		amount = inp; // amount of the numbered buttons
                    	resetArray(true); // resets all the button values
                    	if(enterSpeed.getText().equals("0"))
                    		enterSpeed.setText(speed+"");
                    	enterPage.setVisible(false); // switches from the main screen
                    	sortPage.setVisible(true); // to the sort screen
                    	return;}}
                	    bw.setText(BETWEEN); // if the input is not correct
                	    bw.setVisible(true);}); // shows the warning message                
        setItem(sortButton, sortPage, 100, 40, optBtPos, 45, 5, 0, true, 
        		(ActionEvent e)->{  // button to sort the numbered buttons      			
        			sm.setVisible(false);  // warning message is not needed      			
        			if(paused) //  if the previous thread is suspended
        				resume(); // resume it for canceling
        			if(thr!=null&&thr.isAlive()) // if it is running
        				cancel(); // cancel the previous sorting thread     			
        			intrptd = false; // cancel the interrupted state            	
        			if(values.length<2) // no need to sort the array
            			return; // if its length is less than 2
        			lastLow = 0; // stores the array bounds
        	        lastHigh = amount-1; // to use it for repainting buttons
        	        pauseButton.setEnabled(true); // pause is enabled
        			thr = new Thread(()->{ // this thread performs sorting
        				for(JButton jb : buttons) // paints all buttons green
    						jb.setBackground(colors[1]); // on the start
        			try { quickSort( 0, amount-1); // calls the sorting method
					} catch (InterruptedException e1) {} 
        			for(JButton jb : buttons) // repaints all the buttons blue
						{jb.setBackground(colors[4]); // on finish into their
						jb.setForeground(colors[0]); // original state 
						jb.setBorderPainted(false);} // and hides borders
        			shift=!shift; // changes the order of sorting to opposite
        			pauseButton.setEnabled(false);}); // nothing to suspend on finish 			
        			thr.start(); }); // the thread starts
        setItem(resetButton, sortPage, 100, 40, optBtPos, 100, 5, 0, true, 
        		(ActionEvent e)->{ // the button to reset array with new amount
        			if(paused) // similar actions to cancel the previous thread
        			if(thr!=null&&thr.isAlive())
        				cancel();
        			sortPage.setVisible(false); // switches from the sort screen
                	enterPage.setVisible(true); // to the enter screen
        			sm.setVisible(false); }); // no need for the warning message                
        setItem(enterSpeed, sortPage, 100, 40, optBtPos, 300, -1, -1, true, 
        		(ActionEvent e)->{ // the field to enter the speed for sorting
        			String input = enterSpeed.getText(); // reads it from the input    	
        			if(input!=null) // validation of the entered value
        				if (input.matches("\\d{1,2}"))
        				{int inp=Integer.parseInt(input);
        				if (inp>=0&&inp<=30) // it must be between 1 and 30
                		{sm.setVisible(false); // no need for the warning message
                		if(inp==0)
                			pause();
                		else {speed = inp; // sets the speed of the sorting                		
                			delay = 500/inp; // and its delay depending on speed
                			if(paused)
                				resume();} 
                		return; }}
        			shift = false; });
        setItem(pauseButton, sortPage, 100, 40, optBtPos, 155, 5, 0, true, 
        		(ActionEvent e)->{ // the button to suspend or resume the animation       			        			
                		if(paused) // if the animation is suspended
                			resume(); // calls the method to resume it
                		else pause(); // else suspends it
                		sm.setVisible(false);});          
        pauseButton.setEnabled(false); // the pause button is enabled by default      
        setVisible(true);} // displays the main screen  

	// the method to set all the parameters for the visual component
    void setItem(Component item, Component place, int w, int h, int x, int y, 
			int bgc, int fgc, boolean vis, ActionListener lstnr)
	{if(x>-1)
	item.setLocation(x, y); // sets its coordinates on the screen
	if(fgc>-1)
	item.setForeground(colors[fgc]); // sets its foreground color
	if(lstnr!=null) // sets the listener to handle the button clicking
		if(item instanceof AbstractButton)
		{((AbstractButton)item).addActionListener(lstnr);		
		((AbstractButton)item).setFocusPainted(false);}
		else if (item instanceof JTextField)
			((JTextField)item).addActionListener(lstnr);
	setItem(item, place, w,  h, bgc, vis);} // calls the overloaded method
	
	void setItem(Component item, Component place, int w, int h,  
			int bgc, boolean vis)
	{  item.setSize(w, h);	// sets the pixel size of the element
	if(place!=null) // attaches the element to its place
		((Container)place).add(item);		
		if(item instanceof JPanel) // layout manager is not used
			((JPanel) item).setLayout(null);
		if(bgc>-1) // sets the background color
			item.setBackground(colors[bgc]);
		item.setVisible(vis);} // shows or hides the element on the screen	
	  
	void resetArray(boolean changeSize) // the method to reset the array	
	{ cancel(); // of random values and to update the buttons on screen
		if(init||changeSize) // if the amount of buttons changed
		values = new int [amount]; // changes the array size
	for (int i=0; i<amount; i++) // fills the array with random numbers
		values[i]=(int)(Math.random()*1000)+1; // between 1 and 1000
	int some = (int) (Math.random()*amount); // one of the button values
	if(values[some]>30) // must be equals or less than 30
		values[some]=(int) (Math.random()*30)+1; // reset it in other case
	if(init) // if it is the first launch of the sorting
	buttons = new ArrayList<JButton>(amount); // creating the buttons list
	if(init||changeSize)
	changeButtonsCount(amount); // resets the buttons view on the screen
	init = false;
	for(int i=0; i<amount; i++) // sets the values of the buttons 
		buttons.get(i).setText(values[i]+""); // with the array values
	table.setSize(100*((amount+9)/10)+50, 700); // changes the panel size
	optBtPos=table.getWidth()+30; // offsets the options buttons position 
	for (JComponent c:optBt) // and changes their location to that position
		c.setLocation(optBtPos, c.getY());
		table.setVisible(true);} // displays the buttons panel
	
	void changeButtonsCount(int finalCount) // updates the buttons view on the screen
	{if(finalCount<buttons.size()) // if it need to remove some buttons
{for (int i=lastSize-1; i>=finalCount; i--)
	buttons.get(i).setVisible(false); // hides them from the screen
    k=finalCount%10;} // and remembers the final position
	else for (int i=lastSize; i<finalCount; i++, k++, y+=50) 
	{if(k==10) // in other case when some buttons need to be added
		{k=0; y=50; x+=100;}	
	if(i>=buttons.size()) // if they are not created yet
	{JButton bttn = new JButton(values[i]+""); // creates the new button
	buttons.add(bttn); // and stores it in the button list
	bttn.setBorder(border); // sets border for it,
	bttn.setBorderPainted(false); // which is not painted yet
	setItem(bttn, table, 90, 35, x, y, 4, 0, true, gridLstnr);}
	// sets all the parameters of the button including the click handler
	else buttons.get(i).setVisible(true);}} // shows button if it exists
	
	// the method for performing the quick sorting algorithm 
	public synchronized void quickSort(int low, int high) throws InterruptedException { 
		if(intrptd) // on this step the sorting process interrupts
			return; // if the intrptd variable is set from the outside
		int sG=low, fG=high+1, sB=lastLow, fB=lastHigh+1, sP=0, fP=0; 
   	       boolean within = false, greenMid = false;
   	       if(!(low==lastLow&&high==lastHigh)) {
   	    	   within=(!(low<lastLow^high>lastHigh)); 
   	       if(within) // repaints the processed subarray buttons green
   	       {greenMid=sG>sB||fB>fG; // and not used ones - blue
   	    	   sP=greenMid?sG:sB; 
   	       fP=greenMid?fB:fG;}
   	    	 else if(high>lastHigh) 
   	       {sG=lastHigh+1; fB=low;}
   	       else {fG=lastLow; sB=high+1;}	 	
   	       if(within)
   	    	   { for(int i=(greenMid?sB:sG); i<fP; i++) 
          			{if(i==sP)
       		   {i = greenMid?fG:fB;
       		   if(i>=values.length) break;}
   	    		buttons.get(i).setBackground(greenMid?colors[4]:colors[1]); }}
     else {for(int i=sG; i<fG; i++) 
			buttons.get(i).setBackground(colors[1]);
	 for(int i=sB; i<fB; i++)
			buttons.get(i).setBackground(colors[4]);}}
        lastLow = low; // remembers the current sorting bounds 
        lastHigh = high; // to use them for the further repainting
        if (values.length == 0 || low >= high) 
            return; // returns if it is not possible to divide the array
        wait(delay); // calls the delay to show the subarray for sorting        
        int middle = low + (high - low) / 2; // chooses the pivot element
        int bearing = values[middle]; // the value of the pivot element
        buttons.get(middle).setBackground(colors[3]); // paints the pivot purple
        int i = low, j = high, temp; //iterating starts from this bounds
        boolean isi = false; // shows if the middle element is i or j
        wait(delay); // displays the chosen pivot with the delay
        
        while (i <= j) { // search for the elements that need to swap 
        while ((shift?(values[i] < bearing):(values[i] > bearing))) { 
        		if(intrptd)
        			return;
        		buttons.get(i).setBorderPainted(true);
          	   wait(delay);
          	buttons.get(i).setBorderPainted(false);
          	  i++;} 
           while ((shift?(values[j] > bearing):(values[j] < bearing))) { 
        	   if(intrptd)
       			  return;
        	  buttons.get(j).setBorderPainted(true);
        	  wait(delay);
        	  buttons.get(j).setBorderPainted(false);
        	  j--;}   
           
            	if((isi=i==middle)||(j==middle)) // if it equals to pivot         			
        		{while(i<j) // setting it upper or lower than pivot
        		{if(intrptd)
        			return;
        		if(shift? values[j]<values[i] : values[j]>values[i]) 
        		{highlight(i, j, true); // highlight it and the pivot
        			temp = values[isi?j:i]; 
        			for(int t=isi?j:i; isi?t>i:t<j; t+=(isi?-1:1))  
        // offsets the other elements from the pivot to the moved element place
        			{values[t] = values[isi?t-1:t+1]; // in its direction
        			buttons.get(t).setText(values[t]+"");}
        		values[middle]=temp; // moved element is on the middle position
        		JButton b = buttons.get(middle);
        		b.setText(temp+"");
        		for(int t=1; t<4; t+=2, b = buttons.get(isi?i+1:j-1))
        		{b.setBackground(colors[t]); // repaints the changed elements
        		b.setForeground(colors[2]);
        		b.setBorderPainted(true);}
        		highlight(i, j, false); // undo the highlight after the swapping
        		if(isi) // sets new middle position
        			i++;
        		else j--;
        		middle=isi?i:j;} 
        		else // if the element is not suitable for swapping
        		{if(isi) // move to the next element
        			j--; // this loop will continue until the cursor
        		else i++;}} // is on pivot position in both directions
        		if(high-low<2) // returns if it is nothing to swap
        			return;
        		break;} // this loop is final for the current subarray
            	if (i <= j) { //in case when both elements are not middle            	
                	if(i!=j) // they are swapped normally
            	{ highlight(i, j, true); // and highlighted to show it
            		temp = values[i];
            		values[i] = values[j];
            	    values[j] = temp;
            	    buttons.get(i).setText(values[i]+"");                
            	    buttons.get(j).setText(values[j]+"");
            	    highlight(i, j, false); 
            	    } 
                if(intrptd)
        			return;    
                i++;
                j--;}}			
       buttons.get(middle).setBackground(colors[1]); // it is not pivot now
       if (low < j) // calls the recursive method 
         quickSort( low, j); // to sort left and right parts as subarrays
       if (high > i)
        quickSort(i, high);}
	
	// the method to highlight the swapped elements with the border and text color
	synchronized void highlight(int i, int j, boolean on) throws InterruptedException
	{if(intrptd) // immediately stops the process if it is interrupted
		return; 
		if(!on) // if the highlight is on the off-phase
		wait(delay); // calls the delay to display changes before this
	buttons.get(i).setForeground(colors[on?2:0]); // changes text color
	buttons.get(i).setBorderPainted(on); // and shows or hides the border
	buttons.get(j).setForeground(colors[on?2:0]); // for 2 elements
    buttons.get(j).setBorderPainted(on); // to display their swapping
    if (on) // makes delay to see the result if it is on-phase
    wait(delay); }
	
	void cancel() // the method to cancel the animation thread
	{if(thr!=null&&thr.isAlive()) // if the thread exists and is running
		{intrptd=true; // breaks the performing loop with control variable
	d = delay>500?500:delay; // stores the current delay
	delay = 0; // nullify the delay to make immediate interrupting
	try {thr.join(); // waits for the completing of the thread
		} catch (InterruptedException e2) {}
	delay = d;} // set back the general delay
	intrptd=false;} // the loops can process normally now
	
	void pause() // the method to suspend the animation performing
	{paused=true; // changes state to paused
	d = delay>500?500/speed:delay; // stores the current delay
	delay = 10000000; // sets the extralarge delay to invoke wait until resuming
	pauseButton.setText("Resume");}
	
	synchronized void resume() // the method to resume the animation performing
	{paused=false;// changes state to resumed
	delay = 0; // makes the thread perform immediately after the clicking
	notify(); // interrupts the mutex waiting of the thread
	delay=d; // sets back the normal delay
	pauseButton.setText("Pause");
	if(enterSpeed.getText().equals("0")) // sets normal speed if it was zero
		enterSpeed.setText(speed+"");}
}
