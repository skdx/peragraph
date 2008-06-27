Notes from Noel.


This is my package for managing application preferences. 

It is mostly a wrapper around the java Preferences API. But it makes it much easier to handle :

- saving preferences at the class level

- migrating preferences, for example, if you changed class or package names, it can load the old
preferences, so that customers do not lose any saved prefs.

- storing prefs for various GUI widgets


Example usage:

public class MyMain extends JPanel
{

	private static final AppPreferencesNode prefs = new AppPreferencesNode(MyMain.class);
	
	public MyMain()
	{
		PreferencesLib.addSavePreferencesListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.put("displayResponseTime", displayResponseTimeCheckbox.isSelected());
				prefs.put("ignoreInfoUnits", ignoreInfoUnits.isSelected());
				prefs.put("logDirectory", logDirectoryField.getText());
			}
		});
		displayResponseTimeCheckbox.setSelected(prefs.getBoolean("displayResponseTime", false));
		ignoreInfoUnits.setSelected(prefs.getBoolean("ignoreInfoUnits"));
		logDirectoryField.setText(prefs.get("logDirectory", System.getProperty("user.dir")));
	}
}
