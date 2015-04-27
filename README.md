# HelpDesk 


A simple help desk application. It doesn't look prety.. nor is it 
efficient, but at least it's secure. 

This was more of a learning project than an attempt to make something 
that is actually publishable. 


#### REQUIREMENTS ####
JRE 1.8
Tomcat 7 (or 8, probably)
#### SETUP ####





Deployment instructions: 

1) Drop the war file into your "vanilla" Tomcat 7 webapps folder on your 
Linux machine with Java 8.

2) Put the users.txt folder in the root directory of your Tomcat folder.

3) Edit your server.xml file to enable SSL and change the default port 
8080 to 13200. Use 13201 for SSL. You should have something that looks 
like this: 



<Connector port="13200" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="13201" enableLookups="false" />
  
    <Connector SSLEnabled="true" clientAuth="false" keystoreFile="/path/to/keystore" keystorePass="matthew" maxThreads="150" port="13201" protocol="org.apache.coyote.http11.Http11NioProtocol" scheme="https" secure="true" sslProtocol="TLS"/>


You will have to create your own keystore, I am not supplying one.
Instructions on doing so can be found here: 
https://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html



4)Start Tomcat 7.

5) Browse to http://localhost:13200/HelpDesk, you will be redirected to 
https://localhost:13201/HelpDesk. (obviously you could just browse 
directly to the secure site).

6)USING CHROME OR FIREFOX....
Log in as an anonymous user with a username between 1 and 10 
alphanumeric characters, and fill in the captcha correctly.

Or,

Log in as an authenticated HelpDesk user by clicking the button at the 
bottom of the page and filling in the Username and Password fields.

Usernames, passwords (all case sensitive)

Bob, bob123456789

John, john123456789

Steve, steve123456789
