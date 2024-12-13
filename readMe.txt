Welcome to team Purahs Word Tracker program.


How to run this program:


1.  Export the entire zipped folder to a destination of you choice.


2.  Navigate inside the folder "cprg304assignment3".


3.  Right click inside this folder and select the option: "Open in Terminal".


4.  Create a text file for the word tracker to read. 


5.  In the command line, enter the following command:

	java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f <output.txt>]



input.txt: the path of the file that will be read and have its words tracked


-pf/-pl/-po: option for how the results will be shown, chose one
-pf: prints all words in alphabetical order, and files in which the words occur
-pl: prints all words, file occurrences, and line numbers
-pf: prints all words, file occurrences, line numbers, and frequency of word occurrence.


-f <output.txt>: The path of the file where the results will be written to.
		 This is optional. If you do not include this, the results will be shown in the console.
		 The output file does not have to exist, it will be automatically created.
	


example:
	java -jar WordTracker.jar "C:\InputFile.txt" -po -f "C:\OutputFile.txt"

