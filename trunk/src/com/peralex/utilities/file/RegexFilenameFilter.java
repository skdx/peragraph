package com.peralex.utilities.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class RegexFilenameFilter implements FilenameFilter
{
	private final Pattern pattern;
	
	public RegexFilenameFilter(String regex)
	{
		this.pattern = Pattern.compile(regex);
	}
	
	public boolean accept(File dir, String name)
	{
		return pattern.matcher(name).matches();
	}

}
