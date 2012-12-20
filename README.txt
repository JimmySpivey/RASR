This Eclipse plugin works to record actions on an SSH terminal connection. These actions are saved as python statements which rely on pexpect. There are 3 eclipse projects versioned. The feature project is needed to contain the plugin for installation (Eclipse prefers to install a feature which contains plugins). And the update site project allows for the feature project to be built as an update site.


The complete and up-to-date project description can be found here at:
http://www.osehra.org/wiki/vista-system-test-platform-project


Current installation instructions:

Prerequisites to fully run the plugin and have it update (add new tests) to the Automated Testing Framework:
-Eclipse 3.7 (Indigo) 32bit
-Modified OSEHRA-Automated-Testing repo (found at: https://github.com/JimDeanSpivey/ATF-RASR). (temporary, will rely on main OSEHRA repo)

Plugin Installation instructions
1) Clone this repository
2) In Eclipse, go to Help -> Install New Software... -> Add..
	Enter any name
	Enter http://eclipse.jcraft.com/ for the Location. Install the JCTerm plugin.
3) On the Install Screen (from step 2), click Add... again
	Click Local...
	Enter any name.
	Select the folder RASR Update Site (from the checked out git repo)
	Click OK
4) Install the plugin 
	Select the local update site (created in step 3)
	(You may have to uncheck Group Items by category for the plugin to show up)
	Ensure "Contact all update sites during install to find required software" is checked.
	Check the RASR Recorder plugin and continue to follow the prompts to complete installation.