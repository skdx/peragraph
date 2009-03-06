package com.peralex.utilities.ui.validatedtextfield;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class SampleCode
{
	
	public static void main(String [] args) {
		
		// standard float range model
		final ValidatedTextField floatRangeField = new ValidatedTextField();
		floatRangeField.setModel(new FloatRangeTextModel(1, 0.3f, 2000f));
		
		// standard int range model
		ValidatedTextField percentageField = new ValidatedTextField();
		percentageField.setModel(new IntRangeTextModel(100, 0, 100));
		
		// adding an action listener to be notified when a value changes
		floatRangeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("new value "+ floatRangeField.getValue());
			}
		});

		// extending utility model SimpleTextField, so we can validate using a date regex.
		ValidatedTextField regexField = new ValidatedTextField();
		regexField.setModel(new SimpleTextModel() {
			private final Pattern p = Pattern.compile("(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])");
			public boolean isValid(String s) {
				return p.matcher(s).matches();
			}
		});
		
		// creating a custom model that only accepts "true" or "false"
		ValidatedTextField booleanField = new ValidatedTextField();
		booleanField.setModel(new ValidatedTextModel() {
			private Boolean value;
			public Object getValue()
			{
				return value;
			}
			/** Is the text value valid? */
			public boolean isValid(String s)
			{
				return "true".equals(s) || "false".equals(s);
			}
			/** Convert an object to text. Throws some kind of RuntimeException if the value is invalid. */
			public String objectToText(Object obj)
			{
				return ((Boolean)obj).toString();
			}
			/** will always be called with a valid value */
			public void setValue(Object obj)
			{
				this.value = (Boolean) value;
			}
			/** Convert text to object. Will always be called with a valid text value. */
			public Object textToObject(String s)
			{
				return Boolean.parseBoolean(s);
			}
		});
		
	}

}
