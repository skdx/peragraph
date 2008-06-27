package com.peralex.utilities;

/**
 * DO NOT CHANGE THE NAME OF THIS CLASS OR THE FORMATS OF THE STRINGS.
 * 
 * The build process updates the strings with real values.
 * 
 * @author Noel Grandin
 */
public final class BuildInfo
{
	/** not meant to be instantiated */
	private BuildInfo() {}
	
	/**
	 * The repository that this was built from
	 */
	public static final String REPOSITORY = "@@REPOSITORY@@";
	
	/**
	 * The revision number from the repository that identifies the checkout that built the code.
	 */
	public static final String REPOSITORY_REVISION = "@@REPOSITORY_REVISION@@";
	
	/**
	 * The version of the build e.g. "V1.00T012"
	 */
	public static final String BUILD_VERSION = "@@BUILD_VERSION@@";
	
	/**
	 * The path that this version was built to 
	 * e.g. "V:\Peralex\Common\MRCM\ifs\ext\EWEquipmentServers\MRR8000\V3_00\Test\T003"
	 */
	public static final String PROJECT_PATH = "@@PROJECT_PATH@@";
	
	/**
	 * The build date of this project.
	 */
	public static final String BUILD_DATE = "@@BUILD_DATE@@";
	
	public static boolean isDevelopmentMode()
	{
		// Note: I don't use an equals check here, or it would produce the wrong answer in production because
		// the contents of the string would also get replaced!.
		return BUILD_VERSION.startsWith("@@BUILD");
	}
}
