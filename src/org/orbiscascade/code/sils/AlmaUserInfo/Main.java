/*-----------------------------------------------------------------------------
 Class   :  Main.java
 Author  :  Keith Folsom
 Date    :  11/22/13
 Purpose :  This class holds the main() method for the package, which is
            meant to be run interactively from the command line.  It
            parses arguments from the command line and uses them to create
            an instance of the AlmaUserInfo class to retrieve information for
            the given Alma user account.

 Credits :  This class makes use of the Apache Commons CLI library, which is
            licensed for use under the Apache License.  See the included
            LICENSE.txt file for the complete text of this license.

 License :  
 
   Copyright 2013 Orbis Cascade Alliance

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License

 Changes :
-----------------------------------------------------------------------------*/

package org.orbiscascade.code.sils.AlmaUserInfo;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    private static String mVersion = "1.0";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        CommandLine commandLine = null;
        boolean showAll;
        boolean checkAuth;
        String configFile;

        Option all        = new Option("all", "prints full XML block returned by Alma Web Services [default action, overrides other options]");
        Option authorized = new Option("authorized", "returns 1 if user authorized, 0 if not, -1 if an error occurs");

        @SuppressWarnings("static-access")
        Option config     = OptionBuilder.withArgName("config")
                                .hasArg()
                                .withDescription("path and name of config file [default ./config.xml]")
                                .create("config");

        Option help    =    new Option("help", "print this information");
        Option version =    new Option("version", "returns version information");

        Options options = new Options();

        options.addOption(all);
        options.addOption(authorized);
        options.addOption(config);
        options.addOption(help);
        options.addOption(version);

        CommandLineParser parser = new GnuParser();

        try {
            commandLine = parser.parse(options, args);
        } // try
        catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
        } // catch

        if (commandLine.hasOption("all")) {
            showAll = true;
        } // if
        else {
            showAll = false;
        } // else

        if (commandLine.hasOption("authorized")) {
            checkAuth = true;
        } // if
        else {
            checkAuth = false;
        } // else

        if (commandLine.hasOption("config")) {
            configFile = commandLine.getOptionValue("config");
        } // if
        else {
            configFile = "./config.xml";
        } // else

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("almauserinfo [options] <username>", options);
            return;
        } // if

        if (commandLine.hasOption("version")) {
            System.out.println("almauserinfo version " + mVersion);
            System.out.println("Copyright(c) Orbis Cascade Alliance");
            System.out.println("Licensed under the Apache License, Version 2.0");
            return;
        } // if

        File f = new File(configFile);
        if (!f.exists()) {
            System.err.println("*** ERROR:  Configuration file \"" + configFile + "\" doesn't exist.  Exiting...");
            return;
        } // if

        String[] miscArgs = commandLine.getArgs();

        if ((args.length <= 0) || (miscArgs == null) || (miscArgs.length != 1)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("almauserinfo [options] <username>", options);
        } // if
        else {
            AlmaUserInfo userInfo = new AlmaUserInfo(showAll, checkAuth, configFile, miscArgs[0]);
            userInfo.retrieve();
        } // else
    } // main
} // class Main
