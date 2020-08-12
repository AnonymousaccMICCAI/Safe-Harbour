# Safe Harbour Technology

## :thought_balloon: Concept

The App developement method we used for this app is due to the fact that we wanted it to be simple to program and easy to export if needed. So some of the developement choiches we made could be easly implemented in the future.


In order to accomplish what we had in mind we needed:
1) An app developement platform
3) A geolocalization service
2) A database 
3) Data analysis tools

## :computer: Software

For each of the previous tools we developed a strategy to accomplish our goal, we actually finished coding some parts of the app and others are not finished, but we have an idea on how to make all things work, it's just a matter of time and effort.
___
📱 App developement platform


* invision to develope a prototype in order to give an idea of the actual look of the application (you can find it in our repo);
* for the actual software we used android studio (we programmed in java)
* we have set a google log in interaction in the front page so only authorized users can use the app

___
:globe_with_meridians: Geolocalization

* We use the Google maps service with an api key in order to have a map displayed on the user's screen, also the key gives us us the possibility to have all the Google dev functions and tools for geocoding.

* we used android studio functions to display the map

*The idea is that the code will give you the ability to set a pointer on the map that is going to be displayed with others to all users.The marker location will be translated in coordinates,all geopoints corresponding to a specific location will be saved on the database via the firebase method we wrote in our code. (we are not courrently able, in our application, to gain coordinates but we are able to store objects in the database). We are taking example from this code to implement the geolocalization:*   
* we used parts from this code >https://github.com/mitchtabian/Google-Maps-2018


___
:books: Database

* We used Firebase as our database solution because it is simple to implement and it has a lot of functionalities. 
* We managed to extablish a connection between the database and our app in Android Studio, so currently we can send and read data from the database.

*Firebase is a NonSql database this could seem a disadvantage due to the fact that data could be duplicated, but for our application is quite good because it is faster in reading data than a traditional Sql database, and our users are going to read more information than what they are going to write.*


The code works like this:

* the app gets the user imput and sends it to the database as geopoint (in our demo we have a simple variable instead but the principle is the same)
* The database stores it in a document
* If the user requests it via an interaction the database sends the data so the app can display it using google maps tools

All the code is in the section dedicated to the app


___
:chart_with_upwards_trend: Data analysis tool

* For the analythics we wanted to use Firebase because it implements some function from google analythics, and it gives you a powerfull tool that group all the analithics you need in a single place instead of having many software packages. 

* We have set a crushlithics service( we can see if the app crashes multiple times in order to make an update to fix it)

*The main goal in using this tool is to collect data about good or bad reports, we could gather this information adding an analythics counter on the button that saves the information about the review with a boolean attribute. and we could filter this data for a specifical geographic region or for a specific business, this could give us the level of safety of a specifical place, the more user submit their report, the more accurate it gets*
___

## :wrench: Work that needs to be done

* implement qr code reader into our application
* implement invision grafic design code to the real app
* implement other analythical tools for specific interactions
* implement a higher level of security in our database


## :heavy_check_mark: Work that we have done

* log in setup in android studio with google account
* database setup on Firebase
* basic firebase analythics setup in Firebase and android studio
* invision prototype
* implementation of google maps tools
* connection to database via android studio
* a working demo that gives you acces to maps and to the database
