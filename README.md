# TextToSpeech

In General:

This is a communication software built to assist people with disabilities interact with their environment by clicking a single Button.

the software uses:

1. an Excel sheet filled with tables. each table consists of a topic and 2 columns, in the first column different sub-topics related to
the main topic of the table. in the second column are links to other tables in the excel sheet, that expand the sub-topic into other 
topic related to it (sub-sub topics), for example- a general beggining table can be (I want, I don't want, I feel). by choosing the "I want"
table, the link will connect us to it's expanding table- (to Eat, to Drink, to Sleep,..). choosing "To Eat" will connect us to the
next table (Fruits, Meat, Vegetables,...). and so on, until a full sentence is completed.

2. a folder of audio files- each file corresponds to a relevant topic in one of the tables in the Excel sheet. the name of the audio file
and a topic from a table must be identical.

3. an additional USB sound drive, in order that the user will be able to hear the different topics in headsets, while the topics he chooses
are played through the computer's speaker. for switching between the 2 sound drives we will use an external "hot key" software, configured
that "q" and "r" toggle between the 2 devices (the textToSpeech software will toggle them automaticaly once configured).

4. headsets and speakers

User Guide:

1. a single click by the user automatically starts reading the first table (which can be chosen in the main interface window)
   - the topics are read in groups of 3 (the number can be changed through the settings window) twice. first they are read fast,
     so the user knows what are the topics he can choose from, then another slow iteration is performed, giving the user a few seconds
     for each topic to choose from  (3 by default, can be changed in settings).

2. the user listens in headsets to the different topics played to him according to the table. after a fast introductory round that
   the topics were read to him quickly, another slow round is played, giving the user the possibilty to click when he hears the 
   topic he wants to talk about.

3. after the user had clicked, the topic he chose will be played through the speakers to the environment, and then switch to the 
   expanding table that connects to the chosen topic. 
   a. if a link exists, go back to step 2.
   b. if not, this is the end of the sentence, the sentence (all topics chosen) will be played fully through the speakers.

the software is written in Java.
Additional Libraries Used:

1. Apache POI - Microsoft library for handling the Microsoft Excel files
2. javaFX - for the graphical interface design
3. Javazoom - for playing the MP3 files






