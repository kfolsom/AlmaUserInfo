Purpose:
--------

almauserinfo allows you to retrieve information about an Alma user from
an Alma instance via the Alma Web Services API.  You can retrieve the entire
XML block of information or specify the return of just a 1 or 0 that 
indicates whether the user is "authorized" according to some XPath expression
evaluated against the user information.  This is useful if you want to integrate 
this application into an authorization package such as EZProxy.

Using this application:
-----------------------

The usage info block for the application is:

usage: almauserinfo [options] <username>
 -all               prints full XML block returned by Alma Web Services
                    [default action, overrides other options]
 -authorized        returns 1 if user authorized, 0 if not, -1 if an error
                    occurs
 -config <config>   path and name of config file [default ./config.xml]
 -version           returns version information

Before using the application, make a copy of "config.xml.template" and 
customize it for your site.  By default, the application will look for this
file in the directory from which it is being run.  You can also specify the
config file using the "-config" command line switch.

To run the application, pass the .jar file to java:

  # java -jar almauserinfo.jar

Output is simple text.
