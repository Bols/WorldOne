# WorldOne, take #1      

This is the framework for a sandbox bio-inspired AI using genes and an evolution algorithm to optimize the outcome of an agent in a 1-dimensional world. This will later be extended to a 2/3-dimensional world when viability has been established. 
*Note*: this project was started in 2018 during christmas holiday. The physics and evolution algorithm part are semi-complete, but the neuron/brain implementation is still in the skeleton stage.

### Main principles
This project simulates a theoretical agent (named oneleg, since it only has a single motion-generating output) that moves along an axis in a virtual 1-dimensional world, and feeds by stopping at certain points where food has been placed. The agent has sensors that are continously updated with distance to food in sync with its motions, and sensors for eating, which should be connected to a reward/reinforcement catalyst similar to dopamine. The brain of this agent is/will be be a spiking neural network that is parameterized using genes that control how neurons form, grow, and communicate, and how they are affected by the reinforcement modulator (dopamine).

* Start with extremely simple reinforcement tasks - starting with human-level-tasks will lead to missing out on finding the principles of general problem-solving behavior  
* Embodied agent - because that is how the only known general intelligence works. The concept of predictive processing is likely a major foundation of the mammalian brains, and requires a constant feed of sensor input that can be correlated with the motor output to create the concept of expecation and surprise
* Evolve the neuron parameters using an indirect approach, where the genes are tied to the neuron population - not to an individual neuron.


### Physics implementation
The virtual world and the neuron implementation is based on an event-driven mechanism where individual spikes are communicated as events between the nodes in the network, rather than having a time-step simulation which is probably more common. An event-based approach will on normal hardware be orders of magnitude faster, especially on networks with sparse spiking activity. The downside is that the neuron membrane potential changes and decay should preferrably be calculated by finding  solutions to the model differential equations rather than simulating the changes over time. 
Parallellizing the event-driven mechanism is also possible, but there are some potential pitfalls regarding timing and look-ahead that may limit the amount of parallellization. This implementation circumvent all of this by running a simulation single-threaded but spawning out all the simulations of the EA population in parallell.

### Evolution algorithm
This was implemented from scratch as I was not happy with the performance and API of the current java-based EA libraries. 


### Neuron/Brain implementation



### Evolution Algorithm overview
1. The evolution algorithm gener
