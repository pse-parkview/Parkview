# Presentation

## rough guideline

- You should first and foremost present the software you developed - show  some core features and highlights. 
- You should also list your experiences with the software development  process, time planning, tools etc, how faithfully you were able to  implement specification and design. 
- Finally, some stats and stories about your code (how many LOC for which  part, how much coverage, how many commits, which parts were the most  demanding) 
- You can plan for around 15 minutes of presentation plus questions,  though this isn't necessarily a hard limit.
- probably ~15 slides

## sections
- general overview
	- problem description (i want to write high perfomance software but can't keep track)
	- what was the goal
	- requirements
	- quick overview over product
- demo time
	- show every feature (or at least the more visual ones)
	- probably host everything on HEL since schwuppdiwupp is a bitch
	- maybe give listeners link so they can go along as they please

	**( joe presents stuff here )**
- code time
	- statistics (loc, commits, coverage, who has the most coverage, why the backend is so much better than the frontend, why schinken still owes me a beer subscription [plot twist, its because the backend is so much better than the frontend])
	- small overview over system architecture and function
	- go into extensibility (maybe even code a new plot type live)
- ci time
	- explain ci pipeline
- story time
	- few sentences by everyone, can add stories or whatever here
- end
	- why the fuck is it called parkview
	- i hope you enjoyed reading this essay as much as i enjoyed writing it

## content
### General Overivew
#### Continuous Benchmarking
- Problem: Keeping track of software performance
- Solution: Run benchmarks on every (major) change, similar to continuous testing
- How to inspect the results?
#### Parkview to the rescue
- Goal: Create a product that can keep track of previously run benchmark results, visualize them and compare them to each other
- [list the (summarized) requirements here]
- Our solution to the problem: Parkview
### Demo time
[pin down what to demo, we should stick to 15min timeframe since nobody wants to listen to students talking about some shitty webapp for more than 20 mins]
[joe does  whatever joe does, joe does not whatever joe does not]
### code time
#### Statistics
- *X* lines of code
- *Y* percent line coverage
- *Z* commits, *A* issues resolved, *B* pull requests merged
#### Quick'n dirty Code Review
[system model image]
#### Extensibility
- For new plot types only changes to the backend are needed
- Frontend renders all available options and just passes them back to the backend
- Example: [plot that joe didn't do]
### Continuous Integration
#### Pipeline
[image of pipeline]
#### Containerization
- Made development and especially deployment much easier
- People working on the frontend didn't have to bother with compiling the backend or setting up a database and the other way around
- Makes continuous deployment a one line shell script: `docker-compose down && docker-compose pull && docker-compose up -d`
### Our Experience
#### Ma oponion
- I learned a lot about "meta development"
- I learned how to *NOT* write software (waterfall model)
- Leveraging github for cooperation
- I have to continuously refine my work, otherwise I will never be happy with it
#### your uninteresting opionions
### Conclusion
![](https://c.tenor.com/WIqvnT_7Vj8AAAAi/terry-a-davis-terry-davis.gif)