# WorldOne, take #1      

This is the framework for a sandbox bio-inspired AI using genes and an evolution algorithm to optimize the outcome of an agent in a 1-dimensional world. This will later be extended to a 2/3-dimensional world when viability has been established. 
*Note*: this project was started in 2018 during a christmas holiday, and has been actively worked on only in brief periods. The physics and evolution algorithm part are semi-complete, but the neuron/brain implementation is still in the skeleton stage.

### Main principles
This project simulates a theoretical agent (named oneleg, since it only has a single motion-generating output) that moves along an axis in a virtual 1-dimensional world, and feeds by stopping at certain points where food has been placed. The agent has sensors that are continously updated with distance to food in sync with its motions, and sensors for eating, which should be connected to a reward/reinforcement catalyst similar to dopamine. The brain of this agent is/will be be a spiking neural network that is parameterized using genes that control how neurons form, grow, reinforce weights and communicate, and how they are affected by the reinforcement modulator (dopamine).
The motivation for this setup is the following key assumptions about how to explore the concept of biologically inspired intelligence:
* Begin with extremely simple reinforcement tasks - expecting to solve human-level tasks from the start will lead to missing out on finding the principles of general problem-solving behavior  
* Embodied agent - because that is how the only known general intelligence works. The concept of predictive processing seems to be a major foundation of the mammalian brains, and requires a constant feed of sensor input that can be correlated with the motor output and previous experience to create the concept of expecation and surprise
* Evolve the neuron parameters using an indirect approach, where the genes are tied to the neuron population - not to an individual neuron.

### Physics implementation
The virtual world and the neuron implementation is based on an event-driven mechanism where individual spikes are communicated as events between the nodes in the network, rather than having a time-step simulation which is probably more common. An event-based approach will on normal hardware be orders of magnitude faster, especially on networks with sparse spiking activity. The downside is that the neuron membrane potential changes and decay should preferrably be calculated by finding  solutions to the model differential equations rather than simulating the changes over time. 
Parallellizing the event-driven mechanism is also possible, but there are some potential pitfalls regarding timing and look-ahead that may limit the amount of parallellization. This implementation circumvent all of this by running a simulation single-threaded but spawning out all the simulations of the EA population in parallell.

### Evolution algorithm library
This was implemented from scratch as I was not happy with the performance and API of the current java-based EA libraries. 
The gene-class is annotated with gene type and the range of the values. Two individuals can be recombined into one, with a random chance of selecting traits from each parent, and a configurable chance of mutation of each gene.
The evolution algorithm also supports a gradient descent-approach for fine-tuning gene parameters. Given the main mechanism of the scenario simulation, the loss/fitness function is obviously not differentiable with respect to the gene values. So to achieve a similar functionality, the gradient descent will tune each of the gene-values a small amount up or down, and see which way this affects the total fitness, and then move in the direction of total better fitness. This approach is very much dependent on the scenario score being very stable, with no randomization between runs, and may not be appropriate in all cases. 

Currently missing is some sort of mechanism for ensuring that mutated individuals that differ significantly from the leaders are not eradicated by the main winning population, but allowed to evolve and settle into a potentially better solution.

### Neuron/Brain implementation
This part has been barely started, with the chosen approach being hebbian STDP with dopamine as a modulator.

### Evolution process
* Each simulation starts with the same state, and the agent is run until a satisfactory score or scenario time limit is reached. The same individual is then rerun again in exactly the same scenario, but this time keeping the neuronal weights and state from the previous run. This is repeated as long as the individual keeps improving the score. When the score does not improve anymore, this single simulation is finished, and the score of the last run is recorded.
* First random individuals will be created and tested in the simulation until a minimum (100) number of individuals are found that comply with a lower threshold score. Depending on the complexity of the gene model, the complexity of the task, and the selected threshold score this may take some time. The accepted individuals are the first generation
* After this, a new generation is picked by combining the genes of two and two individuals, with the top performers being more likely to procreate. The whole new generation is then simulated, and the best performers are kept as parents for the next generation.

After execution, the algorithm will re-run the winning candidate with a few metrics enabled, and then display a graphical presentation of the agent movement, success and neuronal activity.

![oneworldresults](https://user-images.githubusercontent.com/131504/206002365-76696328-4879-44f2-8523-3134aecda20f.png)
