# JWordle

A simple command-line [Wordle](https://www.powerlanguage.co.uk/wordle/) solver and server. 

The **server** will randomly pick a five-letter word, and give you feedback on your guess, in the form of five 'G', 'Y', 
and 'W' characters, to represent:

 * 'G': Green - This letter is in the right place
 * 'Y': Yellow - This letter is in the wrong place, but is a valid letter in the word.
 * 'W': Wrong - This letter is not in the word

The **solver** attempts to determine which word the server is wanting you to guess. The solver
requires you to type in the word it tells you to type into [Wordle](https://www.powerlanguage.co.uk/wordle/) (or your own 
server instance), and then provide it 
with the response back from the service in the form, again, of 'G', 'Y', and 'W' characters in the places they appear on
the website. The solver will then refine the guidance it gives until it hopefully lands on the correct word, saving your
brain any possibility of being challenged!